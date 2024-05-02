package elementi;

public class Serpente extends Collegamento {
	
	
    public Serpente(int testaX, int testaY, int codaX, int codaY) {
        if (testaX <= codaX) {
            throw new IllegalArgumentException();
        }
        
        top = new Posizione(testaX, testaY);
        bottom = new Posizione(codaX, codaY);
    }
    

    public Serpente(Posizione top, Posizione bottom) {
        if (top.getX() >= bottom.getX()) {
            throw new IllegalArgumentException();
        }
        
        this.top = new Posizione(top.getX(), top.getY());
        this.bottom = new Posizione(bottom.getX(), bottom.getY());
    }
    
}
