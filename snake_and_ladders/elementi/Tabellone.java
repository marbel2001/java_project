package elementi;

import java.util.HashMap;
import java.util.Map;

import gioco.ConfigurazionePartita;


public class Tabellone {
	
	//FATTORE DI SCALA PER GENERARE LE SCALE ED I SERPENTI ALL'INTERNO DEL TABELLONE
	private static final double PARAMETRO = 0.05;
	
    private final ConfigurazionePartita conf;
    private boolean[][] collegamentiSpeciali;
    private final int n, m, numCollegamenti, numGiocatori, numSpeciali;
    private Serpente[] serpenti;
    private Scala[] scale;
    private Giocatore[] giocatori;
    private Map<CasellaSpeciale.Tipo, CasellaSpeciale[]> caselleSpeciali;

    
    
    public Tabellone(ConfigurazionePartita conf) {
        this.conf = conf;
        
        n = conf.getRighe();
        m = conf.getColonne();
        
        numCollegamenti = (int) Math.rint((n*m)*PARAMETRO);
        numGiocatori = conf.getNumGiocatori();
        numSpeciali = numCollegamenti-2;
        
        collegamentiSpeciali = new boolean[n][m];
        
        serpenti = new Serpente[numCollegamenti];
        scale = new Scala[numCollegamenti];
        giocatori = new Giocatore[numGiocatori];
        
        caselleSpeciali = new HashMap<>();
    }
    
    
    public ConfigurazionePartita getConf() {
        return this.conf;
    }


    public int getN() {
        return this.n;
    }


    public int getM() {
        return this.m;
    }


    public int getNumGiocatori() {
        return this.numGiocatori;
    }


    public int getNumCollegamenti() {
        return this.numCollegamenti;
    }


    public Giocatore[] getGiocatori() {
        return this.giocatori;
    }


    public Scala[] getScale() {
        return scale;
    }
    

    public Serpente[] getSerpenti() {
        return this.serpenti;
    }


    public Map<CasellaSpeciale.Tipo, CasellaSpeciale[]> getCaselleSpeciali() {
        return this.caselleSpeciali;
    }


    public void inizializza() {
    	
        for (int i=0 ; i<numGiocatori ; i++) {
            giocatori[i] = new Giocatore(i);
        }
        
        
        Posizione[] arr_pos;
        
        for (int i=0 ; i<numCollegamenti ; i++) {
            arr_pos = generaCollegamento();
            
            Scala sc = new Scala(arr_pos[0], arr_pos[1]);
            scale[i] = sc;
            
            arr_pos = generaCollegamento();
            
            Serpente se = new Serpente(arr_pos[0], arr_pos[1]);
            serpenti[i] = se;
        }
        
        
        if (conf.casellePremio()) {
            CasellaSpeciale.Tipo t = CasellaSpeciale.Tipo.DADI;
            caselleSpeciali.put(t, new CasellaSpeciale[numSpeciali]);
            
            Posizione pos;
            
            for (int i=0 ; i<numSpeciali ; i++) {
                pos = generaCaselleSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t, pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
            
            t = CasellaSpeciale.Tipo.MOLLA;
            caselleSpeciali.put(t, new CasellaSpeciale[numSpeciali]);
            
            for (int i=0 ; i<numSpeciali ; i++) {
                pos = generaCaselleSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t, pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
        }
        
        if (conf.caselleSosta()) {
            CasellaSpeciale.Tipo t = CasellaSpeciale.Tipo.PANCHINA;
            caselleSpeciali.put(t, new CasellaSpeciale[numSpeciali]);
            
            for (int i=0 ; i<numSpeciali ; i++) {
                Posizione pos = generaCaselleSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t, pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
            
            t = CasellaSpeciale.Tipo.LOCANDA;
            caselleSpeciali.put(t, new CasellaSpeciale[numSpeciali]);
            
            for (int i=0 ; i<numSpeciali ; i++) {
                Posizione pos = generaCaselleSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t, pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
        }
        
        if (conf.pescaCarta()) {
            CasellaSpeciale.Tipo t = CasellaSpeciale.Tipo.PESCA;
            caselleSpeciali.put(t, new CasellaSpeciale[numSpeciali]);
            
            for (int i=0 ; i<numSpeciali ; i++) {
                Posizione pos = generaCaselleSpeciali();
                CasellaSpeciale speciale = new CasellaSpeciale(t, pos);
                caselleSpeciali.get(t)[i] = speciale;
            }
        }
    }
    

    private Posizione[] generaCollegamento() {
        boolean flag = false;
        Posizione[] p = new Posizione[2];
        
        while (!flag) {
            int bX = (int) Math.rint(Math.random()*(n-2))+1; //DEVE ESSERE ALMENO 1 (NON PUO' ESSERE LA PRIMA CASELLA)
            int bY = (int) Math.rint(Math.random()*(m-1));
            
            if (!collegamentiSpeciali[bX][bY] && bY!=0) {
            	flag = true;
                collegamentiSpeciali[bX][bY] = true;
                Posizione b = new Posizione(bX ,bY);
                
                boolean flag2 = false;
                
                while (!flag2) {
                    int tX = (int) Math.rint(Math.random()*(n-1));
                    int tY = (int) Math.rint(Math.random()*(m-1));
                    Posizione t = new Posizione(tX, tY);
                    
                    if (!t.equals(b) && (tX<bX) && !collegamentiSpeciali[tX][tY] && (tX!=0 || tY!=0)) {
                    	flag2 = true;
                        collegamentiSpeciali[tX][tY] = true;
                        p[0] = t;
                        p[1] = b;
                    }
                }
            }
        }
        
        return p;
    }

    
    private Posizione generaCaselleSpeciali() {
        Posizione ris = null;
        boolean flag = false;
        
        while (!flag) {
            int x = (int) Math.rint(Math.random()*(n-1));
            int y = (int) Math.rint(Math.random()*(m-1));
            
            if (!(x==0 && y==0) && !(x==m-1 && y==0) && !(x==0 && y==m-1) && !collegamentiSpeciali[x][y]) {
                ris = new Posizione(x, y);
                flag = true;
                collegamentiSpeciali[x][y] = true;
            }
        }
        
        return ris;
    }
    

    public void muovi(int giocatore, int casella, Posizione pos) {
        Giocatore target = null;
        
        for (Giocatore g: giocatori) {
            if (g.getId() == giocatore) {
                target = g;
                break;
            }
        }
        
        if (target==null || pos.getX()>=n || pos.getY()>=m) {
            throw new IllegalArgumentException();
        }
        
        target.setPos(pos);
        target.setCasella(casella);
    }


    public static int cambiaCasella(int casella, int lancio, int ultimaCasella) {
        int nuovaCasella;
        
        int c = casella + lancio;
        if (c < ultimaCasella) {
            nuovaCasella = c;
        } else if (c > ultimaCasella) {
        	//SUPERATA LA FINE SI TORNA INDIETRO
            int offset = ultimaCasella - casella;
            int back = lancio - offset;
            nuovaCasella = ultimaCasella - back;
        } else {
            nuovaCasella = ultimaCasella;
        }
        
        return nuovaCasella;
    }

}
