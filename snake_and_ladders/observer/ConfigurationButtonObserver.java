package observer;

import javax.swing.*;
import gioco.ConfigurazionePartita;


public class ConfigurationButtonObserver extends AbstractButtonObserver {
	
	private ConfigurazionePartita conf;
	

	public ConfigurationButtonObserver(JButton button) {
		super(button);
	}
	
	
	public ConfigurazionePartita getConf() {
        return this.conf;
    }
	
	
	public void setConf(ConfigurazionePartita conf) {
        this.conf = conf;
    }
	

	@Override
	public void add(ButtonObserver o) {
		if (!(o instanceof ConfigurationObserver)) {
            throw new IllegalArgumentException();
        }
		
        observers.add(o);
	}
	


}
