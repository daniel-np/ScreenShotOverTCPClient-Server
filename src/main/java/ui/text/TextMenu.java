package ui.text;

import controllers.UiController;

import java.util.Scanner;

public class TextMenu {
    private Scanner scan = new Scanner(System.in);
    private UiController uiController = new UiController();

    public void start() {
        System.out.println("Welcome to ScreenShot App text client");
        System.out.println();
        menu();
    }

    private void menu() {
        System.out.println("Main Menu");
        System.out.println("---------");
        System.out.println("(1) Start server");
        System.out.println("(2) Request screenshot");
        System.out.println("(3) Settings");
        System.out.println("(q) Quit");
        menuInput();
    }

    private void menuInput() {
        while (true) {
            String input = scan.nextLine().toLowerCase();
            mainChoices(input);
        }
    }


    private void mainChoices(String input) {
        switch (input) {
            case "1":
                uiController.startServer(30000);
                break;
            case "2":
                uiController.startClient(30000);
                break;
            case "3":
                settings();
                break;
            case "q":
                System.exit(1);
                break;
        }
    }

    private void settings() {
        System.out.println("Settings");
        System.out.println("---------");
        System.out.println("(1) Set host ipv4 address");
        System.out.println("(b) Back");
        String settingsInput = scan.nextLine().toLowerCase();
        settingsChoices(settingsInput);
    }

    private void settingsChoices(String input) {
        switch (input) {
            case "1":
                String hostAddress = uiController.getClientHostAddress();
                System.out.println("Current host ipv4 address: " + hostAddress);
                System.out.println("Set valid ipv4 or (b) to cancel: ");
                String addressInput = scan.nextLine().toLowerCase();
                if(addressInput.equals("b")){
                    settings();
                }
                boolean isSuccessful = uiController.setClientHostAddress(addressInput);

                if(!isSuccessful) {
                    settingsChoices("1");
                }
                settings();
                break;
            case "b":
                menu();
                break;
        }
    }
}
