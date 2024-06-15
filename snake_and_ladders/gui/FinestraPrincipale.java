package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gioco.*;
import elementi.*;
import observer.*;
import singleton.*;


public class FinestraPrincipale {
	
	enum Colore {GIALLO, BIANCO, ROSSO, BLU, VERDE, ARANCIO, VIOLA, CIANO, ROSA, NERO, GRIGIO}
    private static final Colore[] COLOR_ROTATION = {Colore.GIALLO, Colore.BIANCO, Colore.ROSSO, Colore.BLU, Colore.VERDE};
    private JFrame finestra;
    private Casella[] caselle;
    private ScalaGUI[] scale;
    private SerpenteGUI[] serpenti;
    private ArrayList<PedinaGUI> pedine;
    private final Tabellone tabellone;
    private final int n;
    private final int m;
    private LabelButtonObserver dado;
    private Semaphore mutex;
    
    //PER LE ULTERIORI CARTE
    private Map<Integer, Integer> divietoDiSosta;
    private LinkedList<CasellaSpeciale.Tipo> listaCarte;
    

    public FinestraPrincipale(Tabellone tabellone, LabelButtonObserver dado, Semaphore mutex) {
        this.tabellone = tabellone;
        n = tabellone.getN();
        m = tabellone.getM();
        this.dado = dado;
        this.mutex = mutex;
        divietoDiSosta = new HashMap<>();
        
        listaCarte = new LinkedList<>();
        for (CasellaSpeciale.Tipo c: CasellaSpeciale.Tipo.values()) {
        	if (!c.equals(CasellaSpeciale.Tipo.PESCA)) {
        		if (c.equals(CasellaSpeciale.Tipo.DIVIETO) && tabellone.getConf().ulterioriCarte()) {
        			listaCarte.add(c);
        		} else if (!c.equals(CasellaSpeciale.Tipo.DIVIETO)) {
        			listaCarte.add(c);
        		}
        	}
        }
    }
        

    public void inizializza() {
    	
    	//DIMENSIONI DELLO SCHERMO
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	int PIXEL_X = (int) Math.round(screenSize.getWidth());
    	int PIXEL_Y = (int) Math.round(screenSize.getHeight());
    	
    	
    	//DIMENSIONE DELLA FINESTRA
    	int HEIGHT = 650;
    	int WIDTH = 585;
    	
    	//POSIZIONE DELLA FINESTRA
    	int x = (PIXEL_X/2) - (WIDTH/2);
    	int y = (PIXEL_Y/2) - (HEIGHT/2);
    	
    	
    	//DEFINIAMO LA FINESTRA
    	String titolo = "Scale e Serpenti";
        finestra = new JFrame();
        finestra.setTitle(titolo);
        finestra.setSize(WIDTH, HEIGHT);
        finestra.setMaximumSize(screenSize);
		finestra.setLocation(x, y);
		finestra.setResizable(false);
        finestra.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        finestra.setLayout(new BorderLayout());

        finestra.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String msg = "La partita corrente verrà persa per sempre.";
                String title = "Sicuro di voler uscire?";
                if (JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });

        JMenuBar menu = new JMenuBar();

        JMenu help = new JMenu("Aiuto");

        JMenuItem cosaFare = new JMenuItem("Guida per giocare");
        cosaFare.addActionListener(e -> {
            String msg = "Scale e Serpenti.\n";
            msg += "\nGuardando in basso vedrai in ordine:\n";
            msg += " - il turno del giocatore corrente\n";
            msg += " - il pulsante per poter tirare " + (tabellone.getConf().dadoSingolo()? "il dado": "i dadi") + ";\n";
            msg += " - il numero di caselle che il giocatore precedente ha percorso con l'ultimo tiro.\n";
            msg += "\nElementi del tabellone di gioco:\n";
            msg += " - i tubi verdi rappresentano le scale;\n";
            msg += " - i tubi rossi rappresentano i serpenti.\n";
            msg += "\nSe hai selezionato le rispettive scelte nella configurazione, su alcune caselle vedrai delle scritte:\n";
            msg += " - PAN (panchina);\n";
            msg += " - LOC (locanda);\n";
            msg += " - DAD (dadi);\n";
            msg += " - MOL (molla);\n";
            msg += " - PES (pesca).\n";
            msg += "\nBuona partita!";
            JOptionPane.showMessageDialog(finestra, msg);
        });

