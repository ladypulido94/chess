package ui;

import facade.ServerFacade;
import model.AuthData;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginUI {
    private final ServerFacade facade;
    private final Scanner scanner;
    private boolean running;

    public PreLoginUI(ServerFacade facade){
        this.facade = facade;
        scanner = new Scanner(System.in);
        running = true;
    }

    public void run(){
        System.out.println("♕ Welcome to 240 Chess. Type 'help' to get started. ♕");

        while(running){
            System.out.print(SET_TEXT_COLOR_GREEN + "[LOGGED_OUT] >>> " + RESET_TEXT_COLOR);

            String input = scanner.nextLine().trim();

            String[] tokens = input.split(" ");
            String command = tokens[0].toLowerCase();

            switch (command) {
                case "register" -> handleRegister(tokens);
                case "login" -> handleLogin(tokens);
                case "quit" -> running = false;
                case  "help" -> handleHelp();
                default -> System.out.println("Unknown command.Type 'help' for options.");
            }
        }
    }

    private void handleHelp(){
        System.out.println( SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" +
                SET_TEXT_COLOR_WHITE + " - to create an account");
        System.out.println( SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> " +
                SET_TEXT_COLOR_WHITE + " - to play chess");
        System.out.println(SET_TEXT_COLOR_BLUE + "quit " +
                SET_TEXT_COLOR_WHITE +" - playing chess");
        System.out.println(SET_TEXT_COLOR_BLUE + "help" +
                SET_TEXT_COLOR_WHITE +" - with possible commands");
    }

    private void handleRegister(String[] tokens) {
        if(tokens.length != 4){
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }

        try{
            AuthData authData = facade.register(tokens[1], tokens[2], tokens[3]);
            System.out.println("Registered and logged in as " + authData.username());
            PostLoginUI postLoginUI = new PostLoginUI(facade, authData);
            postLoginUI.run();

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void handleLogin(String[] tokens){
        if (tokens.length != 3){
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }

        try {
            AuthData authData = facade.login(tokens[1], tokens[2]);
            System.out.println("Logged in as: " + authData.username());
            PostLoginUI postLoginUI = new PostLoginUI(facade, authData);
            postLoginUI.run();

        } catch (Exception e){
            System.out.println("Login failed: " + e.getMessage());
        }
    }

}
