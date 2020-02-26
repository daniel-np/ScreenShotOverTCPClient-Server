package ui.fx;

import javafx.scene.control.TextArea;

import java.util.Observable;
import java.util.Observer;

public class ClientTextArea extends TextArea implements Observer {

    public ClientTextArea(String name, int rows, int columns){
        this.setEditable(false);
        this.setText(name);
        this.setPrefRowCount(rows);
        this.setPrefColumnCount(columns);
    }

    @Override
    public void update(Observable observable, Object arg) {
        appendText("\n");
        appendText((String) arg);
    }
}
