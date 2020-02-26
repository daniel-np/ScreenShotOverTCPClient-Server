package service.tcp;

import org.apache.commons.validator.routines.InetAddressValidator;
import service.screenShot.ScreenShotHandler;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Observable;

public class Client extends Observable implements Runnable{

    private Thread t;
    private String hostAddress = "127.0.0.1";
    private boolean transferInProgress = false;
    private long startTime, endTime, duration;
    private boolean isRunning = false;
    private final int DEFAULTTIMER = 30000;
    private int screenShotTimer = DEFAULTTIMER;
    private String path = "";
    private InetAddressValidator validator = new InetAddressValidator();

    public Client() {

    }

    public void start(int screenShotTimer) {
        this.screenShotTimer = screenShotTimer;
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run() {
        this.isRunning = true;
        String message;
        BufferedImage image;
        try {
            message = "Client started...";
            messageOut(message);

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

                startTime = System.nanoTime();

                int width = dataInputStream.readInt();
                int height = dataInputStream.readInt();

                image = ScreenShotHandler.streamInImage(dataInputStream, width, height, 1);

                transferInProgress = false;

                // Duration
                endTime = System.nanoTime();
                duration = (endTime-startTime) / 1000000;
                message = "Transaction completed in " + duration + "ms!";
                messageOut(message);

                //image = ScreenShotHandler.constructImage(rgb, 1);
                path = Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                String fileName = endTime/1000000 + "ScreenShot.png";
                String filePath = path + fileName;
                ScreenShotHandler.saveImage(filePath, image);

                message = "ScreenShot Saved: " + fileName;
                messageOut(message);
                messageOut("At " + path);

                clientSocket.close();
                waitForScreenshot(screenShotTimer);
            }

        } catch (IOException | URISyntaxException e){
            e.printStackTrace();
        }
    }

    private void messageOut(String message) {
        System.out.println("Client: " + message);
        setChanged();
        notifyObservers(message);
    }

    private synchronized void waitForScreenshot(int timer) {
        try {
            t.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public String getHostAddress() {
        return this.hostAddress;
    }

    public boolean setHostAddress(String address) {
        if(!transferInProgress) {
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
        try {
            this.isRunning = false;
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getScreenShotTimer() {
        return screenShotTimer;
    }

    public void setScreenShotTimer(int screenShotTimer) {
        this.screenShotTimer = screenShotTimer;
    }
}
