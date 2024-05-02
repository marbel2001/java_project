package singleton;

import elementi.Giocatore;


public enum Gioca implements Stato {
	//QUESTA ENUMERAZIONE HA UN'UNICA ISTANXA DETTA "INSTANCE"
    INSTANCE;
	

    @Override
    public boolean fineSosta(Giocatore g) {
        return true;
    }

}
