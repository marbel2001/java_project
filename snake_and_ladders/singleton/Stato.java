package singleton;

import elementi.Giocatore;


public interface Stato {
	
	//TRUE SE SI PUO' MUOVERE, FALSE ALTRIMENTI
    boolean fineSosta(Giocatore g);
    
}
