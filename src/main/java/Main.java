import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.screenShot.Screenshot;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ui.text.TextMenu;

import java.awt.image.BufferedImage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {

        //Setting title to the Stage
        stage.setTitle("ScreenShot App");

        //Adding scene to the stage
        Scene screenShotScene = mainScene();
        screenShotScene.setFill(Color.BEIGE);
        stage.setScene(screenShotScene);

        //Displaying the contents of the stage
        stage.show();
    }

    private Scene mainScene() {
        //Creating an image

        //Setting the image view
        ImageView imageView = new ImageView(takeScreenShot());

        //Setting the position of the image
        imageView.setX(50);
        imageView.setY(25);

        //setting the fit height and width of the image view
        imageView.setFitHeight(450);
        imageView.setFitWidth(500);

        //Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);

        //Creating a Group object
        Group root = new Group(imageView);

        //Creating a scene object
        return new Scene(root, 600, 400);
    }


    public static void main(String[] args) {
        if(args.length > 0) {
            if (args[0].equals("-text") || args[0].equals("-t")){
                TextMenu tm = new TextMenu();
                tm.start();
            } else {
                System.out.println("Invalid argument");
                System.exit(0);
            }
        }
        //launch();
    }

    private Image takeScreenShot() {
        BufferedImage screenShot  = Screenshot.captureWholeScreen();
        String path = System.getProperty("user.home")+"/Screenshot.png";
        Screenshot.saveImage(path, screenShot);

        return new Image("file:" + path);
    }

}
