package elementi;

public class Collegamento {
	
	protected Posizione top, bottom;

	
    public Posizione getTop() {
        return top;
    }
    

    public Posizione getBottom() {
        return bottom;
    }
    

    public String toString() {
        String ris = "[Top = " + top + ", Bottom = " + bottom + "]";
        return ris;
    }

}
