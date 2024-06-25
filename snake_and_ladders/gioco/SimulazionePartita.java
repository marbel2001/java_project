package gioco;


import elementi.*;
import observer.*;
import gui.*;

import javax.swing.*;
import java.awt.*;
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
            
        	LabelButtonObserver dado = new LabelButtonObserver(new JButton());
            if (tabellone.getConf().dadoSingolo()) {
                dado.getSubject().setText("Lancia il dado!");
            } else {
                dado.getSubject().setText("Lancia i dadi!");
            }
            dado.getSubject().setVisible(false);
            
        	//SEMAPHORE PER LA SINCRONIZZAZIONE
            Semaphore mutex = new Semaphore(0);
            FinestraPrincipale fp = new FinestraPrincipale(tabellone, dado, mutex);
            fp.inizializza();
            LabelObserver o2 = new LabelObserver(dado);
            dado.add(o2);
            
            String stringa;
            
            while (running) {
            	for (int i=0 ; i<tabellone.getNumGiocatori() ; i++) {
            		try {
            			Thread.sleep(1000);
            		} catch (InterruptedException exception) {
            			exception.printStackTrace();
            		}
            		
            		dado.getSubject().doClick();
                    Giocatore g = tabellone.getGiocatori()[i];
                    
                    if (o2.getState() == AbstractButtonObserver.State.NON_ATTIVO) {
                        try {
                            mutex.acquire();
                        } catch (InterruptedException e) {
                            System.out.println("Interruzione indesiderata.");
                        }
                    }
                                                
                    dado.setState(AbstractButtonObserver.State.NON_ATTIVO);
                    int lancio = calcolaLancio(g);
                    int casella = Tabellone.cambiaCasella(g.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                    
                    //PER IL RITORNO INDIETRO QUANDO SI SFORA LA CASELLA FINALE
                    boolean finish = false;
                    int posCorr = g.getCasella();
                    int calcolo = posCorr + lancio;
                    int numCaselle = conf.getColonne()*conf.getRighe();
                    if (calcolo > numCaselle) {
                    	finish = true;
                    }
                    
                    
                    if (conf.doppioSei() && lancio==12) {
                    	fp.move(g.getId(), casella, finish, lancio);
                    	
                    	stringa = "Il giocatore G" + (g.getId()+1) + " ha ottenuto un doppio 6!";
                    	String title = "DOPPIO 6!";
                    	fp.showDialog(title, stringa);
                    	
                        lancio = calcolaLancio(g);
                        casella = Tabellone.cambiaCasella(g.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                        
                        posCorr = g.getCasella();
                        calcolo = posCorr + lancio;
                        numCaselle = conf.getColonne()*conf.getRighe();
                        if (calcolo > numCaselle) {
                        	finish = true;
                        }
                    }
                    
                    stringa = "Il giocatore G" + (g.getId()+1) + " si muove da " + g.getCasella() + " a " + casella;
                    
                    if (storico != null) {
                    	out.println(stringa);
                    }
                    
                    System.out.println(stringa);
                    
                    //MUOVO IL GIOCATORE
                    fp.move(g.getId(), casella, finish, lancio);
                    if (calcolaVincitore(g)) {
                        vincitore = g;
                        running = false;
                        stringa = "Il giocatore G" + (vincitore.getId()+1) + " ha vinto!";
                        
                        if(storico != null) {
                        	out.println(stringa);
                        }
                        
                        System.out.println(stringa);
                        fp.mostraVincitore(g);
                        break;
                    } else {
                        //MODIFICA DEL TESTO DEL LABEL IN BASE AL PROSSIMO GIOCATORE
                        fp.prossimoGiocatore(updateLabels(g, lancio, o2.getLabels()));
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
        	
        	
        	LabelButtonObserver dado = new LabelButtonObserver(new JButton());
            if (tabellone.getConf().dadoSingolo()) {
                dado.getSubject().setText("Lancia il dado!");
            } else {
                dado.getSubject().setText("Lancia i dadi!");
            }
            
            //SEMAPHORE PER LA SINCRONIZZAZIONE
            Semaphore mutex = new Semaphore(0);
            FinestraPrincipale fp = new FinestraPrincipale(tabellone, dado, mutex);
            fp.inizializza();
            LabelObserver o2 = new LabelObserver(dado);
            dado.add(o2);
            
            String stringa;
            
            while (running) {
                for (int i=0 ; i<tabellone.getNumGiocatori() ; i++) {
                    Giocatore g = tabellone.getGiocatori()[i];
                    
                    if (o2.getState() == AbstractButtonObserver.State.NON_ATTIVO) {
                        try {
                        	mutex.acquire();
                        } catch (InterruptedException e) {
                            System.out.println("Interruzione indesiderata.");
                        }
                    }
                    
                    dado.setState(AbstractButtonObserver.State.NON_ATTIVO);
                    int lancio = calcolaLancio(g);
                    int casella = Tabellone.cambiaCasella(g.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                    
                    //PER IL RITORNO INDIETRO QUANDO SI SFORA LA CASELLA FINALE
                    boolean finish = false;
                    int posCorr = g.getCasella();
                    int calcolo = posCorr + lancio;
                    int numCaselle = conf.getColonne()*conf.getRighe();
                    if (calcolo > numCaselle) {
                    	finish = true;
                    }
                    
                    
                    if (conf.doppioSei() && lancio == 12) {
                        fp.move(g.getId(), casella, finish, lancio);
                        
                        stringa = "Il giocatore G" + (g.getId()+1) + " ha ottenuto un doppio 6!";
                    	String title = "DOPPIO 6!";
                    	fp.showDialog(title, stringa);
                    	
                        lancio = calcolaLancio(g);
                        casella = Tabellone.cambiaCasella(g.getCasella(), lancio, tabellone.getN()*tabellone.getM());
                        
                        posCorr = g.getCasella();
                        calcolo = posCorr + lancio;
                        numCaselle = conf.getColonne()*conf.getRighe();
                        if (calcolo > numCaselle) {
                        	finish = true;
                        }
                    }
                    
                    stringa = "Il giocatore G" + (g.getId()+1) + " si muove da " + g.getCasella() + " a " + casella;
                    
                    if (storico != null) {
                    	out.println(stringa);
                    }
                    
                    System.out.println(stringa);
                    
                    //MUOVO IL GIOCATORE
                    fp.move(g.getId(), casella, finish, lancio);
                    if (calcolaVincitore(g)) {
                        vincitore = g;
                        running = false;
                        stringa = "Il giocatore G" + (vincitore.getId()+1) + " ha vinto!";
                        
                        if(storico != null) {
                        	out.println(stringa);
                        }
                        
                        System.out.println(stringa);
                        fp.mostraVincitore(g);
                        break;
                    } else {
                        //MODIFICA DEL TESTO DEL LABEL IN BASE AL PROSSIMO GIOCATORE
                        fp.prossimoGiocatore(updateLabels(g, lancio, o2.getLabels()));
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
            int lancio = ((int) Math.rint((Math.random() * 5))) + 1; //+1 POICHE' RANDOM() GENERA UN NUMERO CASUALE TRA 0.0 ED 1.0
            
            if ((!conf.dadoSingolo() && ((casella<(ultimaCasella-6))) || !conf.lancioUnico())) {
                lancio += ((int) Math.rint((Math.random() * 5))) + 1; //+1 POICHE' RANDOM() GENERA UN NUMERO CASUALE TRA 0.0 ED 1.0
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
        //INCREMENTO L'ID DEL GIOCATORE (PRENDO IL PROSSIMO GIOCATORE)
        i = (i+1) % tabellone.getNumGiocatori();
        l1 = l1.substring(0, l1.length()-1) + (i+1);

        String l2 = lables[1].getText();
        l2 = l2.substring(0, l2.indexOf(':')+1) + " " + lancio;

        return new String[]{l1, l2};
    }

    
    
    public static void main(String[] args) {
        SimulazionePartita g = new SimulazionePartita();
        g.start();
    }
}
