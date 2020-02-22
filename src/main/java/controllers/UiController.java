package controllers;

import service.tcp.Client;
import service.tcp.Server;

public class UiController {
    private Server server = new Server();
    private Client client = new Client();

    public void startServer(int timer) {
        server.start();
    }

    public void startClient(int timer) {
        if(!client.isRunning()) {
            client.start();
        } else {
            switch (timer) {
                case 0: client.setScreenShotTimer(30000);
                break;
                case 1: client.setScreenShotTimer(60000);
                break;
                case 2: client.setScreenShotTimer(120000);
            }
        }
    }

    public String getClientHostAddress() {
        return client.getHostAddress();
    }

    public boolean setClientHostAddress(String address) {
        return client.setHostAddress(address);
    }
}
