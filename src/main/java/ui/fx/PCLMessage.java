package ui.fx;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PCLMessage {
    private String clientMessage;
    private String serverMessage;

    private PropertyChangeSupport support;

    PCLMessage() {
        support = new PropertyChangeSupport(this);
    }

    void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setClientMessage(String clientMessage) {
        support.firePropertyChange("clientMessage", this.clientMessage, clientMessage);
        this.clientMessage = clientMessage;
    }

    public void setServerMessage(String serverMessage) {
        support.firePropertyChange("serverMessage", this.serverMessage, serverMessage);
        this.serverMessage = serverMessage;
    }
}
