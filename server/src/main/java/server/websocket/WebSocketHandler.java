package server.websocket;

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
        //TODO: Remove from connections map

    }

    @OnWebSocketMessage
    public void onMessage(WsMessageContext ctx){
        System.out.println("Received: " + ctx.message());
        //TODO: Parse message, handle command
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

    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException, DataAccessException{

    }

    private void handleResign(Session session, UserGameCommand command) throws IOException, DataAccessException{

    }

}