        JMenuItem about = new JMenuItem("Info");
        about.addActionListener(e -> {
            String msg = "Autore del progetto: Marco Belvedere." + "\n" + "Grazie per aver giocato!";
            JOptionPane.showMessageDialog(finestra, msg);
        });
        
        JMenuItem options = new JMenu("Opzioni");
        JMenuItem esci = new JMenuItem("Esci");
        esci.addActionListener(e -> {
            finestra.dispatchEvent(new WindowEvent(finestra, WindowEvent.WINDOW_CLOSING));
        });
        
        options.add(esci);

        help.add(cosaFare);
        help.add(about);
        
        menu.add(options);
        menu.add(help);
        
        //POSIZIONE 0 VUOTA PER CORRISPONDENZA DI INDICI
        caselle = new Casella[(n*m)+1];
        
        pedine = new ArrayList<>(tabellone.getNumGiocatori());
        scale = new ScalaGUI[tabellone.getNumCollegamenti()];
        serpenti = new SerpenteGUI[tabellone.getNumCollegamenti()];

        JLayeredPane center = new JLayeredPane();
        
        //GENERAZIONE DEL TABELLONE DI GIOCO
        JPanel grid = new JPanel(new GridLayout(n, m));
        grid.setSize(585, 520);
        generateGameBoard(grid);

        //GENERAZIONE DI SCALE E SERPENTI NEL TABELLONE
        generateSS(center);
        center.add(grid);

        Label turnoDi = new Label("Turno del giocatore G1");
        Label estrazione = new Label("Numero estratto:");

        dado.setLabels(new Label[] {turnoDi, estrazione});
        dado.getSubject().addActionListener(e -> {
            dado.notifica();
            mutex.release();
        });

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
                
        JPanel innerSouth1 = new JPanel(new FlowLayout());
        JPanel innerSouth2 = new JPanel(new FlowLayout());
        innerSouth1.add(turnoDi);
        innerSouth2.add(estrazione);
        
