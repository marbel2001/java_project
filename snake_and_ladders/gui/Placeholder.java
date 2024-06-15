package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;


public class Placeholder {
	
    public static FocusListener getFocusListener(JTextField field, String placeholder) {
        return new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText(null);
                    field.setForeground(Color.BLACK);
                    field.setFont(field.getFont().deriveFont(Font.PLAIN));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText() == null || field.getText().equals("")) {
                    field.setFont(field.getFont().deriveFont(Font.ITALIC));
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        };
    }

    public static void setPlaceholder(JTextField field, String placeholder) {
        field.setFont(field.getFont().deriveFont(Font.ITALIC));
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
    }
}
