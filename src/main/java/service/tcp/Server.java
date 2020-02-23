package service.tcp;

import service.screenShot.Screenshot;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

public class Server extends Observable implements Runnable{

    private Thread t;
    private int timer;
    private boolean isRunning = false;
    private BufferedImage bufferedImage;
    private Thread screenShotThread;
    private int timerInterval;

    public Server() {

    }

    public void start(int timerInterval) {
        this.timerInterval = timerInterval;
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    private void startScreenShotThread(int timerInterval) {
        screenShotThread = new Thread(()->{
            while(true) {
                try {
                    bufferedImage = Screenshot.captureWholeScreen();
                    Thread.sleep(timerInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        screenShotThread.start();
    }

    @Override
    public void run() {
        this.isRunning = true;

        ServerSocket server;
        Socket connection;
        String message;
        try {
            server = new ServerSocket(5194, 100);
            messageOut("Server started...");
            startScreenShotThread(timerInterval);

            while(isRunning) {
                // Establishing connection
                connection = server.accept();
                messageOut("Connection received from: " + connection.getInetAddress().getHostAddress());

                // Create outputStream
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream());
                DataOutputStream outputStream = new DataOutputStream(bufferedOutputStream);

                // Image screenshot handling
                messageOut("Screen captured");
                long startTime = System.nanoTime();
                assert bufferedImage != null;
                boolean success = Screenshot.streamOutImage(bufferedImage, outputStream);
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
        try {
            this.isRunning = false;
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
