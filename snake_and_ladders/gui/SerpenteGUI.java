package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;


public class SerpenteGUI extends JLabel {
	
	private FinestraPrincipale.Casella top, bottom;
	private final int SPESSORE = 10;
	
	
	public SerpenteGUI(FinestraPrincipale.Casella top, FinestraPrincipale.Casella bottom) {
		super();
		this.top = top;
		this.bottom = bottom;
		setBounds(Math.min(top.getX(), bottom.getX()), bottom.getY(), 600, 500);
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//PUNTARE LE LINEE AL CENTRO DELLE DUE CASELLE
        int x1 = top.getX()+(top.getSize().width/2);
        int y1 = top.getY()+(top.getSize().height/2);
        int x2 = bottom.getX()+(bottom.getSize().width/2);
        int y2 = bottom.getY()+(bottom.getSize().height/2);
        int offset = SPESSORE/2;
	    
        g.setColor(Color.BLACK);
        g.drawLine(x1-offset-1, y1, x2-offset-1, y2);
        
        g.setColor(new Color(0, 161, 53));
                               
        for (int i=0 ; i<SPESSORE ; i++) {
        	int tmp1 = x1 + i - offset;
        	int tmp2 = x2 + i - offset;
            g.drawLine(tmp1, y1, tmp2, y2);
        }
        
        g.setColor(Color.BLACK);
        g.drawLine(x1+offset, y1, x2+offset, y2);
    }

}
