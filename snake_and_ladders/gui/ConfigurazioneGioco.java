package gui;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import gioco.ConfigurazionePartita;
import mediator.ConcreteMediator;
import observer.ConfigurationButtonObserver;



public class ConfigurazioneGioco {
	
	private final String PLACEHOLDER_GIOCATORE = "(da 2 a 12, default = 2)";
	private final String PLACEHOLDER_TABELLONE = "(da 5 a 10, default = 10)";
	
	private JFrame finestra;
    private ConfigurazionePartita conf;
    private ConfigurationButtonObserver submit;
    private Semaphore mutex;

    
    public ConfigurazioneGioco(ConfigurationButtonObserver submit, Semaphore mutex) {
        this.submit = submit;
        this.mutex = mutex;
    }
    

    public void inizializza() {
    	
    	//DIMENSIONI DELLO SCHERMO
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	int PIXEL_X = (int) Math.round(screenSize.getWidth());
    	int PIXEL_Y = (int) Math.round(screenSize.getHeight());
    	
    	
    	//DIMENSIONE DELLA FINESTRA
    	int HEIGHT = 550;
    	int WIDTH = 500;
    	
    	//POSIZIONE DELLA FINESTRA
    	int x = (PIXEL_X/2) - (WIDTH/2);
    	int y = (PIXEL_Y/2) - (HEIGHT/2);
    	
    	Dimension minDim = new Dimension(WIDTH, HEIGHT/2);
    	
    	
    	//DEFINIAMO LA FINESTRA
    	String titolo = "Scale e Serpenti  -  Configurazione del gioco";
    	finestra = new JFrame();
		finestra.setTitle(titolo);
		finestra.setSize(WIDTH, HEIGHT);
		finestra.setMinimumSize(minDim);
		finestra.setMaximumSize(screenSize);
		finestra.setLocation(x, y);
		finestra.setResizable(false);
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        //AGGIUNTA DI UNA BARRA DI SCORRIMENTO AL PANNELLO
		JScrollPane jsp = new JScrollPane(panel);
		jsp.setLayout(new ScrollPaneLayout());
		jsp.createVerticalScrollBar();
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		finestra.add(jsp);
        
        
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        
        JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        
        JMenuBar menu = new JMenuBar();
        
        JMenu file = new JMenu("File");
        
        JMenuItem apri = new JMenuItem("Apri");
        apri.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            try {
                if (chooser.showOpenDialog(finestra) == JFileChooser.APPROVE_OPTION) {
                    File configurazione = chooser.getSelectedFile();
                    conf = getConfigurazione(configurazione);
                    if (conf != null && configurazioneCorretta(conf)) {
                        submit.setConf(conf);
                        submit.notifica();
                        mutex.release();
                        finestra.dispose();
                    } else {
                    	JOptionPane.showMessageDialog(finestra, "Il file non contiene alcuna configurazione valida.");
                    }
                }
            } catch (Exception exception) {
            	exception.printStackTrace();
            }
        });
        
        JMenuItem salva = new JMenuItem("Salva configurazione");
        JMenuItem salvaConNome = new JMenuItem("Salva configurazione con nome");
        
        JMenuItem esci = new JMenuItem("Esci");
        esci.addActionListener(e -> {
            finestra.dispatchEvent(new WindowEvent(finestra, WindowEvent.WINDOW_CLOSING));
        });
        

        file.add(apri);
        file.add(salva);
        file.add(salvaConNome);
        file.addSeparator();
        file.add(esci);
        

        JMenu aiuto = new JMenu("Aiuto");
        

        JMenuItem guida = new JMenuItem("Guida");
        guida.addActionListener(e -> {
            String msg = "Utilizzare i pulsanti per definire la configurazione di questa partita.\n\n";
            msg += "In alternativa si può scegliere di:\n";
            msg += "- caricare una configurazione (file con estensione .config) da filesystem;\n";
            msg += "- salvare o salvare con nome la configurazione corrente sul filesystem (con estensione .config).";
            JOptionPane.showMessageDialog(finestra, msg, "Scelta della Configurazione della partita.", JOptionPane.INFORMATION_MESSAGE);
        });
        
        
        JMenuItem crediti = new JMenuItem("Crediti");
        crediti.addActionListener(e -> {
            String msg = "Progetto realizzato da Marco Belvedere.\n";
            JOptionPane.showMessageDialog(finestra, msg);
        });

        aiuto.add(guida);
        aiuto.add(crediti);

        menu.add(file);
        menu.add(aiuto);
        
        
        finestra.setJMenuBar(menu);
        
        

        JTextField numGiocatori = new JTextField(20);
        Placeholder.setPlaceholder(numGiocatori, PLACEHOLDER_GIOCATORE);
        numGiocatori.addFocusListener(Placeholder.getFocusListener(numGiocatori, PLACEHOLDER_GIOCATORE));

        JTextField righe = new JTextField(20);
        Placeholder.setPlaceholder(righe,PLACEHOLDER_TABELLONE);
        righe.addFocusListener(Placeholder.getFocusListener(righe,PLACEHOLDER_TABELLONE));

        JTextField colonne = new JTextField(20);
        Placeholder.setPlaceholder(colonne,PLACEHOLDER_TABELLONE);
        colonne.addFocusListener(Placeholder.getFocusListener(colonne,PLACEHOLDER_TABELLONE));

        JCheckBox dadoSingolo = new JCheckBox("Dado singolo");
        JCheckBox lancioUnico = new JCheckBox("Lancio unico");
        JCheckBox doppioSei = new JCheckBox("Doppio sei");
        JCheckBox caselleSosta = new JCheckBox("Caselle sosta");
        JCheckBox casellePremio = new JCheckBox("Caselle premio");
        JCheckBox pescaCarta = new JCheckBox("Pesca carta");
        JCheckBox ulterioriCarte = new JCheckBox("Ulteriori carte");
        JCheckBox salvaStorico = new JCheckBox("Salva storico partita");
        JCheckBox automatico = new JCheckBox("Modalità automatica");
        
        

        //GLI ACTION LISTENER VANNO MESSI DOPO L'ISTANZIAZIONE DELLE JCHECKBOX
        salva.addActionListener(e -> {
            conf = buildConfigurazione(numGiocatori, righe, colonne, dadoSingolo, lancioUnico, doppioSei, caselleSosta, casellePremio, pescaCarta, ulterioriCarte, salvaStorico, automatico);
            
            if (!configurazioneCorretta(conf)) {
                JOptionPane.showMessageDialog(finestra, "Impossibile salvare, configurazione non valida!", "Errore di inserimento.", JOptionPane.ERROR_MESSAGE);
            } else {
                JFileChooser chooser = new JFileChooser();
                File output = null;
                
                if( chooser.showSaveDialog(finestra) == JFileChooser.APPROVE_OPTION) {
                    output = chooser.getSelectedFile();
                }
                
                if (output != null) {
                    int i = output.getName().lastIndexOf('.');
                    String ext = output.getName().substring(i+1);
                    
                    if (!ext.equals("config")) {
                        JOptionPane.showMessageDialog(finestra, "Salvare il file con l'estensione \".config\".");
                    } else {
                        if (!output.exists()) {
                        	salvaFile(output);
                        } else {
                            int conferma = JOptionPane.showConfirmDialog(finestra, "Sovrascrivere " + output.getName() + "?\n", "Conferma salvataggio", JOptionPane.YES_NO_OPTION);
                            
                            if (conferma == JOptionPane.YES_OPTION) {
                            	salvaFile(output);
                            }
                        }
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(finestra, "Nessun Salvataggio.");
                }
            }
        });
        

        salvaConNome.addActionListener(e -> {
            conf = buildConfigurazione(numGiocatori, righe, colonne, dadoSingolo, lancioUnico, doppioSei, caselleSosta, casellePremio, pescaCarta, ulterioriCarte, salvaStorico, automatico);
            
            if (!configurazioneCorretta(conf)) {
                JOptionPane.showMessageDialog(finestra, "Impossibile salvare, configurazione non valida!", "Errore di inserimento.", JOptionPane.ERROR_MESSAGE);
            } else {
                JFileChooser chooser = new JFileChooser();
                File output = null;
                
                if (chooser.showSaveDialog(finestra) == JFileChooser.APPROVE_OPTION) {
                    output = chooser.getSelectedFile();
                }
                
                if (output != null) {
                    int i = output.getName().lastIndexOf('.');
                    String ext = output.getName().substring(i+1);
                    
                    if (!ext.equals("config")) {
                        JOptionPane.showMessageDialog(finestra, "Estensione non compatibile.");
                    } else {
                        if (!output.exists()) {
                        	salvaFile(output);
                        } else {
                            JOptionPane.showMessageDialog(finestra, "Esiste già un file con questo nome nella cartella. Scegliere un altro nome con cui salvare il file.");
                        }
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(finestra, "Nessun Salvataggio.");
                }
            }
        });

        
        submit.getSubject().addActionListener(e -> {
            try {
                int conferma = conferma();
                
                if (conferma == JOptionPane.YES_OPTION) {
                    conf = buildConfigurazione(numGiocatori, righe, colonne, dadoSingolo, lancioUnico, doppioSei, caselleSosta, casellePremio, pescaCarta, ulterioriCarte, salvaStorico, automatico);
                    
                    if (configurazioneCorretta(conf)) {
                        submit.setConf(conf);
                        submit.notifica();
                        mutex.release();
                        finestra.dispose();
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(finestra, "Inserire solo numeri interi positivi nelle aree di testo!", "Errore di inserimento.", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(finestra, "I valori inseriti non rispettano i limiti!", "Errore di inserimento.", JOptionPane.ERROR_MESSAGE);
            }
        });

        ConcreteMediator mediator = new ConcreteMediator();

        mediator.setDadoSingolo(dadoSingolo);
        mediator.setLancioUnico(lancioUnico);
        mediator.setDoppioSei(doppioSei);

        mediator.setPescaCarta(pescaCarta);
        mediator.setUlterioriCarte(ulterioriCarte);

        dadoSingolo.addActionListener(e -> mediator.sceltaDado(dadoSingolo));
        lancioUnico.addActionListener(e -> mediator.sceltaDado(lancioUnico));
        doppioSei.addActionListener(e -> mediator.sceltaDado(doppioSei));

        ulterioriCarte.setEnabled(false);
        pescaCarta.addActionListener(e -> mediator.pescaCarta(pescaCarta));
        
        
        JPanel sub1 = new JPanel();
        sub1.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel sub2 = new JPanel();
        sub2.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel sub3 = new JPanel();
        sub3.setLayout(new FlowLayout(FlowLayout.LEFT));

        sub1.add(new JLabel("Numero dei giocatori: "));
        sub1.add(numGiocatori);
        sub2.add(new JLabel("Numero righe tabellone: "));
        sub2.add(righe);
        sub3.add(new JLabel("Numero colonne tabellone: "));
        sub3.add(colonne);
        
        panel1.add(sub1);
        panel1.add(sub2);
        panel1.add(sub3);
        
        panel2.add(dadoSingolo);
        panel2.add(lancioUnico);
        panel2.add(doppioSei);
        panel2.add(caselleSosta);
        panel2.add(casellePremio);
        panel2.add(pescaCarta);
        panel2.add(ulterioriCarte);
        panel2.add(salvaStorico);
        panel2.add(automatico);
        
        panel4.add(submit.getSubject());
       
        panel3.add(panel1);
        panel3.add(panel2);
        
        
        panel.add(panel3);
        panel.add(panel4);
                
        finestra.add(panel);
        
        finestra.setVisible(true);
    }

    
    
    private ConfigurazionePartita buildConfigurazione(JTextField numGiocatori, JTextField righe, JTextField colonne,
    		JCheckBox dadoSingolo, JCheckBox lancioUnico, JCheckBox doppioSei,
            JCheckBox caselleSosta, JCheckBox casellePremio, JCheckBox pescaCarta,
            JCheckBox ulterioriCarte, JCheckBox salvaStorico, JCheckBox automatico)
    {
        int numG, n, m;

        if (numGiocatori.getText().equals(PLACEHOLDER_GIOCATORE)) {
        	numG = 2;
        } else {
        	numG = Integer.parseInt(numGiocatori.getText());
        }

        if (righe.getText().equals(PLACEHOLDER_TABELLONE)) {
            n = 10;
        } else {
            n = Integer.parseInt(righe.getText());
        }

        if (colonne.getText().equals(PLACEHOLDER_TABELLONE)) {
            m = 10;
        } else {
            m = Integer.parseInt(colonne.getText());
        }

        return new ConfigurazionePartita (numG, n, m,
                dadoSingolo.isSelected(), lancioUnico.isSelected(), doppioSei.isSelected(),
                caselleSosta.isSelected(), casellePremio.isSelected(),
                pescaCarta.isSelected(), ulterioriCarte.isSelected(), salvaStorico.isSelected(),
                automatico.isSelected()
        );
    }
    
    

    private ConfigurazionePartita getConfigurazione(File f) throws FileNotFoundException {
        if (!f.exists()) {
            JOptionPane.showMessageDialog(finestra, "Il file non esiste.");
        } else {
            int i = f.getName().lastIndexOf('.');
            String ext = f.getName().substring(i+1);
            
            if (!ext.equals("config")) {
                JOptionPane.showMessageDialog(finestra, "Estensione del file non supportata.");
            } else {
                int numG = 0;
                int n = 0;
                int m = 0;
                
                boolean dadoSingolo = false;
                boolean lancioUnico = false;
                boolean doppioSei = false;
                boolean caselleSosta = false;
                boolean casellePremio = false;
                boolean pescaCarta = false;
                boolean ulterioriCarte = false;
                boolean salvaStorico = false;
                boolean automatico = false;
                
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                
                while (true) {
                    try {
                        String line = in.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] entry = line.trim().split(":");
                        switch (entry[0]) {
                            case "Configurazione {":
                            	break;
                            case "}":
                            	break;
                            case "numGiocatori":
                            	numG = Integer.parseInt(entry[1]);
                            	break;
                            case "numRighe":
                                n = Integer.parseInt(entry[1]);
                                break;
                            case "numColonne":
                                m = Integer.parseInt(entry[1]);
                                break;
                            case "dadoSingolo":
                                dadoSingolo = entry[1].equals("true");
                                break;
                            case "lancioUnico":
                                lancioUnico = entry[1].equals("true");
                                break;
                            case "doppioSei":
                                doppioSei = entry[1].equals("true");
                                break;
                            case "caselleSosta":
                                caselleSosta = entry[1].equals("true");
                                break;
                            case "casellePremio":
                                casellePremio = entry[1].equals("true");
                                break;
                            case "pescaCarta":
                                pescaCarta = entry[1].equals("true");
                                break;
                            case "ulterioriCarte":
                                ulterioriCarte = entry[1].equals("true");
                                break;
                            case "salvaStorico":
                            	salvaStorico = entry[1].equals("true");
                                break;
                            case "automatico":
                                automatico = entry[1].equals("true");
                                break;
                            default:
                            	throw new IllegalArgumentException();
                        }
                    } catch (IOException exception) {
                        break;
                    }
                }
                return new ConfigurazionePartita(numG, n, m, dadoSingolo, lancioUnico, doppioSei, caselleSosta, casellePremio, pescaCarta, ulterioriCarte, salvaStorico, automatico);
            }
        }
        return null;
    }
    
    

    private boolean configurazioneCorretta(ConfigurazionePartita conf) {
        if (conf.getNumGiocatori()<2 || conf.getNumGiocatori()>12 ||
                conf.getRighe()<5 || conf.getRighe()>10 ||
                conf.getColonne()<5 || conf.getColonne()>10) {
            return false;
        }
        
        if (conf.dadoSingolo() && (conf.lancioUnico() || conf.doppioSei())) {
            return false;
        }
        
        return conf.pescaCarta() || !conf.ulterioriCarte();
    }
    
    

    private void salvaFile(File f) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(f), true);

            out.println("Configurazione {");
            out.println("\tnumGiocatori:" + conf.getNumGiocatori());
            out.println("\tnumRighe:" + conf.getRighe());
            out.println("\tnumColonne:" + conf.getColonne());
            out.println("\tdadoSingolo:" + conf.dadoSingolo());
            out.println("\tlancioUnico:" + conf.lancioUnico());
            out.println("\tdoppioSei:" + conf.doppioSei());
            out.println("\tcaselleSosta:" + conf.caselleSosta());
            out.println("\tcasellePremio:" + conf.casellePremio());
            out.println("\tpescaCarta:" + conf.pescaCarta());
            out.println("\tulterioriCarte:" + conf.ulterioriCarte());
            out.println("\tautomatico:" + conf.isAuto());
            out.println("}");

            out.close();
        } catch (FileNotFoundException exception) {
        	exception.printStackTrace();
        }
    }

    
    
    
    static class Placeholder {
    	
        public static FocusListener getFocusListener(JTextField field, String placeholder) {
            return new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText(null);
                        field.setForeground(Color.BLACK);
                        field.setFont(field.getFont().deriveFont(Font.PLAIN));
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText() == null || field.getText().equals("")) {
                        field.setFont(field.getFont().deriveFont(Font.ITALIC));
                        field.setText(placeholder);
                        field.setForeground(Color.GRAY);
                    }
                }
            };
        }

        public static void setPlaceholder(JTextField field, String placeholder) {
            field.setFont(field.getFont().deriveFont(Font.ITALIC));
            field.setText(placeholder);
            field.setForeground(Color.GRAY);
        }
    }

    private int conferma() {
        String msg = "Sicuro di voler continuare con questa configurazione?";
        String title = "Conferma Submit";
        return JOptionPane.showConfirmDialog(finestra,msg,title,JOptionPane.YES_NO_OPTION);
    }

}
