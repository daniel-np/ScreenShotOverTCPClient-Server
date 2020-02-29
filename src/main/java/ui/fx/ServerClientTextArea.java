package ui.fx;

import javafx.scene.control.TextArea;

class ServerClientTextArea extends TextArea {

    ServerClientTextArea(String name, int rows, int columns) {
        this.setEditable(false);
        this.setText(name + ":");
        this.setPrefRowCount(rows);
        this.setPrefColumnCount(columns);
        this.setWrapText(true);
    }
}
