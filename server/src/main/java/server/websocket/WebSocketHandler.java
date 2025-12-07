package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final Map<String, Session> connections = new ConcurrentHashMap<>();
    private final Map<String, Integer> connectionGames = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    @OnWebSocketConnect
    public void onConnect(WsConnectContext ctx){
        System.out.println("Websocket connected: " + ctx.session);
    }

    @OnWebSocketClose
    public void onClose(WsCloseContext ctx){
        System.out.println("WebSocket closed: " + ctx.session);

    }

    @OnWebSocketMessage
    public void onMessage(WsMessageContext ctx){
        System.out.println("Received: " + ctx.message());
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

            switch(command.getCommandType()){
                case CONNECT -> handleConnect(ctx.session, command);
                case MAKE_MOVE -> handleMakeMove(ctx.session, command);
                case LEAVE -> handleLeave(ctx.session, command);
                case RESIGN -> handleResign(ctx.session, command);
            }

        } catch (Exception e) {
            try {
                sendMessage(ctx.session, new ErrorMessage("Error: " + e.getMessage()));
            } catch (IOException ex) {
                System.err.println("Failed to send error message: " + ex.getMessage());
            }
        }
    }

    @OnWebSocketError
    public void onError(WsErrorContext ctx){
        System.err.println("Websocket error: " + ctx.error().getMessage());
    }

    // --------- HELPER METHODS ----------
    private void sendMessage(Session session, ServerMessage message) throws IOException {
        if(session.isOpen()){
            session.getRemote().sendString(gson.toJson(message));
        }
    }

    private void broadcastToGame(Integer gameID, ServerMessage message, Session excludeSession) throws IOException{
        for(Map.Entry<String, Session> entry: connections.entrySet()){
            Session session = entry.getValue();
            String authToken = entry.getKey();

            if(connectionGames.get(authToken) != null &&
            connectionGames.get(authToken).equals(gameID) &&
            session != excludeSession){
                sendMessage(session, message);
            }
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws  IOException, DataAccessException {
        String authToken = command.getAuthToken();
        Integer gameId = command.getGameID();

        //Validate the auth token
        AuthData authData = dataAccess.getAuthToken(authToken);
        if(authData == null){
            sendMessage(session, new ErrorMessage("Error: Invalid auth token"));
            return;
        }

        //Validate the game
        GameData gameData = dataAccess.getGame(gameId);
        if(gameData == null){
            sendMessage(session, new ErrorMessage("Error: Game not found"));
            return;
        }

        //Store the connections
        connections.put(authToken, session);
        connectionGames.put(authToken, gameId);

        //send Load Game to the connecting client
        sendMessage(session, new LoadGameMessage(gameData.game()));

        //Verify if it's a player or an observer
        String username = authData.username();
        String notification;

        if(username.equals(gameData.whiteUsername())){
            notification = username + " joined the game as WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            notification = username + " joined the game as BLACK";
        } else {
            notification = username + " joined as an observer";
        }

        broadcastToGame(gameId, new NotificationMessage(notification), session);
    }

    private void handleMakeMove(Session session, UserGameCommand command) throws  IOException, DataAccessException{
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        AuthData token = dataAccess.getAuthToken(authToken);
        if(token == null){
            sendMessage(session, new ErrorMessage("Error: Invalid auth token"));
            return;
        }

        MakeMoveCommand moveCommand = (MakeMoveCommand) command;
        ChessMove move = moveCommand.getMove();

        GameData game = dataAccess.getGame(gameID);
        if(game == null){
            sendMessage(session, new ErrorMessage("Game not found"));
            return;
        }

        String username = token.username();
        ChessGame chessGame = game.game();

        ChessGame.TeamColor currentTurn = chessGame.getTeamTurn();
        boolean isWhitePlayer = username.equals(game.whiteUsername());
        boolean isBlackPlayer = username.equals(game.blackUsername());

        if((currentTurn == ChessGame.TeamColor.WHITE && !isWhitePlayer) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !isBlackPlayer)){
            sendMessage(session, new ErrorMessage("Error: Not your turn"));
            return;
        }

        try{
            chessGame.makeMove(move);
            dataAccess.updateGame(game);
            broadcastToGame(gameID, new LoadGameMessage(chessGame), null);

            String moveDescription = formatMove(move);
            String notification = username + " moved " + moveDescription;
            broadcastToGame(gameID, new NotificationMessage(notification), session);

            ChessGame.TeamColor opponentColor = (currentTurn == ChessGame.TeamColor.WHITE)
                    ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

            if(chessGame.isInCheckmate(opponentColor)){
                broadcastToGame(gameID, new NotificationMessage(opponentColor + " is in checkmate! " + username + " wins!"), null);
            } else if (chessGame.isInStalemate(opponentColor)) {
                broadcastToGame(gameID, new NotificationMessage("Stalemate! Game is a draw."), null);
            } else if (chessGame.isInCheck(opponentColor)){
                broadcastToGame(gameID, new NotificationMessage(opponentColor + " is in check!"), null);
            }


        } catch (Exception e) {
            sendMessage(session, new ErrorMessage("Error: Invalid move - " + e.getMessage()));
        }

    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException, DataAccessException{
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        AuthData token = dataAccess.getAuthToken(authToken);
        if(token == null){
            sendMessage(session, new ErrorMessage("Error: Invalid auth token"));
            return;
        }

        GameData game = dataAccess.getGame(gameID);
        if(game == null){
            String username = token.username();

            if(username.equals(game.whiteUsername())){
                dataAccess.updateGame(new GameData(gameID, null, game.blackUsername(), game.gameName(), game.game()));

            } else if (username.equals(game.blackUsername())) {
                dataAccess.updateGame(new GameData(gameID, game.whiteUsername(), null, game.gameName(), game.game()));
            }

            broadcastToGame(gameID, new NotificationMessage(username + " left the game"), session);
        }

        connections.remove(authToken);
        connectionGames.remove(authToken);

    }

    private void handleResign(Session session, UserGameCommand command) throws IOException, DataAccessException{
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        AuthData token = dataAccess.getAuthToken(authToken);
        if(token == null){
            sendMessage(session, new ErrorMessage("Error: Invalid auth token"));
            return;
        }

        GameData game = dataAccess.getGame(gameID);
        if(game == null){
            sendMessage(session, new ErrorMessage("Error: Game not found"));
            return;
        }

        String username = token.username();

        if(!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())){
            sendMessage(session, new ErrorMessage("Error: Observers cannot resign"));
            return;
        }

        String notification = username + " has resigned. Game Over";
        broadcastToGame(gameID, new NotificationMessage(notification), null);
    }

    private String formatMove(ChessMove move){
        return positionToNotation(move.getStartPosition()) + " to " + positionToNotation(move.getEndPosition());
    }

    private String positionToNotation(ChessPosition position){
        char col = (char) ('a' + position.getColumn() - 1);
        return "" + col + position.getRow();
    }

}
