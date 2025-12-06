package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import websocket.ServerMessageObserver;
import websocket.WebSocketCommunicator;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GamePlayUI implements ServerMessageObserver {
    private final WebSocketCommunicator webSocketCommunicator;
    private final String authToken;
    private final int gameID;
    private final String playerColor;
    private final Scanner scanner;
    private boolean running;
    private ChessGame currentGame;

    public GamePlayUI(WebSocketCommunicator webSocketCommunicator, String authToken, int gameID,
                      String playerColor) {
        this.webSocketCommunicator = webSocketCommunicator;
        this.authToken= authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        scanner = new Scanner(System.in);
        running = true;

    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()){
            case LOAD_GAME -> handleLoadGame((LoadGameMessage) message);
            case ERROR -> handleError((ErrorMessage) message);
            case NOTIFICATION -> handleNotification((NotificationMessage) message);
        }

    }

    // Read - Eval - Print Loop
    public void run(){
        try{
            UserGameCommand connectCommand = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    authToken,
                    gameID
            );
            webSocketCommunicator.send(connectCommand);

        } catch (Exception e) {
            System.out.println("Failed to connect: " + e.getMessage());
            return;
        }

        //Game Loop
        while(running){
            System.out.println(SET_TEXT_COLOR_GREEN + "[IN_GAME] >>> " + RESET_TEXT_COLOR);
            String input = scanner.nextLine().trim();

            if(input.isEmpty()){
                continue;
            }

            String[] tokens = input.split(" ");
            String command = tokens[0].toLowerCase();

            try{
                switch (command) {
                    case "help" -> handleHelp();
                    case "redraw" -> drawBoard();
                    case "leave" -> handleLeave();
                    case "move" -> handleMove(tokens);
                    case "resign" -> handleResign();
                    case "highlight" -> handleHighlight(tokens);
                    default -> System.out.println("Unknown command. Type 'help' for options.");
                }

            } catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    // --------- METHOD HELPERS ----------

    //Store the game and redraw the board
    private void handleLoadGame(LoadGameMessage message){
        this.currentGame = message.getGame();
        drawBoard();
    }

    //Print Error in red
    private void handleError(ErrorMessage message){
        System.out.println(SET_TEXT_COLOR_RED + message.getErrorMessage() + RESET_TEXT_COLOR);

    }

    //Print notification in yellow
    private void handleNotification(NotificationMessage message){
        System.out.println(SET_TEXT_COLOR_YELLOW + message.getNotificationMessage() + RESET_TEXT_COLOR);

    }

    //Drawing the board from white or black perspective
    private void drawBoard(){
        if(currentGame == null){
            System.out.println("No game loaded yet.");
            return;
        }

        if("WHITE".equals(playerColor)){
            ChessBoard.drawWhiteBoard(currentGame);

        } else if("BLACK".equals(playerColor)){
            ChessBoard.drawBlackBoard(currentGame);
        } else {
            ChessBoard.drawWhiteBoard(currentGame);
        }
    }

    // Print how the commands should be used
    private void handleHelp(){
        System.out.println(SET_TEXT_COLOR_BLUE + "redraw" + SET_TEXT_COLOR_WHITE + " - redraw the chessboard");
        System.out.println(SET_TEXT_COLOR_BLUE + "leave" + SET_TEXT_COLOR_WHITE + " - leave the game");
        System.out.println(SET_TEXT_COLOR_BLUE + "move <FROM> <TO>" + SET_TEXT_COLOR_WHITE + " - make a move (e.d., move e2 e4)");
        System.out.println(SET_TEXT_COLOR_BLUE + "resign" + SET_TEXT_COLOR_WHITE + " - forfeit the game");
        System.out.println(SET_TEXT_COLOR_BLUE + "highlight <POSITION>" + SET_TEXT_COLOR_WHITE + " - show legal moves (e.g., highlight e4)");
        System.out.println(SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - show available commands");
    }

    //Leaves the interface
    private void handleLeave() throws Exception{
        UserGameCommand leaveCommand = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        webSocketCommunicator.send(leaveCommand);
        webSocketCommunicator.close();
        running = false;
        System.out.println("Left the game.");
    }

    private void handleResign() throws Exception{
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String input = scanner.nextLine().trim();

        if(input.equalsIgnoreCase("yes")){
            UserGameCommand commandResign = new UserGameCommand(
                    UserGameCommand.CommandType.RESIGN,
                    authToken,
                    gameID
            );
            webSocketCommunicator.send(commandResign);
            System.out.println("You have resigned from the game");
        } else {
            System.out.println("Resigned cancelled.");
        }

    }

    private void handleMove(String[] tokens) throws Exception{
        if(tokens.length != 3){
            System.out.println("USAGE: move <FROM> <TO> (e.g., move e2 e4)");
            return;
        }

        String startPositionCommand = tokens[1];
        String endPositionCommand = tokens[2];

        ChessPosition startPosition = parsePosition(startPositionCommand);
        ChessPosition endPosition = parsePosition(endPositionCommand);

        ChessMove move = new ChessMove(startPosition,endPosition,null);

        MakeMoveCommand moveCommand = new MakeMoveCommand(
                authToken,
                gameID,
                move
        );
        webSocketCommunicator.send(moveCommand);

    }

    private void handleHighlight(String[] tokens){

    }

    private ChessPosition parsePosition(String position){
        char columnChar = position.charAt(0);
        char rowChar = position.charAt(1);

        int column = (columnChar - 'a') + 1;
        int row = Character.getNumericValue(rowChar);

        return new ChessPosition(row, column);
    }

}
