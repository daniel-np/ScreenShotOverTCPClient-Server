package service.tcp;

import javafx.scene.image.Image;
import org.apache.commons.validator.routines.InetAddressValidator;
import service.screenShot.PCLScreenShot;
import service.screenShot.ScreenShotHandler;
import ui.fx.PCLMessage;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Objects;

public class Client implements Runnable {

    private Thread t;
    private String hostAddress = "127.0.0.1";
    private boolean transferInProgress = false;
    private boolean isRunning = false;
    private final int DEFAULT_TIMER = 30000;
    private int screenShotTimer = DEFAULT_TIMER;
    private InetAddressValidator validator = new InetAddressValidator();
    private PCLScreenShot observableScreenShot;
    private PCLMessage observableClientMessage;

    public Client(PCLScreenShot observableScreenShot, PCLMessage observableClientMessage) {
        this.observableScreenShot = observableScreenShot;
        this.observableClientMessage = observableClientMessage;
    }

    public Client() {

    }

    public void start(int screenShotTimer) {
        this.screenShotTimer = screenShotTimer;
        if (t == null) {
            t = new Thread(this);
            t.setDaemon(true);
            t.setName("Client Thread");
            t.start();
        }
    }

    @Override
    public void run() {
        this.isRunning = true;
        String message;
        message = "Client started...";
        messageOut(message);
        connect(0);
    }

    private void connect(int connectionRefusedCounter) {
        BufferedImage bufferedImage;
        String message;
        try {
            while (isRunning) {

                transferInProgress = true;
                // Setup socket
                InetAddress host = InetAddress.getByName(hostAddress);
                Socket clientSocket = new Socket(host, 5194);
                message = "Connected to : " + clientSocket.getInetAddress().getHostAddress();
                messageOut(message);

                // Setup input stream
                InputStream inputStream = clientSocket.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

                message = "Starting transaction...";
                messageOut(message);

                long startTime = System.nanoTime();

                int width = dataInputStream.readInt();
                int height = dataInputStream.readInt();

                bufferedImage = ScreenShotHandler.streamInImage(dataInputStream, width, height, 1);
                if (!Objects.isNull(observableScreenShot)) {
                    // Save and notify observer
                    observableScreenShot.setScreenShot(convertBufferedImageThroughSaving(bufferedImage));
                } else {
                    // save only
                    saveImage(bufferedImage);
                }
                transferInProgress = false;

                // Duration
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000;
                message = "Transaction completed in " + duration + "ms!";
                messageOut(message);

                //image = ScreenShotHandler.constructImage(rgb, 1);
                String path = Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                String fileName = endTime / 1000000 + "ScreenShot.png";
                String filePath = path + fileName;
                ScreenShotHandler.saveImage(filePath, bufferedImage);

                message = "ScreenShot Saved: " + fileName;
                messageOut(message);
                messageOut("At " + path);

                clientSocket.close();
                waitForScreenshot(screenShotTimer);
            }
        } catch (IOException | URISyntaxException e) {
            if (e.getMessage().contains("Connection refused")) {
                messageOut("Connection refused" + "(" + ++connectionRefusedCounter + ")" + "...");
                if (connectionRefusedCounter < 10) {
                    try {
                        Thread.sleep(1000 * (int)(Math.pow(2, (double) connectionRefusedCounter)));
                        messageOut("Retrying connection");
                        connect(connectionRefusedCounter);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    messageOut("Stopping client...");
                }
            } else {
                e.printStackTrace();
            }
        }
        messageOut("Client stopped");
    }

    private Image convertBufferedImageThroughSaving(BufferedImage screenShot) {
        String path = System.getProperty("user.home") + "/ScreenShot " + System.currentTimeMillis() / 1000 + ".png";
        ScreenShotHandler.saveImage(path, screenShot);

        return new Image("file:" + path);
    }

    private void saveImage(BufferedImage screenShot) {
        String path = System.getProperty("user.home") + "/ScreenShot " + System.currentTimeMillis() / 1000 + ".png";
        ScreenShotHandler.saveImage(path, screenShot);
    }

    private void messageOut(String message) {
        System.out.println("Client: " + message);
        observableClientMessage.setClientMessage(message);
    }

    private synchronized void waitForScreenshot(int timer) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public String getHostAddress() {
        return this.hostAddress;
    }

    public boolean setHostAddress(String address) {
        if (!transferInProgress) {
            if (validator.isValidInet4Address(address)) {
                this.hostAddress = address;
                messageOut("Host address set to : " + this.hostAddress);
                return true;
            } else {
                messageOut("Invalid ip-address!");
                return false;
            }
        } else {
            messageOut("Transfer in progress!");
            return false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopClient() {
        this.isRunning = false;
    }

    public int getScreenShotTimer() {
        return screenShotTimer;
    }

    public void setScreenShotTimer(int screenShotTimer) {
        this.screenShotTimer = screenShotTimer;
    }
}
