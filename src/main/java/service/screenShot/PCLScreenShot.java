package service.screenShot;

import javafx.scene.image.Image;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PCLScreenShot {
    private Image screenShot;

    private PropertyChangeSupport support;

    public PCLScreenShot() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setScreenShot(Image screenShot) {
        support.firePropertyChange("screenShot", this.screenShot, screenShot);
        this.screenShot = screenShot;
    }

}
