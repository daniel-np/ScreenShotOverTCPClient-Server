package ui.text;

import controllers.UiController;

import java.io.IOException;
import java.util.Scanner;

public class TextMenu {
    private Scanner scan = new Scanner(System.in);
    private UiController uiController = new UiController();

    private UiController.intervalTimer serverIntervalTimer = UiController.intervalTimer.SHORT;
    private UiController.intervalTimer clientIntervalTimer = UiController.intervalTimer.SHORT;

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
                // Start server
                uiController.startServer(serverIntervalTimer);
                break;
            case "2":
                // Request screenshot
                uiController.startClient(clientIntervalTimer);
                break;
            case "3":
                // Settings
                settingsMenu();
                break;
            case "q":
                // quit
                System.exit(1);
            default:
                System.out.println("Invalid key");
        }
    }

    private void settingsMenu() {
        System.out.println("Settings");
        System.out.println("---------");
        System.out.println("(1) Set host ipv4 address");
        System.out.println("(2) Set screen-shot time interval");
        System.out.println("(b) Back");
        String settingsInput = scan.nextLine().toLowerCase();
        settingsChoices(settingsInput);
    }

    private void settingsChoices(String input) {
        switch (input) {
            case "1":
                // Change host address
                String hostAddress = uiController.getClientHostAddress();
                System.out.println("Current host ipv4 address: " + hostAddress);
                System.out.println("Set valid ipv4 or (b) to cancel: ");
                String addressInput = scan.nextLine().toLowerCase();
                if (addressInput.equals("b")) {
                    settingsMenu();
                }
                boolean isSuccessful = uiController.setClientHostAddress(addressInput);

                if (!isSuccessful) {
                    settingsChoices("1");
                }
                settingsMenu();
                break;
            case "2":
                // Change screenshot time interval
                timeIntervalMenu();
                break;
            case "b":
                // back
                menu();
            default:
                System.out.println("Invalid key");
        }
    }

    private void timeIntervalMenu() {
        System.out.println("Set time interval or (b) to cancel: ");
        System.out.println("---------");
        int counter = 1;
        for (UiController.intervalTimer value : UiController.intervalTimer.values()) {
            System.out.print(
                    "(" + counter++ + ") "
                            + value + "(" + value.getTime() / 1000 + "s)"
            );
            if (value == clientIntervalTimer) {
                System.out.print(" - Current");
            }
            System.out.println();
        }

        String timeIntervalInput = scan.nextLine().toLowerCase();
        timeIntervalChoices(timeIntervalInput);
    }

    private void timeIntervalChoices(String input) {
        switch (input) {
            case "1":
                // SHORT time
                serverIntervalTimer = UiController.intervalTimer.SHORT;
                clientIntervalTimer = UiController.intervalTimer.SHORT;
                break;
            case "2":
                // MEDIUM time
                serverIntervalTimer = UiController.intervalTimer.MEDIUM;
                clientIntervalTimer = UiController.intervalTimer.MEDIUM;
                break;
            case "3":
                // LONG time
                serverIntervalTimer = UiController.intervalTimer.LONG;
                clientIntervalTimer = UiController.intervalTimer.LONG;
                break;
            case "b":
                settingsMenu();
            default:
                System.out.println("Invalid key");
        }
        timeIntervalMenu();
    }
}