        south.add(innerSouth1);
        south.add(innerSouth2);
        south.add(dado.getSubject());
        
                
        finestra.add(menu, BorderLayout.NORTH);
        finestra.add(center, BorderLayout.CENTER);
        finestra.add(south, BorderLayout.SOUTH);
        finestra.setVisible(true);
    }
    

    private void generateGameBoard(JPanel grid) {
        int id = n*m;
        
        if ((n-1)%2 == 0) {
            id -= m-1;
        }
        
        //PER AVERE UNA MIGLIORE ALTERNANZA DEI COLORI
        int k = -3;
        
        for (int i=0 ; i<n ; i++) {
            k = (k+3) % COLOR_ROTATION.length;
            boolean verso;
            
            //SCELGO IL VERSO DELLA RIGA: TRUE (-->), FALSE (<--)
            if (n%2 == 0) {
            	if (i%2 != 0) {
            		verso = true;
            	} else {
            		verso = false;
            	}
            } else {
            	if (i%2 == 0) {
            		verso = true;
            	} else {
            		verso = false;
            	}
            }
            
            for (int j=0 ; j<m ; j++) {
                Casella casella;
                
                if (verso) {
                    casella = new Casella(id++, COLOR_ROTATION[k], new Posizione(i, j), tabellone, pedine);
                } else {
                    casella = new Casella(id--, COLOR_ROTATION[k], new Posizione(i, j), tabellone, pedine);
                }
                
                caselle[casella.getId()] = casella;
                grid.add(casella);
                k++;
                
                if (k >= COLOR_ROTATION.length) {
                    k = 0;
                }
            }
            
            //PER COMINCIARE A NUMERARE LA RIGA SOTTOSTANTE
            if (!verso) {
                id -= m-1;
            } else {
                id -= m+1;
            }
        }
    }

    
    private void generateSS(JLayeredPane div) {
        Scala[] scale = tabellone.getScale();
        
        for (int i=0 ; i<tabellone.getNumCollegamenti() ; i++) {
            Scala s = scale[i];
            Casella top = find(s.getTop());
            Casella bottom = find(s.getBottom());
            ScalaGUI l = new ScalaGUI(top, bottom);
            this.scale[i] = l;
            div.add(l);
        }

        Serpente[] serpenti = tabellone.getSerpenti();
        
        for (int i=0 ; i<tabellone.getNumCollegamenti() ; i++) {
            Serpente s = serpenti[i];
            Casella top = find(s.getTop());
            Casella bottom = find(s.getBottom());
            SerpenteGUI sn = new SerpenteGUI(top, bottom);
            this.serpenti[i] = sn;
            div.add(sn);
        }
    }
    

    private Casella find(Posizione pos) {
        for (int i=1 ; i<caselle.length ; i++) {
            Casella c = caselle[i];
            if (c.getPos().equals(pos)) {
                return c;
            }
        }
        
        return null;
    }

    
    public void move(int giocatore, int casella, boolean finish, int lancio) {
    	dado.getSubject().setEnabled(false);
    	Giocatore g = tabellone.getGiocatori()[giocatore];
        
        if (g.getCasella() == casella) {
        	dado.getSubject().setEnabled(true);
            return;
        }

        Casella c = caselle[casella];
        PedinaGUI p = pedine.get(giocatore);

        Casella old = p.getParent(); 
        old.remove(p);
        
        int casellaCorr = g.getCasella();
        
        if (!finish) {
        	int posizioni = casella - casellaCorr;
        	for (int i=0 ; i<posizioni ; i++) {
            	casellaCorr += 1;
            	moveInner(giocatore, casellaCorr);

            	try {
            		Thread.sleep(500);
            	} catch (InterruptedException exception) {
            		exception.printStackTrace();
            	}
            }
        } else {
        	int ultimaCasella = n*m;
        	int offset = ultimaCasella - casellaCorr;
            int fallback = lancio - offset;
            
            for (int i=0 ; i<offset ; i++) {
            	casellaCorr += 1;
            	moveInner(giocatore, casellaCorr);

            	try {
            		Thread.sleep(500);
            	} catch (InterruptedException exception) {
            		exception.printStackTrace();
            	}
            }
            
            for (int i=0 ; i<fallback ; i++) {
            	casellaCorr -= 1;
            	moveInner(giocatore, casellaCorr);

            	try {
            		Thread.sleep(500);
            	} catch (InterruptedException exception) {
            		exception.printStackTrace();
            	}
            }
        }
        
        
        
        //NUOVA POSIZIONE
        Posizione pos = c.getPos();

        tabellone.muovi(giocatore, c.getId(), pos);

        controlloScala(giocatore, pos);
        controlloSerpente(giocatore, pos);

        controlloPanchina(giocatore, pos);
        controlloLocanda(giocatore, pos);
        controlloDadi(giocatore, pos);
        controlloMolla(giocatore, old.getPos());
        controllaPesca(giocatore, pos, old.getPos());
        
        dado.getSubject().setEnabled(true);
    }
    
    
    private void moveInner(int giocatore, int casella) {        
    	Casella c = caselle[casella];
        PedinaGUI p = pedine.get(giocatore);
        
        //PER QUANDO CI SONO SCALE E SERPENTI
        tabellone.muovi(giocatore, casella, c.getPos());

        Casella old = p.getParent(); 
        old.remove(p);
        
        //AGGIORNO LA CASELLA DELLA PEDINA
        p.setParent(c);
        //AGGIUNGO ALLA CASELLA LA PEDINA
        c.add(p);
        
        //PER AGGIORNARE IL CONTENUTO DELLA CASELLA RIMUOVO E RIGENERO TUTTO
        JLabel num = (JLabel) old.getComponent(0);
        //TOLGO L'ETICHETTA
        old.remove(0);
        Component[] components = old.getComponents();
        old.removeAll();
        //POICHE' L'ETICHETTA NON VA MESSA AL CENTRO
        old.add(num, BorderLayout.SOUTH, 0);
        for (Component component: components) {
            old.add(component, BorderLayout.CENTER);
        }
        
        //REPAINT DELLA VECCHIA CASELLA
        old.repaint();
        old.revalidate(); 
        
        //REPAINT DELLA NUOVA CASELLA
        c.repaint();
        c.revalidate(); 

        for (int i=0 ; i<tabellone.getNumCollegamenti() ; i++) {
            scale[i].repaint();
            serpenti[i].repaint();
        }
        
        for (PedinaGUI pe: pedine) {
            pe.repaint();
        }
    }
    

    private void controlloScala(int giocatore, Posizione pos) {
        for (Scala s: tabellone.getScale()) {
        	//CONTROLLO SE LA CASELLA (POS) CONTIENE I PIEDI DI UNA SCALA
            if (s.getBottom().equals(pos)) {
                System.out.println("SCALA!");
                for (int i=1 ; i<(n*m)+1 ; i++) {
                	//CERCO LA CASELLA CHE OSPITA LA CIMA DELLA SCALA
                    Casella nuova = caselle[i];
                    if (s.getTop().equals(nuova.getPos())) {
                    	moveInner(giocatore, nuova.getId());
                    }
                }
            }
        }
    }

    
    private void controlloSerpente(int giocatore, Posizione pos) {
        for (Serpente s: tabellone.getSerpenti()) {
            if (s.getTop().equals(pos)) {
            	//CONTROLLO SE LA CASELLA (POS) CONTIENE LA TESTA DI UN SERPENTE
                System.out.println("SERPENTE!");
                for (int i=1 ; i<(n*m)+1 ; i++) {
                	//CERCO LA CAELLA CHE OSPITA LA CODA DEL SERPENTE
                    Casella nuova = caselle[i];
                    if (s.getBottom().equals(nuova.getPos())) {
                    	moveInner(giocatore, nuova.getId());
                    }
                }
            }
        }
    }

    
    private void controlloPanchina(int giocatore, Posizione pos) {
        if (tabellone.getConf().caselleSosta()) {
        	
        	String msg;
        	String title;
        	
        	if(tabellone.getConf().ulterioriCarte()) {
        		if (divietoDiSosta.get(giocatore) != null) {
            		divietoDiSosta.remove(giocatore);
            		
            		msg = "Il giocatore G" + (giocatore + 1) + " usa la carta DIVIETO DI SOSTA.";
                    title = "Panchina";
                    JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                    System.out.println(msg);
                    

                    listaCarte.add(CasellaSpeciale.Tipo.DIVIETO);
            		return;
            	}
        	}
        	
            CasellaSpeciale[] panchine = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.PANCHINA);
            
            for (CasellaSpeciale p:panchine) {
                if (p.getPos().equals(pos)) {
                    Giocatore g = tabellone.getGiocatori()[giocatore];
                    if (!(g.getStato() instanceof Panchina)) {
                    	//AGGIORNO LO STATO SE LA PEDINA E' APPENA ARRIVATA SULLA CASELLA
                        g.setStato(Panchina.INSTANCE);
                        msg = "Il giocatore G" + (giocatore+1) + " è finito su una panchina";
                        title = "Panchina";
                        JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                        System.out.println(msg);
                    }
                    break;
                }
            }
        }
    }
    

    private void controlloLocanda(int giocatore, Posizione pos) {
        if (tabellone.getConf().caselleSosta()) {
        	
        	String msg;
        	String title;
        	
        	if(tabellone.getConf().ulterioriCarte()) {
        		if (divietoDiSosta.get(giocatore) != null) {
            		divietoDiSosta.remove(giocatore);
            		
            		msg = "Il giocatore G" + (giocatore + 1) + " usa la carta DIVIETO DI SOSTA.";
                    title = "Locanda";
                    JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                    System.out.println(msg);
                    

                    listaCarte.add(CasellaSpeciale.Tipo.DIVIETO);
            		return;
            	}
        	}
        	
            CasellaSpeciale[] locande = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.LOCANDA);
            
            for (CasellaSpeciale l: locande) {
                if (l.getPos().equals(pos)) {
                    Giocatore g = tabellone.getGiocatori()[giocatore];
                    if (!(g.getStato() instanceof Locanda)) {
                    	//AGGIORNO LO STATO SE LA PEDINA E' APPENA ARRIVATA SULLA CASELLA
                        g.setStato(new Locanda());
                        msg = "Il giocatore G" + (giocatore+1) + " è finito su una locanda";
                        title = "Panchina";
                        JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                        System.out.println(msg);
                    }
                    break;
                }
            }
        }
    }
    

    private void controlloDadi(int giocatore, Posizione pos) {
        if (tabellone.getConf().casellePremio()) {
            CasellaSpeciale[] dadi = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.DADI);
            for (CasellaSpeciale d: dadi) {
                if (d.getPos().equals(pos)) {
                	//IL GIOCATORE SI TROVA SU UNA CASELLA DADI
                    actionDadi(giocatore);
                }
            }
        }
    }

    
    private void actionDadi(int giocatore) {
        Giocatore g = tabellone.getGiocatori()[giocatore];
        int lancio = lanciaDadi(g);
        int nuova = Tabellone.cambiaCasella(g.getCasella(),lancio,n*m);
        System.out.println("DADI! Il giocatore G" + (giocatore+1) + " avanza di " + lancio + " caselle");
        
        boolean finish = false;
        int posCorr = g.getCasella();
        int calcolo = posCorr + lancio;
        if (calcolo > n*m) {
        	finish = true;
        }
        
        if (tabellone.getConf().doppioSei() && lancio == 12) {            
            move(g.getId(), nuova, finish, lancio);
            
            String msg = "Il giocatore G" + (g.getId()+1) + " ha ottenuto un doppio 6!";
        	String title = "DOPPIO 6!";
        	JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
        	
            lancio = lanciaDadi(g);
            nuova = Tabellone.cambiaCasella(g.getCasella(), lancio, n*m);
            
            posCorr = g.getCasella();
            calcolo = posCorr + lancio;
            if (calcolo > n*m) {
            	finish = true;
            }
        }
        
        move(g.getId(), nuova, finish, lancio);
    }

    
    private int lanciaDadi(Giocatore g) {
        int casella = g.getCasella();
        int ultimaCasella = n*m;
        
        //GENERAZIONE DI UN NUMERO CASUALE
        int lancio = ((int) Math.rint((Math.random() * 5))) + 1; //+1 POICHE' RANDOM() GENERA UN NUMERO CASUALE TRA 0.0 ED 1.0
        
        if ((!tabellone.getConf().dadoSingolo() && (casella<(ultimaCasella-6)) || !tabellone.getConf().lancioUnico())) {
                lancio += ((int) Math.rint((Math.random() * 5))) + 1; //+1 POICHE' RANDOM() GENERA UN NUMERO CASUALE TRA 0.0 ED 1.0
        }
        
        return lancio;
    }
    

    private void controlloMolla(int giocatore, Posizione vecchiaPos) {
        if (tabellone.getConf().casellePremio()) {
            CasellaSpeciale[] molle = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.MOLLA);
            
            for (CasellaSpeciale m: molle) {
                Giocatore g = tabellone.getGiocatori()[giocatore];
                
                if (m.getPos().equals(g.getPos())) {
                	//IL GIOCATORE SI TROVA SU UNA CASELLA MOLLA
                    actionMolla(giocatore, vecchiaPos);
                }
                
            }
        
        }
    }

    
    private void actionMolla(int giocatore, Posizione vecchiaPos) {
        Giocatore g = tabellone.getGiocatori()[giocatore];
        
        for (int i=1 ; i<caselle.length ; i++) {
            Casella casella = caselle[i];
            
            if (casella.getPos().equals(vecchiaPos)) {
                int distanza = Math.abs(g.getCasella()-casella.getId());
                int nuova = g.getCasella() + distanza;
                int ultimaCasella = n*m;
                
                boolean finish = false;
                
                if (nuova > ultimaCasella) {
                    int offset = ultimaCasella - nuova;
                    int fallback = distanza - offset;
                    nuova = ultimaCasella - fallback;
                    
                    finish = true;
                }
                
                System.out.println("MOLLA! Il giocatore G" + (giocatore+1) + " avanza di " + distanza + " posizioni");
                move(g.getId(), nuova, finish, distanza);
            }
            
        }
    }

    
    private void controllaPesca(int giocatore, Posizione pos, Posizione vecchiaPos) {
        if (tabellone.getConf().pescaCarta()) {
            CasellaSpeciale[] pescaCarte = tabellone.getCaselleSpeciali().get(CasellaSpeciale.Tipo.PESCA);
            
            for (CasellaSpeciale p: pescaCarte) {
                if (p.getPos().equals(pos)) {
                	//IL GIOCATORE E' FINITO SULLA CASELLA PESCA
                    Giocatore g = tabellone.getGiocatori()[giocatore];
                    
                    CasellaSpeciale.Tipo t = listaCarte.removeFirst();
                    String msg;
                    String title;
                    switch (t) {
                        case PANCHINA:
                            msg = "Il giocatore G" + (giocatore + 1) + " ha pescato: PANCHINA";
                            title = "Panchina";
                            JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                            System.out.println(msg);
                            
                            g.setStato(Panchina.INSTANCE);

                            listaCarte.add(t);
                            break;
                            
                        case LOCANDA:
                            msg = "Il giocatore G" + (giocatore + 1) + " ha pescato: LOCANDA";
                            title = "Locanda";
                            JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                            System.out.println(msg);
                            
                            g.setStato(new Locanda());

                            listaCarte.add(t);
                            break;
                            
                        case DADI:
                            msg = "Il giocatore G" + (giocatore + 1) + " ha pescato: DADI";
                            title = "Dadi";
                            JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                            System.out.println(msg);
                            actionDadi(giocatore);

                            listaCarte.add(t);
                            break;
                            
                        case MOLLA:
                        	msg = "Il giocatore G" + (giocatore + 1) + " ha pescato: MOLLA";
                            title = "Molla";
                            JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                            System.out.println(msg);
                            
                            actionMolla(giocatore, vecchiaPos);

                            listaCarte.add(t);
                            break;
                            
                        case DIVIETO:
                        	//SE E' FERMO LA SUA SOSTA VIENE INTERROTTA
                        	msg = "Il giocatore G" + (giocatore + 1) + " ha pescato: DIVIETO DI SOSTA";
                        	
                        	if (g.isStop()) {
                        		g.setStop(false);
                        	} else {
                        		//AGGIUNTA DEL GIOCATORE ALL'ELENCO DIVIETO DI SOSTA
                        		int tmp = 1;
                        		divietoDiSosta.put(giocatore, tmp);
                        		
                        		title = "DIVIETO DI SOSTA";
                                JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
                                System.out.println(msg);
                        	}
                            
                            System.out.println(msg);
                            break;
                        default: 
                        	//NON CI SI PUO' TROVARE IN ALTRI CASI OLTRE QUELLI SPECIFICATI SOPRA
                        	throw new IllegalStateException();
                    }
                }
            }
        }
    }
    

    public void prossimoGiocatore(String[] newLables) {
    	//PER INFORMARE AI GIOCATORI A CHI TOCCA GIOCARE (TURNO DEL GIOCATORE ...)
        Label[] labels = dado.getLabels();
        System.out.println(newLables[0] + " - " + newLables[1]);
        labels[0].setText(newLables[0]);
        labels[1].setText(newLables[1]);
        dado.notifica();
    }
    

    public void mostraVincitore(Giocatore g) {
        dado.getSubject().setEnabled(false);
        String msg = "Il giocatore G" + (g.getId()+1) + " ha vinto!";
        String title = "C'è un vincitore!";
        JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
        finestra.dispose();
        System.exit(0);
    }
    
    
    public void showDialog(String title, String msg) {
    	JOptionPane.showConfirmDialog(finestra, msg, title, JOptionPane.DEFAULT_OPTION);
    }



}
