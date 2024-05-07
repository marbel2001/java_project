package singleton;

import elementi.Giocatore;

public enum Panchina implements Stato {
	//QUESTA ENUMERAZIONE HA UN'UNICA ISTANZA DETTA "INSTANCE"
	INSTANCE;
	

    @Override
    public boolean continua(Giocatore g) {
        if (g.isStop()) {
            g.setStato(Gioca.INSTANCE);
            g.setStop(false);
            return true;
        }
        
        g.setStato(Gioca.INSTANCE);
        
        return false;
    }

}
