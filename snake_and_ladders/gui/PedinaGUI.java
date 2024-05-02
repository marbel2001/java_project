package gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;


public class PedinaGUI extends JPanel {
	
	private static final FinestraPrincipale.Colore[] PEDINE = {
			FinestraPrincipale.Colore.GIALLO, 
			FinestraPrincipale.Colore.BIANCO, 
			FinestraPrincipale.Colore.ROSSO, 
			FinestraPrincipale.Colore.BLU, 
			FinestraPrincipale.Colore.VERDE,
            FinestraPrincipale.Colore.ARANCIO, 
            FinestraPrincipale.Colore.VIOLA, 
            FinestraPrincipale.Colore.CIANO, 
            FinestraPrincipale.Colore.ROSA,
            FinestraPrincipale.Colore.NERO, 
            FinestraPrincipale.Colore.GRIGIO
    };
	
	private final int id;
    private FinestraPrincipale.Casella parent;
    private String text;

    
    public PedinaGUI(int id, FinestraPrincipale.Casella parent, int x, int y, int w, int h) {
        this.id = id;
        this.parent = parent;
        setBounds(x, y, w, h);
        setBackground(parent.getBackground());
    }

    
    public PedinaGUI(int id, FinestraPrincipale.Casella parent, String text, int x, int y, int w, int h) {
        this.id = id;
        this.parent = parent;
        this.text = text;
        setBounds(getX(), getY(), getSize().width, getSize().height);
    }

    
    public int getId() {
        return this.id;
    }
    

    public FinestraPrincipale.Casella getParent() {
        return this.parent;
    }

    public void setParent(FinestraPrincipale.Casella parent) {
        this.parent = parent;
    }

    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setBackground(parent.getBackground());
        Color colore = null;
        
        switch (PEDINE[id]) {
	        case GIALLO: colore = Color.YELLOW; break;
	        case BIANCO: colore = Color.WHITE; break;
	        case ROSSO: colore = Color.RED; break;
	        case BLU: colore = Color.BLUE; break;
	        case VERDE: colore = Color.GREEN; break;
	        case ARANCIO: colore = Color.ORANGE; break;
	        case VIOLA: colore = Color.MAGENTA; break;
	        case CIANO: colore = Color.CYAN; break;
	        case ROSA: colore = Color.PINK; break;
	        case NERO: colore = Color.BLACK; break;
	        case GRIGIO: colore = Color.DARK_GRAY; break;
	        default: colore = Color.LIGHT_GRAY;
	    }        
        
        g.setColor(colore);
        g.fillOval(getX(), getY(), getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawOval(getX(), getY(), getWidth(), getHeight());
        
        
        if (text != null) {
        	//PER PUNTARE AL CENTRO DELLA PEDINA IL TESTO
        	int centerX = getSize().width/2;
        	int centerY = getSize().height/2;
        	FontMetrics fontMetrics = g.getFontMetrics();
        	int textWidth = fontMetrics.stringWidth(text);
            int textHeight = fontMetrics.getHeight();
            int topX = centerX - (textWidth/2);
            int topY = centerY + (textHeight/4); 
            
            g.drawString(text, topX, topY);
        }
        
    }

}
