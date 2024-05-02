package elementi;


public class Scala extends Collegamento {
	
	
    public Scala(int cimaX, int cimaY, int baseX, int baseY) {
        if (cimaX <= baseX) {
            throw new IllegalArgumentException();
        }
        
        //this.id = id;
        top = new Posizione(cimaX, cimaY);
        bottom = new Posizione(baseX, baseY);
    }
    

    public Scala(Posizione top, Posizione bottom) {
        if (top.getX() >= bottom.getX()) {
            throw new IllegalArgumentException();
        }
        this.top = new Posizione(top.getX(), top.getY());
        this.bottom = new Posizione(bottom.getX(), bottom.getY());
    }   
    


}
