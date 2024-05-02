package gioco;


public class ConfigurazionePartita {
	
	private final int numGiocatori;
    private final int righe;
    private final int colonne;
    private final boolean dadoSingolo;
    private final boolean lancioUnico;
    private final boolean doppioSei;
    private final boolean caselleSosta;
    private final boolean casellePremio;
    private final boolean pescaCarta;
    private final boolean ulterioriCarte;
    private final boolean salvaStorico;
    private final boolean automatico;

    
    public ConfigurazionePartita(int numGiocatori, int righe, int colonne, boolean dadoSingolo, boolean lancioUnico,
                          		 boolean doppioSei, boolean caselleSosta, boolean casellePremio, boolean pescaCarta,
                          		 boolean ulterioriCarte, boolean salvaStorico, boolean automatico)
    {
        this.numGiocatori = numGiocatori;
        this.righe = righe;
        this.colonne = colonne;
        this.dadoSingolo = dadoSingolo;
        this.lancioUnico = lancioUnico;
        this.doppioSei = doppioSei;
        this.caselleSosta = caselleSosta;
        this.casellePremio = casellePremio;
        this.pescaCarta = pescaCarta;
        this.ulterioriCarte = ulterioriCarte;
        this.salvaStorico = salvaStorico;
        this.automatico = automatico;
    }
    

    public int getNumGiocatori() {
        return this.numGiocatori;
    }
    

    public int getRighe() {
        return this.righe;
    }
    

    public int getColonne() {
        return this.colonne;
    }

    
    public boolean dadoSingolo() {
        return this.dadoSingolo;
    }
    

    public boolean lancioUnico() {
        return this.lancioUnico;
    }
    

    public boolean doppioSei() {
        return this.doppioSei;
    }
    

    public boolean caselleSosta() {
        return this.caselleSosta;
    }
    

    public boolean casellePremio() {
        return this.casellePremio;
    }
    

    public boolean pescaCarta() {
        return pescaCarta;
    }
    

    public boolean ulterioriCarte() {
        return ulterioriCarte;
    }
    
    
    public boolean salvaStorico() {
        return salvaStorico;
    }
    

    public boolean isAuto() {
        return automatico;
    }
    

    @Override
    public String toString() {
        return "Configurazione partita {\n" +
                "numGiocatori: " + numGiocatori + "; \n" +
                "righe: " + righe + "; \n" +
                "colonne: " + colonne + ", \n" +
                "dadoSingolo: " + dadoSingolo + "; \n" +
                "lancioUnico: " + lancioUnico + "; \n" +
                "doppioSei: " + doppioSei + "; \n" +
                "caselleSosta: " + caselleSosta + "; \n" +
                "casellePremio: " + casellePremio + "; \n" +
                "pescaCarta: " + pescaCarta + "; \n" +
                "ulterioriCarte: " + ulterioriCarte + "; \n" +
                "salvaStorico: " + salvaStorico + "; \n" +
                "automatico: " + automatico + "; \n" +
                '}';
    }

}
