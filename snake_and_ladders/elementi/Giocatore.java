package elementi;

import singleton.Stato;
import singleton.Gioca;


public class Giocatore {
	
	private final int id;
    private Posizione pos;
    private int casella;
    private Stato stato;
    private boolean fermo;
    
    
    public Giocatore(int id) {
        this.id = id;
        pos = new Posizione(0, 0);
        casella = 1;
        stato = Gioca.INSTANCE;
    }

    
    public int getId() {
        return this.id;
    }
    

    public Posizione getPos() {
        return this.pos;
    }

    
    public int getCasella() {
        return this.casella;
    }

    
    public void setPos(Posizione pos) {
        this.pos.setX(pos.getX());
        this.pos.setY(pos.getY());
    }
    

    public Stato getStato() {
        return this.stato;
    }

    
    public void setStato(Stato stato) {
        this.stato = stato;
    }

    
    public boolean isStop() {
        return this.stato.fineSosta(this);
    }

    
    public void setStop(boolean divietoDiSosta) {
        this.fermo = divietoDiSosta;
    }

    
    public boolean sosta() {
        return this.stato.fineSosta(this);
    }

    
    public void setCasella(int casella) {
        this.casella = casella;
    }

}
