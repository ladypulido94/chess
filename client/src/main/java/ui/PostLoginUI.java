package ui;

import facade.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade facade;
    private final AuthData authData;
    private final Scanner scanner;
    private boolean running;

    private List<GameData> games = new ArrayList<>();

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
        System.out.println("join <ID> [WHITE|BLACK] - a game");
        System.out.println("observe <ID> - a game");
        System.out.println("logout - when you are done");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }

    private void handleCreate(String[] tokens){
        if(tokens.length != 2){
            System.out.println("Usage: create <NAME>");
            return;
        }

        try{
            facade.createGame(authData.authToken(), tokens[1]);
            System.out.println("Game created successfully!");
            games = new ArrayList<>(facade.listAllGames(authData.authToken()));

        } catch (Exception e){
            System.out.println("Create failed: " + e.getMessage());
        }
    }

    private void handleList(){
        try{
            games = new ArrayList<>(facade.listAllGames(authData.authToken()));

            if(games.isEmpty()){
                System.out.println("No games available.");
                return;
            }

            for(int i = 0; i < games.size(); i++){
                GameData game = games.get(i);
                int number = i + 1;
                String whiteUsername;
                String blackUsername;

                if(game.whiteUsername() != null){
                    whiteUsername = game.whiteUsername();
                } else {
                    whiteUsername = "(empty)";
                }

                if(game.blackUsername() != null){
                    blackUsername = game.blackUsername();
                } else {
                    blackUsername = "(empty)";
                }

                System.out.println(number + ". " + game.gameName() +
                        " - White: " + whiteUsername + ", Black: " + blackUsername);
            }

        } catch (Exception e){
            System.out.println("List failed: " + e.getMessage());
        }
    }

    private void handleJoin(String[] tokens){
        if(tokens.length != 3){
            System.out.println("Usage: join <ID> [WHITE|BLACK]");
            return;
        }

        if(games.isEmpty()){
            System.out.println("No games available. User 'list' first.");
            return;
        }

        try{
            int gameNumber = Integer.parseInt(tokens[1]);

            if(gameNumber < 1 || gameNumber > games.size()){
                System.out.println("Invalid game number.");
                return;
            }

            String playerColor = tokens[2].toUpperCase();

            if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
                System.out.println("Invalid color. use WHITE or BLACK.");
                return;
            }

            GameData game = games.get(gameNumber - 1);
            facade.joinGame(authData.authToken(), game.gameID(), playerColor);
            System.out.println("Joined game as " + playerColor);

            if (playerColor.equals("WHITE")){
                ChessBoard.drawWhiteBoard(game.game());
            } else {
                ChessBoard.drawBlackBoard(game.game());
            }

        } catch (Exception e){
            System.out.println("Join failed: " + e.getMessage());
        }

    }

    private void handleObserve(String[] tokens){
        if(tokens.length != 2){
            System.out.println("Usage: observe <ID>");
            return;
        }

        if(games.isEmpty()){
            System.out.println("No games available. Use 'list' first");
            return;
        }

        try{
            int gameNumber = Integer.parseInt(tokens[1]);
            if(gameNumber < 1 || gameNumber > games.size()){
                System.out.println("Invalid game number.");
                return;
            }
            GameData game = games.get(gameNumber - 1);
            System.out.println("Observing game: " + game.gameName());

            ChessBoard.drawWhiteBoard(game.game());
        } catch (Exception e) {
            System.out.println("Observe failed: " + e.getMessage());
        }

    }

    private void handleLogout(){
        try{
            facade.logout(authData.authToken());
            System.out.println("Logged out");
            running = false;

        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }

    }

}
