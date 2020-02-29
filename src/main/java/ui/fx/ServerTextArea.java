package ui.fx;

import java.util.Observable;
import java.util.Observer;

public class ServerTextArea extends ServerClientTextArea implements Observer {

    ServerTextArea(String name, int rows, int columns) {
        super(name, rows, columns);
    }

    @Override
    public void update(Observable observable, Object arg) {
        appendText("\n");
        appendText((String) arg);
    }
}
