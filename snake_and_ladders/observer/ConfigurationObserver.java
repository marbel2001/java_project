package observer;

import gioco.ConfigurazionePartita;


public class ConfigurationObserver extends ButtonObserver {
	
	private ConfigurationButtonObserver subject;
    private ConfigurazionePartita conf;
    

    public ConfigurationObserver(ConfigurationButtonObserver subject) {
        super();
        this.subject = subject;
    }

    public ConfigurazionePartita getConf() {
        return conf;
    }

    @Override
    public void update() {
        stato = subject.getState();
        conf = subject.getConf();
    }

}
