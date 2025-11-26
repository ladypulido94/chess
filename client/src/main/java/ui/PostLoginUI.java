package ui;

import facade.ServerFacade;
import model.AuthData;

import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade facade;
    private final AuthData authData;
    private final Scanner scanner;
    private boolean running;

    public PostLoginUI(ServerFacade facade, AuthData authData){
        this.facade = facade;
        this.authData = authData;
        scanner = new Scanner(System.in);
        running = true;
    }

    public void run(){
        while(running){
            String input = scanner.nextLine().trim();
            String[] tokens = input.split(" ");
            String command = tokens[0].toLowerCase();

            switch (command) {
                case "create" -> handleCreate(tokens);
                case "list" -> handleList();
                case "join" -> handleJoin(tokens);
                case "observe" -> handleObserve(tokens);
                case "logout" -> handleLogout();
                case "quit" -> running = false;
                case "help" -> handleHelp();
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        }
    }

    private void handleHelp(){
        System.out.println("create <NAME> - a game");
        System.out.println("list - games");
        System.out.println("join <ID> - a game");
        System.out.println("observe <ID> - a game");
        System.out.println("logout - when you are done");
        System.out.println("quit - playing");
    }

    private void handleCreate(String[] tokens){

    }

    private void handleList(){

    }

    private void handleJoin(String[] tokens){

    }

    private void handleObserve(String[] tokens){

    }

    private void handleLogout(){

    }

}
