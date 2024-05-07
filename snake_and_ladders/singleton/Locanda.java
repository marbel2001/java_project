package singleton;

import elementi.Giocatore;

//NON E' POSSIBILE COSTRUIRE "LOCANDA" COME UN SINGLETON POICHE' PIU' GIOCATORI POSSONO TROVARSI
//CONTEMPORANEAMENTE IN QUESTO STATO E PER OGNUNO DI ESSI VA TENUTA TRACCIA DEL NUMERO DI TURNI
//CHE DEVONO ANCORA ASPETTARE

//QUINDI, OGNI VOLTA CHE LO STATO DI UN GIOCATORE DIVENTA "LOCANDA" VIENE CREATO UN OGGETTO LOCANDA DIVERSO

public class Locanda implements Stato {
  
  private int count = 3;

  
  @Override
  public boolean continua(Giocatore g) {
	  
      if (g.isStop()) {
          g.setStato(Gioca.INSTANCE);
          g.setStop(false);
          return true;
      }
      
      count--;
      
      if (count == 0) {
    	  //IL GIOCATORE HA FINITO LA SOSTA
          g.setStato(Gioca.INSTANCE);
      }
      return false;
  }
}