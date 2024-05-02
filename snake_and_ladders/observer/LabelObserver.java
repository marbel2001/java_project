package observer;

import java.awt.*;


public class LabelObserver extends ButtonObserver {
	
	private LabelButtonObserver subject;
    private Label[] labels;
    

    public LabelObserver(LabelButtonObserver subject) {
        super();
        this.subject = subject;
    }

    
    public Label[] getLabels() {
        return labels;
    }

    
    @Override
    public void update() {
        stato = subject.getState();
        labels = subject.getLabels();
    }

}
