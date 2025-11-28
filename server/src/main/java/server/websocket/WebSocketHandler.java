package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final Map<String, Session> connections = new ConcurrentHashMap<>();
    private final Map<String, Integer> connectionGames = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

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
