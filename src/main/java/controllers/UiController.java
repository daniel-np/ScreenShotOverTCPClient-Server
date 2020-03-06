package controllers;

import service.screenShot.PCLScreenShot;
import service.tcp.Client;
import service.tcp.Server;

public class UiController {
    private Server server;
    private Client client;

    public UiController(PCLScreenShot observableScreenShot) {
        this.client = new Client(observableScreenShot);
        this.server = new Server();
    }

    public UiController() {
        this.client = new Client();
        this.server = new Server();
    }

    public enum intervalTimer {
        SHORT(30000),
        MEDIUM(60000),
        LONG(120000);

        intervalTimer(int i) {
            this.time = i;
        }

        private int time;

        public int getTime() {
            return time;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString().toLowerCase());
            sb.replace(0,1, sb.substring(0,1).toUpperCase());
            sb.append("(").append(time/1000).append("s)");

            return sb.toString();
        }
    }

    public void startServer(intervalTimer intervalTimer) {
        server.start(intervalTimer.getTime());
    }

    public void stopServer() {
        if(server.isRunning()) {
            server.stopServer();
        }
    }

    public void startClient(intervalTimer intervalTimer) {
        client.start(intervalTimer.getTime());
    }

    public void stopClient() {
        if(client.isRunning()) {
            client.stopClient();
        }
    }

    public String getClientHostAddress() {
        return client.getHostAddress();
    }

    public boolean setClientHostAddress(String address) {
        return client.setHostAddress(address);
    }

    public void setScreenShotTimer(intervalTimer timer) {
        client.setScreenShotTimer(timer.getTime());
        server.setTimer(timer.getTime());
    }

    public intervalTimer getScreenShotTimer() {
        int timer = client.getScreenShotTimer();

        intervalTimer returnValue = intervalTimer.SHORT;
        for (intervalTimer intTimer : intervalTimer.values()) {
            if(intTimer.getTime() == timer) {
                returnValue = intTimer;
                break;
            }
        }
        return returnValue;
    }
}
