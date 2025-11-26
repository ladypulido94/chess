import chess.*;
import facade.ServerFacade;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http:localhost:8080";

        if (args.length > 0) {
            serverUrl = args[0];
        }

        ServerFacade serverFacade = new ServerFacade(serverUrl);

        PreLoginUI preLoginUI = new PreLoginUI();

        System.out.println("Goodbye!");
    }
}
