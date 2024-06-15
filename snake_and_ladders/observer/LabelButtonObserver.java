package observer;

import java.awt.*;
import javax.swing.*;


public class LabelButtonObserver extends AbstractButtonObserver {
	
	private Label[] labels;
	

    public LabelButtonObserver(JButton subject) {
        super(subject);
    }
    

    public Label[] getLabels() {
        return (new Label[] {labels[0], labels[1]});
    }

    
    public void setLabels(Label[] labels) {
        this.labels = labels;
    }

    
    @Override
    public void add(ButtonObserver o) {
        if (!(o instanceof LabelObserver)) {
            throw new IllegalArgumentException();
        }
        
        observers.add(o);
    }
}

