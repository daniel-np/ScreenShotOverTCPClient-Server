package ui.fx;

import controllers.UiController;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import service.screenShot.Screenshot;

import java.awt.image.BufferedImage;

public class MainStage extends Stage {

    UiController uiController = new UiController();

    public MainStage() {

    }

    private Image takeScreenShot() {
        BufferedImage screenShot  = Screenshot.captureWholeScreen();
        String path = System.getProperty("user.home")+"/Screenshot.png";
        Screenshot.saveImage(path, screenShot);

        return new Image("file:" + path);
    }
}
