package gioco;


import elementi.*;
import observer.*;
import singleton.*;
import gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;


public class SimulazionePartita {
	
	private ConfigurazionePartita conf;
    private Tabellone tabellone;
    private File storico = null;
    private PrintWriter out;
    
    
    public void start() {
    	JButton button = new JButton("Comincia la partita!");
        ConfigurationButtonObserver submit = new ConfigurationButtonObserver(button);
        
        //PER LA SINCRONIZZAZIONE DEI TURNI DEI GIOCATORI
        Semaphore mutexConf = new Semaphore(0);
        
        ConfigurazioneGioco fc = new ConfigurazioneGioco(submit, mutexConf);
        fc.inizializza();
        ConfigurationObserver o1 = new ConfigurationObserver(submit);
        submit.add(o1);
        
        if (o1.getState() == AbstractButtonObserver.State.NON_ATTIVO) {
            try {
                mutexConf.acquire();
            } catch (InterruptedException e) {
                System.out.println("Interruzione indesiderata.");
            }
        }
        
        conf = o1.getConf();
        tabellone = new Tabellone(conf);
        tabellone.inizializza();
        Giocatore vincitore;
        boolean running = true;

        if (conf.isAuto()) {

        	if (conf.salvaStorico()) {
        		JFileChooser chooser = new JFileChooser();
                JOptionPane.showMessageDialog(chooser, "Scegliere il file (con estensione \".txt\") nel quale salvare lo storico della partita");
                                
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    storico = chooser.getSelectedFile();
                }
                
                if (storico != null) {
                    try {
                        out = new PrintWriter(new FileOutputStream(storico), true);
                        
                    } catch (FileNotFoundException e) {
                        System.out.println("File non trovato.");
                    }
                }
        	}
            
        	LabelButtonObserver roll = new LabelButtonObserver(new JButton());
            if (tabellone.getConf().dadoSingolo()) {
                roll.getSubject().setText("Lancia il dado!");
            } else {
                roll.getSubject().setText("Lancia i dadi!");
            }
            roll.getSubject().setVisible(false);
            
        	//SEMAPHORE PER LA SINCRONIZZAZIONE
            Semaphore mutex = new Semaphore(0);
            FinestraPrincipale fp = new FinestraPrincipale(tabellone, roll, mutex);
            fp.inizializza();
            LabelObserver o2 = new LabelObserver(roll);
            roll.add(o2);
            
            String stringa;
            
            while (running) {
            	for (int i=0 ; i<tabellone.getNumGiocatori() ; i++) {
            		try {
            			Thread.sleep(1000);
            		} catch (InterruptedException exception) {
            			exception.printStackTrace();
            		}
            		
            		roll.getSubject().doClick();
                    Giocatore cur = tabellone.getGiocatori()[i];
                    
                    if (o2.getState() == AbstractButtonObserver.State.NON_ATTIVO) {
                        try {
                            mutex.acquire();
                        } catch (InterruptedException e) {
                            System.out.println("Interruzione indesiderata.");
                        }
                    }
                                                
                    roll.setState(AbstractButtonObserver.State.NON_ATTIVO);
                    int lancio = calcolaLancio(cur);
                    int casella = Tabellone.cambiaCasella(cur.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                    
                    //PER IL RITORNO INDIETRO QUANDO SI SFORA LA CASELLA FINALE
                    boolean finish = false;
                    int posCorr = cur.getCasella();
                    int calcolo = posCorr + lancio;
                    int numCaselle = conf.getColonne()*conf.getRighe();
                    if (calcolo > numCaselle) {
                    	finish = true;
                    }
                    
                    if (conf.doppioSei() && lancio == 12) {
                        fp.move(cur.getId(), casella, finish, lancio);
                        lancio = calcolaLancio(cur);
                        casella = Tabellone.cambiaCasella(cur.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                        
                        posCorr = cur.getCasella();
                        calcolo = posCorr + lancio;
                        numCaselle = conf.getColonne()*conf.getRighe();
                        if (calcolo > numCaselle) {
                        	finish = true;
                        }
                    }
                    
                    stringa = "Il giocatore G" + (cur.getId()+1) + " si muove da " + cur.getCasella() + " a " + casella;
                    
                    if (storico != null) {
                    	out.println(stringa);
                    }
                    
                    System.out.println(stringa);
                    
                    //MUOVO IL GIOCATORE
                    fp.move(cur.getId(), casella, finish, lancio);
                    if (calcolaVincitore(cur)) {
                        vincitore = cur;
                        running = false;
                        stringa = "Il giocatore G" + (vincitore.getId()+1) + " ha vinto!";
                        
                        if(storico != null) {
                        	out.println(stringa);
                        }
                        
                        System.out.println(stringa);
                        fp.mostraVincitore(cur);
                        break;
                    } else {
                        //MODIFICA DEL TESTO DEL LABEL IN BASE AL PROSSIMO GIOCATORE
                        fp.prossimoGiocatore(updateLabels(cur, lancio, o2.getLabels()));
                    }
                }
            }
            
            
        } else {
        	
        	if (conf.salvaStorico()) {
        		JFileChooser chooser = new JFileChooser();
                JOptionPane.showMessageDialog(chooser, "Scegliere il file (con estensione \".txt\") nel quale salvare lo storico della partita");
                                
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    storico = chooser.getSelectedFile();
                }
                
                if (storico != null) {
                    try {
                        out = new PrintWriter(new FileOutputStream(storico), true);
                        
                    } catch (FileNotFoundException e) {
                        System.out.println("File non trovato.");
                    }
                }
        	}
        	
        	
        	LabelButtonObserver roll = new LabelButtonObserver(new JButton());
            if (tabellone.getConf().dadoSingolo()) {
                roll.getSubject().setText("Lancia il dado!");
            } else {
                roll.getSubject().setText("Lancia i dadi!");
            }
            
            //SEMAPHORE PER LA SINCRONIZZAZIONE
            Semaphore mutex = new Semaphore(0);
            FinestraPrincipale fp = new FinestraPrincipale(tabellone, roll, mutex);
            fp.inizializza();
            LabelObserver o2 = new LabelObserver(roll);
            roll.add(o2);
            
            String stringa;
            
            while (running) {
                for (int i=0 ; i<tabellone.getNumGiocatori() ; i++) {
                    Giocatore cur = tabellone.getGiocatori()[i];
                    
                    if (o2.getState() == AbstractButtonObserver.State.NON_ATTIVO) {
                        try {
                        	mutex.acquire();
                        } catch (InterruptedException e) {
                            System.out.println("Interruzione indesiderata.");
                        }
                    }
                    
                    roll.setState(AbstractButtonObserver.State.NON_ATTIVO);
                    int lancio = calcolaLancio(cur);
                    int casella = Tabellone.cambiaCasella(cur.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                    
                    //PER IL RITORNO INDIETRO QUANDO SI SFORA LA CASELLA FINALE
                    boolean finish = false;
                    int posCorr = cur.getCasella();
                    int calcolo = posCorr + lancio;
                    int numCaselle = conf.getColonne()*conf.getRighe();
                    if (calcolo > numCaselle) {
                    	finish = true;
                    }
                    
                    if (conf.doppioSei() && lancio == 12) {
                        fp.move(cur.getId(), casella, finish, lancio);
                        lancio = calcolaLancio(cur);
                        casella = Tabellone.cambiaCasella(cur.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                        
                        posCorr = cur.getCasella();
                        calcolo = posCorr + lancio;
                        numCaselle = conf.getColonne()*conf.getRighe();
                        if (calcolo > numCaselle) {
                        	finish = true;
                        }
                    }
                    
                    stringa = "Il giocatore G" + (cur.getId()+1) + " si muove da " + cur.getCasella() + " a " + casella;
                    
                    if (storico != null) {
                    	out.println(stringa);
                    }
                    
                    System.out.println(stringa);
                    
                    //MUOVO IL GIOCATORE
                    fp.move(cur.getId(), casella, finish, lancio);
                    if (calcolaVincitore(cur)) {
                        vincitore = cur;
                        running = false;
                        stringa = "Il giocatore G" + (vincitore.getId()+1) + " ha vinto!";
                        
                        if(storico != null) {
                        	out.println(stringa);
                        }
                        
                        System.out.println(stringa);
                        fp.mostraVincitore(cur);
                        break;
                    } else {
                        //MODIFICA DEL TESTO DEL LABEL IN BASE AL PROSSIMO GIOCATORE
                        fp.prossimoGiocatore(updateLabels(cur, lancio, o2.getLabels()));
                    }
                }
            }
        }
    }
    

    private int calcolaLancio(Giocatore g) {
        if (g.sosta()) {
            int casella = g.getCasella();
            int ultimaCasella = tabellone.getN() * tabellone.getM();
            //GENERAZIONE DI UN NUMERO CASUALE
            int lancio = ((int) Math.rint((Math.random() * 5))) + 1;
            if (!conf.dadoSingolo() && ((casella<(ultimaCasella-6)) || !conf.lancioUnico())) {
                lancio += ((int) Math.rint((Math.random() * 5))) + 1;
            }
            return lancio;
        }
        
        return 0;
    }
    

    private boolean calcolaVincitore(Giocatore g) {
        return (g.getCasella() == tabellone.getN()*tabellone.getM());
    }

    
    private String[] updateLabels(Giocatore g, int lancio, Label[] lables) {
    	//PER AGGIORNARE LE LABELS DEL GIOCATORE E DEL LANCIO
        String l1 = lables[0].getText();
        int i = g.getId();
        i = (i+1) % tabellone.getNumGiocatori();
        l1 = l1.substring(0, l1.length()-1) + (i+1);

        String l2 = lables[1].getText();
        l2 = l2.substring(0, l2.indexOf(':')+1) + " " + lancio;

        return new String[]{l1, l2};
    }

    
    private Posizione getPos(int casella) {
        int id = tabellone.getN()* tabellone.getM();
        
        if ((tabellone.getN()-1)%2 == 0) {
            id -=  tabellone.getM()-1;
        }
        
        for (int i=0 ; i<tabellone.getN() ; i++) {
            boolean verso;
            
            //IL VERSO DI UNA RIGA CON QUELLA SUPERIORE E' OPPOSTO (true = -->, false = <--)
            //IL VERSO DI OGNI RIGA VIENE SCELTO IN BASE ALLE RIGHE TOTALI
            if (tabellone.getN()%2 == 0) {
            	if(i%2 != 0) {
            		verso = true;
            	} else {
            		verso = false;
            	}
            } else {
            	if(i%2 == 0) {
            		verso = true;
            	} else {
            		verso = false;
            	}
            	
            }
            
            for (int j=0 ; j< tabellone.getM() ; j++) {
                if (id == casella) {
                    return new Posizione(i, j);
                }
                
                if (verso) {
                    id++;
                } else {
                    id--;
                }
            }
            
            //PER RIPRENDERE IL CONTEGGIO DALLA CASELLA DI SOTTO
            if (!verso) {
                id -= tabellone.getM()-1;
            } else {
                id -= tabellone.getM()+1;
            }
        }
        
        return null;
    }
    

    private int getCasella(Posizione pos) {
        int id = tabellone.getN()* tabellone.getM();
        
        if ((tabellone.getN()-1)%2 == 0) {
            id -=  tabellone.getM()-1;
        }
        
        for (int i=0 ; i<tabellone.getN() ; i++) {
            boolean verso;
            
            //IL VERSO DI UNA RIGA CON QUELLA SUPERIORE E' OPPOSTO (true = -->, false = <--)
            //IL VERSO DI OGNI RIGA VIENE SCELTO IN BASE ALLE RIGHE TOTALI
            if (tabellone.getN() % 2 == 0) {
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
            
            for (int j=0 ; j<tabellone.getM() ; j++) {
                if (i == pos.getX() && j == pos.getY()) {
                    return id;
                }
                
                if (verso) {
                    id++;
                } else {
                    id--;
                }
            }
            
            //PER RIPRENDERE IL CONTEGGIO DALLA CASELLA DI SOTTO
            if (!verso) {
                id -= tabellone.getM()-1;
            } else {
                id -= tabellone.getM()+1;
            }
        }
        return 0;
    }

    
    private void controlloCasellaSpeciale(Giocatore cur, int lancio, Posizione newPos, PrintWriter out) {
        boolean trovato = false;

        for (Scala s: tabellone.getScale()) {
            if (s.getBottom().equals(newPos)) {
                trovato = true;

                int nuova = getCasella(s.getTop());
                out.println("Il giocatore G" + (cur.getId()+1) + " è finito su una SCALA, arriva alla casella " + nuova + "\n");
                tabellone.muovi(cur.getId(), nuova, s.getTop());
            }
        }

        if (!trovato) {
            for (Serpente s: tabellone.getSerpenti()) {
                if (s.getTop().equals(newPos)) {
                    trovato = true;

                    int nuova = getCasella(s.getBottom());
                    out.println("Il giocatore G" + (cur.getId() + 1) + " è finito su un SERPENTE, ritorna alla casella " + nuova + "\n");
                    tabellone.muovi(cur.getId(), nuova, s.getBottom());
                }
            }
        }

        if (!trovato) {
            for (CasellaSpeciale.Tipo t: tabellone.getCaselleSpeciali().keySet()) {
                for (CasellaSpeciale c: tabellone.getCaselleSpeciali().get(t)) {
                    if (c.getPos().equals(newPos)) {
                        out.println("Il giocatore G" + (cur.getId() + 1) + " è finito su " + t.name() + "\n");
                        azioneCasella(t, cur, lancio, out);
                    }
                }
            }
        }
    }
    

    private void azioneCasella(CasellaSpeciale.Tipo t, Giocatore cur, int lancio, PrintWriter out) {
    	
    	Posizione nuovaPos;
    	int nuovaCasella;
    	
        switch (t) {
            case PANCHINA:
            	cur.setStato(Panchina.INSTANCE);
            	break;
            case LOCANDA:
            	cur.setStato(new Locanda());
            	break;
            case DIVIETO:
            	cur.setStop(true);
            	break;
            case DADI:
                int nuovoLancio = calcolaLancio(cur);
                nuovaCasella = Tabellone.cambiaCasella(cur.getCasella(), nuovoLancio, tabellone.getN()*tabellone.getM());
                out.println("Il giocatore G" + (cur.getId()+1) + " tira di nuovo i dadi");
                if (conf.doppioSei() && nuovoLancio==12) {
                    out.println("Doppio 6! Il giocatore G" + (cur.getId()+1) + " tira di nuovo i dadi");
                    nuovoLancio = calcolaLancio(cur);
                    nuovaCasella = Tabellone.cambiaCasella(cur.getCasella(), nuovoLancio, tabellone.getN()*tabellone.getM());
                }
                out.println("Numero estratto: " + nuovoLancio);
                out.println("Il giocatore G" + (cur.getId()+1) + " si muove da " + cur.getCasella() + " a " + nuovaCasella + "\n");

                nuovaPos = getPos(nuovaCasella);
                tabellone.muovi(cur.getId(), nuovaCasella, nuovaPos);

                controlloCasellaSpeciale(cur, nuovoLancio, nuovaPos, out);
                
                break;
            case MOLLA:
                nuovaCasella = Tabellone.cambiaCasella(cur.getCasella(),lancio,tabellone.getN()*tabellone.getM());
                out.println("Il giocatore G" + (cur.getId()+1) + " avanza di altre " + lancio + " caselle, arriva alla casella " + nuovaCasella + "\n");
                nuovaPos = getPos(nuovaCasella);
                tabellone.muovi(cur.getId(), nuovaCasella, nuovaPos);

                controlloCasellaSpeciale(cur, lancio, nuovaPos, out);
                
                break;
            case PESCA:
                int i;
                if (tabellone.getConf().ulterioriCarte()) {
                	//CON 4 ESCLUDO "PESCA"
                    i = (int) Math.rint(Math.random() * 4);
                } else {
                	//CON 5 ESCLUDO "DIVIETO" E "PESCA"
                    i = (int) Math.rint(Math.random() * 3);
                }
                CasellaSpeciale.Tipo cartaPescata = CasellaSpeciale.Tipo.values()[i];
                out.println("Il giocatore pesca: " + cartaPescata);
                azioneCasella(cartaPescata, cur, lancio, out);
                
        }
    }
    

    
    
    public static void main(String[] args) {
        SimulazionePartita g = new SimulazionePartita();
        g.start();
    }
}
