package ui.fx;

import controllers.UiController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.screenShot.PCLScreenShot;
import service.screenShot.ScreenShotHandler;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainStage extends Application {

    private UiController uiController;
    private ImageView screenShotImageView = new ImageView();

    public MainStage() {
        PCLScreenShotChannel screenShotChannel = new PCLScreenShotChannel();
        PCLScreenShot observableScreenShot = new PCLScreenShot();
        observableScreenShot.addPropertyChangeListener(screenShotChannel);
        uiController = new UiController(observableScreenShot);
    }

    private class PCLScreenShotChannel implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            this.setScreenShot((Image) propertyChangeEvent.getNewValue());
        }

        private void setScreenShot(Image screenShot) {
            screenShotImageView.setImage(screenShot);
        }
    }

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
        //Setting the position of the image
        screenShotImageView.setX(50);
        screenShotImageView.setY(25);

        //setting the fit height and width of the image view
        screenShotImageView.setFitHeight(450);
        screenShotImageView.setFitWidth(500);

        //Setting the preserve ratio of the image view
        screenShotImageView.setPreserveRatio(true);

        screenShotImageView.imageProperty();

        //Creating a Group object
        HBox hBox = new HBox(screenShotImageView);
        StackPane sp = new StackPane();

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
        choiceBox.setValue(uiController.getScreenShotTimer());
        timerChoiceBoxLabel.setLabelFor(choiceBox);

        choiceBox.setOnAction((actionEvent)->{uiController.setScreenShotTimer(choiceBox.getValue());});

        Button startButton = new Button("Start");
        startButton.setOnAction((actionEvent)-> {
            if(addressTextField.getText().equals("127.0.0.1")){
                // If localHost start both server and client
                uiController.startServer(choiceBox.getValue());
                uiController.startClient(choiceBox.getValue());
            } else if(addressTextField.getText().equals("")){
                // If no IP start only server
                uiController.startServer(choiceBox.getValue());
            } else {
                // Else start only client
                uiController.startClient(choiceBox.getValue());
            }
        });

        HBox controlHBox = new HBox(
                addressLabel,
                addressTextField,
                timerChoiceBoxLabel,
                choiceBox,
                startButton);
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
        String path = System.getProperty("user.home")+"/ScreenShot " + System.currentTimeMillis()/1000 + ".png";
        ScreenShotHandler.saveImage(path, screenShot);

        return new Image("file:" + path);
    }
}
