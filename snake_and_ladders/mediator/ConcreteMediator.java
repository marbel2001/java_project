package mediator;

import javax.swing.*;


public class ConcreteMediator implements Mediator {
	
	//USO MEDIATOR PER GUIDARE L'OSSERVATORE NELLA SCELTA DELLA CONFIGURAZIONE
    private JCheckBox dadoSingolo, lancioUnico, doppioSei, pescaCarta, ulterioriCarte;
    

    public void setDadoSingolo(JCheckBox dadoSingolo) {
        this.dadoSingolo = dadoSingolo;
    }
    

    public void setLancioUnico(JCheckBox lancioUnico) {
        this.lancioUnico = lancioUnico;
    }
    

    public void setDoppioSei(JCheckBox doppioSei) {
        this.doppioSei = doppioSei;
    }
    

    public void setPescaCarta(JCheckBox pescaCarta) {
        this.pescaCarta = pescaCarta;
    }
    

    public void setUlterioriCarte(JCheckBox ulterioriCarte) {
        this.ulterioriCarte = ulterioriCarte;
    }
    

    @Override
    public void sceltaDado(JCheckBox scelta) {
        if (!scelta.isSelected()) {
            if (scelta == dadoSingolo) {
                lancioUnico.setEnabled(true);
                doppioSei.setEnabled(true);
            } else if (!lancioUnico.isSelected() && !doppioSei.isSelected()) {
                dadoSingolo.setEnabled(true);
            }
        } else if (scelta == dadoSingolo) {
            lancioUnico.setEnabled(false);
            doppioSei.setEnabled(false);
        } else if (scelta==lancioUnico || scelta==doppioSei) {
            dadoSingolo.setEnabled(false);
        }
    }

    
    @Override
    public void pescaCarta(JCheckBox scelta) {
        if (scelta == pescaCarta) {
            if (!scelta.isSelected()) {
                ulterioriCarte.setSelected(false);
            }
            
            ulterioriCarte.setEnabled(scelta.isSelected());
        }
    }

}
