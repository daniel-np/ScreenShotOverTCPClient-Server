package service.tcp;

import service.screenShot.ScreenShotHandler;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Observable;

public class Server extends Observable implements Runnable{

    private Thread t;
    private int timer;
    private boolean isRunning = false;
    private BufferedImage bufferedImage;
    private int timerInterval;
    private Thread screenShotThread;

    public Server() {

    }

    public void start(int timerInterval) {
        this.timerInterval = timerInterval;
        if (t == null) {
            t = new Thread(this);
            t.setDaemon(true);
            t.setName("Server Thread");
            t.start();
        }
    }

    private void startScreenShotThread(int timerInterval) {
        this.screenShotThread = new Thread(() -> {
            while (isRunning) {
                try {
                    bufferedImage = ScreenShotHandler.captureWholeScreen();
                    messageOut("Screen captured");
                    Thread.sleep(timerInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            messageOut("Screen-shot handler stopped");
        });
        this.screenShotThread.setName("ScreenShot Thread");
        this.screenShotThread.setDaemon(true);
        this.screenShotThread.start();
    }

    @Override
    public void run() {
        this.isRunning = true;

        ServerSocket server;
        Socket connection;
        String message;
        bufferedImage = ScreenShotHandler.captureWholeScreen();
        messageOut("Screen captured");
        startScreenShotThread(timerInterval);

        try {
            server = new ServerSocket(5194, 100);
            messageOut("Server started...");

            while(isRunning) {
                // Establishing connection
                connection = server.accept();
                messageOut("Connection received from: " + connection.getInetAddress().getHostAddress());

                // Create outputStream
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream());
                DataOutputStream outputStream = new DataOutputStream(bufferedOutputStream);

                // Image screenshot handling
                long startTime = System.nanoTime();
                boolean success = ScreenShotHandler.streamOutImage(Objects.requireNonNull(bufferedImage), outputStream);
                if (success) {
                    outputStream.close();
                    // Duration
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;
                    messageOut("Transaction completed in " + duration + "ms!");
                } else {
                    messageOut("Transaction failed!");
                }
            }
        }catch (EOFException eof) {
            message = "Client terminated connection";
            messageOut(message);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        messageOut("Server stopped");
    }

    private void messageOut(String message) {
        System.out.println("Server: " + message);
        setChanged();
        notifyObservers(message);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopServer() {
        this.isRunning = false;
    }
}
