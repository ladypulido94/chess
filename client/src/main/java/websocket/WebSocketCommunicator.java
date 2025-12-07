package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.*;
import websocket.messages.*;

import java.net.URI;

@ClientEndpoint
public class WebSocketCommunicator{
    private Session session;
    private ServerMessageObserver observer;
    private final Gson gson = new Gson();

    public WebSocketCommunicator(String url, ServerMessageObserver observer) throws  Exception{
        this.observer = observer;

        //Convert http://localhost:8080 to ws://localhost:8080/ws
        url = url.replace("http", "ws");
        URI uri = new URI(url + "/ws");

        //Connect to the server
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        System.out.println("WebSocket connection established");
    }

    //Close the connection
    @OnClose
    public void onClose(Session session, CloseReason closeReason) throws Exception{
        System.out.println("Connection closed: " + closeReason);
    }

    public void close() throws Exception{
        if(session != null && session.isOpen()){
            session.close();
        }
    }

    @OnMessage
    public void onMessage(String message){
        try{
            ServerMessage baseMessage = gson.fromJson(message, ServerMessage.class);
            ServerMessage serverMessage = switch (baseMessage.getServerMessageType()){
                case LOAD_GAME -> gson.fromJson(message, LoadGameMessage.class);
                case ERROR -> gson.fromJson(message, ErrorMessage.class);
                case NOTIFICATION -> gson.fromJson(message, NotificationMessage.class);
            };

            if(observer != null){
                observer.notify(serverMessage);
            }

        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    //Converts the GamePlayUI command into a string for the webSocket
    public void send(UserGameCommand command) throws Exception{
        if(session == null || !session.isOpen()){
            throw new Exception("Websocket session is not open");
        }

        String jsonCommand = gson.toJson(command);

        try{
            session.getBasicRemote().sendText(jsonCommand);
        } catch (Exception e){
            System.err.println("DEBUG: Failed to send message: " + e.getMessage());
            throw e;
        }
    }

    public void setObserver(ServerMessageObserver observer){
        this.observer = observer;
    }
}
