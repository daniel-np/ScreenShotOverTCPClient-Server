package ui.fx;

import controllers.UiController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.InetAddressValidator;
import service.screenShot.PCLScreenShot;
import service.screenShot.ScreenShotHandler;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainStage extends Application {

    private UiController uiController;
    private ImageView screenShotImageView = new ImageView();
    private ClientTextArea clientTextArea;
    private ServerTextArea serverTextArea;

    public MainStage() {


        PCLScreenShot observableScreenShot = new PCLScreenShot();
        observableScreenShot.addPropertyChangeListener(e -> screenShotImageView.setImage((Image) e.getNewValue()));

        PCLMessage observableClientMessage = new PCLMessage();
        observableClientMessage.addPropertyChangeListener(e -> clientTextArea.appendText("\n" + e.getNewValue()));

        PCLMessage observableServeMessage = new PCLMessage();
        observableServeMessage.addPropertyChangeListener(e -> serverTextArea.appendText("\n" + e.getNewValue()));

        uiController = new UiController(observableScreenShot, observableClientMessage, observableServeMessage);
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

        //setting the fit height and width of the image view
        screenShotImageView.setFitHeight(450);
        screenShotImageView.setFitWidth(622);

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
        clientTextArea = new ClientTextArea("Client", rows, cols);
        serverTextArea = new ServerTextArea("Server", rows, cols);
        HBox outputHBox = new HBox(clientTextArea, serverTextArea);
        outputHBox.setSpacing(50);

        // Control Area
        // Address text field
        Label addressLabel = new Label("Address");
        TextField addressTextField = new TextField(uiController.getClientHostAddress());
        addressTextField.setPrefColumnCount(9);
        addressLabel.setLabelFor(addressTextField);

        // Choice
        Label timerChoiceBoxLabel = new Label("Set timer");
        ObservableList<UiController.intervalTimer> observableList =
                FXCollections.observableArrayList(UiController.intervalTimer.values());

        ChoiceBox<UiController.intervalTimer> choiceBox = new ChoiceBox<>(observableList);
        choiceBox.setValue(uiController.getScreenShotTimer());
        timerChoiceBoxLabel.setLabelFor(choiceBox);

        choiceBox.setOnAction((actionEvent) -> uiController.setScreenShotTimer(choiceBox.getValue()));

        // Start Button
        final Boolean[] isStartButton = {true};
        Button startButton = new Button("Start");
        startButton.setOnAction((actionEvent) -> {
            if (isStartButton[0]) {
                if (isValidAddress(addressTextField.getText())) {
                    addressTextField.setDisable(true);
                    choiceBox.setDisable(true);
                    decideStartup(addressTextField.getText(), choiceBox.getValue());
                    // Set startButton to be a Stop Button
                    startButton.setText("Stop");
                    isStartButton[0] = false;
                } else {
                    String addressString = addressTextField.getText();
                    addressTextField.clear();
                    addressTextField.setPromptText("INVALID");
                    addressTextField.setOnMouseClicked(e -> {
                        addressTextField.setText(addressString);
                        addressTextField.positionCaret(addressString.length());
                        addressTextField.setOnMouseClicked(ie -> {
                        });
                    });
                }
            } else {
                uiController.stopClient();
                uiController.stopServer();
                addressTextField.setDisable(false);
                choiceBox.setDisable(false);
                startButton.setText("Start");
                isStartButton[0] = true;
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
        Separator separator = new Separator();
        VBox mainBox = new VBox(outputHBox, controlHBox, separator);
        mainBox.setSpacing(10);

        Platform.runLater(mainBox::requestFocus);
        return new Group(mainBox);
    }

    private boolean isValidAddress(String addressTextField) {
        InetAddressValidator inetAddressValidator = new InetAddressValidator();
        return addressTextField.equals("") || inetAddressValidator.isValidInet4Address(addressTextField);
    }

    private void decideStartup(String addressTextField, UiController.intervalTimer choiceBoxValue) {
        if (addressTextField.equals("127.0.0.1")) {
            // If localHost start both server and client
            uiController.startServer(choiceBoxValue);
            uiController.startClient(choiceBoxValue);
        } else if (addressTextField.equals("")) {
            // If no IP start only server
            uiController.startServer(choiceBoxValue);
        } else {
            // Else start only client
            uiController.startClient(choiceBoxValue);
        }
    }

    private Image takeScreenShot() {
        BufferedImage screenShot = ScreenShotHandler.captureWholeScreen();
        String path = System.getProperty("user.home") + "/ScreenShot " + System.currentTimeMillis() / 1000 + ".png";
        ScreenShotHandler.saveImage(path, screenShot);

        return new Image("file:" + path);
    }
}
