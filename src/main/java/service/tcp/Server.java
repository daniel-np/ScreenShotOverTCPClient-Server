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
    long startTime, endTime, duration;

    public Server() {

    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run() {
        this.isRunning = true;

        ServerSocket server;
        Socket connection;
        int counter = 1;
        String message;
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
                BufferedImage image = Screenshot.captureWholeScreen();
                messageOut("Screen captured");
                startTime = System.nanoTime();
                boolean success = Screenshot.streamOutImage(image, outputStream);
                if (success) {
                    outputStream.close();
                    // Duration
                    endTime = System.nanoTime();
                    duration = (endTime - startTime) /1000000;
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
