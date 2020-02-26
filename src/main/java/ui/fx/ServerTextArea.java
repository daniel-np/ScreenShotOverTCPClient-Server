package ui.fx;

import javafx.scene.control.TextArea;

import java.util.Observable;
import java.util.Observer;

public class ServerTextArea extends TextArea implements Observer {

    public ServerTextArea(String name, int rows, int columns) {
        this.setEditable(false);
        this.setText(name);
        this.setPrefRowCount(rows);
        this.setPrefColumnCount(columns);
        this.setWrapText(true);
    }

    @Override
    public void update(Observable observable, Object arg) {
        appendText("\n");
        appendText((String) arg);
    }
}
