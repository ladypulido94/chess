package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
    public void onConnect(Session session){
        System.out.println("Websocket connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason){
        System.out.println("WebSocket closed: " + session);
        //TODO: Remove from connections map

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message){
        System.out.println("Received: " + message);
        //TODO: Parse message, handle command
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            switch(command.getCommandType()){
                case CONNECT -> handleConnect(session, command);
                case MAKE_MOVE -> handleMakeMove(session, command);
                case LEAVE -> hanldeLeave(session, command);
                case RESIGN -> handleResign(session, command);
            }

        } catch (Exception e) {
            try {
                sendMessage(session, new ErrorMessage("Error: " + e.getMessage()));
            } catch (IOException ex) {
                System.err.println("Failed to send error message: " + ex.getMessage());
            }
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error){
        System.err.println("Websocket error: " + error.getMessage());
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

}
