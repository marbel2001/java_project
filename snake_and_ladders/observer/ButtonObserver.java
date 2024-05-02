package observer;


public class ButtonObserver implements Observer {

	protected AbstractButtonObserver.State stato;
	

    public ButtonObserver() {
        stato = AbstractButtonObserver.State.NON_ATTIVO;
    }
    

    public AbstractButtonObserver.State getState() {
        return stato;
    }
    

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	

}
