package ui;

import chess.ChessGame;
import websocket.ServerMessageObserver;
import websocket.WebSocketCommunicator;
import websocket.messages.ServerMessage;

import java.util.Scanner;

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

    }

    public void run(){

    }
}
