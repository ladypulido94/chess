package ui;

import chess.ChessGame;
import websocket.ServerMessageObserver;
import websocket.WebSocketCommunicator;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GamePlayUI implements ServerMessageObserver {
    private final WebSocketCommunicator webSocketCommunicator;
    private final String authToken;
    private final int gameID;
    private final String playerColor;
    private final Scanner scanner;
    private final boolean running;
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

}
