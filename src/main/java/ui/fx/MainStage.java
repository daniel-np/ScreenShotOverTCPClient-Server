package ui.fx;

import controllers.UiController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.screenShot.ScreenShotHandler;

import java.awt.image.BufferedImage;

public class MainStage extends Application {

    UiController uiController = new UiController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        //Setting title to the Stage
        stage.setTitle("ScreenShot App");

        //Adding scene to the stage
        Scene mainScene = mainScene();
        //screenShotGroup.setFill(Color.BLACK);
        stage.setScene(mainScene);
        stage.setResizable(false);

        //Displaying the contents of the stage
        stage.show();
    }

    private Scene mainScene() {
        VBox vBox = new VBox(clientServerControlGroup(), screenShotGroup());
        vBox.setSpacing(10);

        return new Scene(vBox);
    }

    private Group screenShotGroup() {
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
        HBox hBox = new HBox(imageView);
        hBox.setAlignment(Pos.CENTER);

        //Creating a scene object
        return new Group(hBox);
    }

    private Group clientServerControlGroup() {

        // Output Area
        int rows = 10;
        int cols = 20;
        ClientTextArea clientTextArea = new ClientTextArea("Client", rows, cols);
        ServerTextArea serverTextArea = new ServerTextArea("Server", rows, cols);
        HBox outputHBox = new HBox(clientTextArea, serverTextArea);
        outputHBox.setSpacing(50);
        // Control Area
        Label addressLabel = new Label("Address");
        TextField addressTextField = new TextField(uiController.getClientHostAddress());
        addressTextField.setPrefColumnCount(9);
        addressLabel.setLabelFor(addressTextField);

        Label timerChoiceBoxLabel = new Label("Set timer");
        ObservableList<UiController.intervalTimer> observableList =
                FXCollections.observableArrayList(UiController.intervalTimer.values());

        ChoiceBox<UiController.intervalTimer> choiceBox = new ChoiceBox<>(observableList);
        timerChoiceBoxLabel.setLabelFor(choiceBox);

        choiceBox.setOnAction((a)->{System.out.println(choiceBox.getValue().getTime());});



        HBox controlHBox = new HBox(
                addressLabel,
                addressTextField,
                timerChoiceBoxLabel,
                choiceBox);
        controlHBox.setSpacing(10);
        controlHBox.setAlignment(Pos.CENTER);
        // Parent VBox
        VBox mainBox = new VBox(outputHBox, controlHBox);
        mainBox.setSpacing(10);

        Platform.runLater(mainBox::requestFocus);
        return new Group(mainBox);
    }

    private Image takeScreenShot() {
        BufferedImage screenShot  = ScreenShotHandler.captureWholeScreen();
        String path = System.getProperty("user.home")+"/ScreenShotHandler.png";
        ScreenShotHandler.saveImage(path, screenShot);

        return new Image("file:" + path);
    }
}
