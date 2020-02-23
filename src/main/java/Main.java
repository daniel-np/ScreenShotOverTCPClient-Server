import ui.fx.MainStage;
import ui.text.TextMenu;

public class Main {

    public static void main(String[] args) {
        if(args.length > 0) {
            if (args[0].equals("-text") || args[0].equals("-t")){
                TextMenu tm = new TextMenu();
                tm.start();
            } else {
                System.out.println("Invalid argument");
                System.exit(0);
            }
        } else {
            MainStage.main(args);
        }
    }
}
