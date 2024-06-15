package observer;

import javax.swing.*;
import java.util.ArrayList;


public abstract class AbstractButtonObserver {
	
	public enum State {ATTIVO, NON_ATTIVO}
	
	protected State stato;
    protected JButton subject;
    protected ArrayList<ButtonObserver> observers;
    
    
    public abstract void add(ButtonObserver o);
    

    public AbstractButtonObserver(JButton subject) {
        this.subject = subject;
        this.stato = State.NON_ATTIVO;
        observers = new ArrayList<>();
    }
    
    
    public void remove(ButtonObserver o) {
        observers.remove(o);
    }

    
    public State getState() {
        return this.stato;
    }
    

    public void setState(State stato) {
        this.stato = stato;
    }
    
    
    public JButton getSubject() {
        return this.subject;
    }
    

    public void notifica() {
        for (ButtonObserver o: observers) {
            o.update();
        }
    }

}
