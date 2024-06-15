package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import elementi.CasellaSpeciale;
import elementi.Posizione;
import elementi.Tabellone;
import gui.FinestraPrincipale.Colore;


public class Casella extends JPanel {
	
    private final int id;
    private final Colore colore;
    private final Posizione pos;
    private final int WIDTH = 10;
    private final int HEIGHT = 10;
    
    private Tabellone tabellone;
    private ArrayList<PedinaGUI> pedine;

    
    public Casella(int id, Colore colore, Posizione pos, Tabellone tabellone, ArrayList<PedinaGUI> pedine) {
        super();
        this.id = id;
        this.colore = colore;
        this.pos = pos;
        this.tabellone = tabellone;
        this.pedine = pedine;
        
        setLayout(new BorderLayout());
        setSize(WIDTH, HEIGHT);
        
        switch (colore) {
            case GIALLO:
            	Color yellow = new Color(247, 193, 45);
            	setBackground(yellow);
            	break;
            case BIANCO:
            	Color white = new Color(236, 232, 221);
            	setBackground(white);
            	break;
            case ROSSO:
            	Color red = new Color(182, 8, 45);
            	setBackground(red);
            	break;
            case BLU:
            	Color blue = new Color(20, 101, 157);
            	setBackground(blue);
            	break;
            case VERDE:
            	Color green = new Color(59, 118, 36);
            	setBackground(green);
            	break;
            default:
            	throw new IllegalArgumentException();
        }
        
        riempi(this);
    }
    

    public int getId() {
        return id;
    }
    

    public Posizione getPos() {
        return pos;
    }
    
    
    private void riempi(Casella casella) {
        JLabel num = new JLabel(Integer.toString(casella.getId()));
        
        //INDICE DELLA CASELLA
        casella.add(num, BorderLayout.SOUTH, 0);
        
        if (casella.getId() == 1) {
        	//AD INIZIO PARTITA TUTTI I GIOCATORI SI TROVANO SULLA PRIMA CASELLA
            for (int i=0 ; i<tabellone.getNumGiocatori() ; i++) {
                PedinaGUI pedina = new PedinaGUI(i, casella, "G"+(i+1), casella.getX(), casella.getY(), casella.getSize().width, casella.getSize().height);
                pedine.add(pedina);
            }

            JLabel start = new JLabel("Start");

            casella.add(start,BorderLayout.CENTER);
            for (PedinaGUI p:pedine) {
                casella.add(p);
            }
        } else if (casella.getId() == tabellone.getN()*tabellone.getM()) {
            JLabel finish = new JLabel("Finish!");
            casella.add(finish, BorderLayout.CENTER);
        }
        
        for (CasellaSpeciale.Tipo t: CasellaSpeciale.Tipo.values()) {
            CasellaSpeciale[] target = tabellone.getCaselleSpeciali().get(t);
            
            if (target != null) {
                for (CasellaSpeciale speciale: target) {
                	
                    if (casella.getPos().equals(speciale.getPos())) {
                        JLabel tipo = new JLabel("    " + t.name().substring(0,3));
                        casella.add(tipo);
                    }
                    
                }
            }
            
        }
    }
}
