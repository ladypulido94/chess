package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;

@ClientEndpoint
public class WebSocketCommunicator extends Endpoint {
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

        //Set up message handler
        this.session.addMessageHandler(new MessageHandler.Whole<String>(){
           @Override
           public void onMessage(String message){
               try{
                   //Parse JSON into ServerMessage and Notify the GameplayUI
                   ServerMessage baseMessage = gson.fromJson(message, ServerMessage.class);
                   ServerMessage serverMessage = switch (baseMessage.getServerMessageType()) {
                       case LOAD_GAME -> gson.fromJson(message, LoadGameMessage.class);
                       case ERROR -> gson.fromJson(message, ErrorMessage.class);
                       case NOTIFICATION -> gson.fromJson(message, NotificationMessage.class);
                   };

                   if(observer != null){
                       observer.notify(serverMessage);
                   }

               } catch (Exception e){
                   System.err.println("Error handling message: " + e.getMessage());
               }
           }
        });

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection established");
    }

    //Converts the GamePlayUI command into a string for the webSocket
    public void send(UserGameCommand command) throws Exception{
        String jsonCommand = gson.toJson(command);
        session.getBasicRemote().sendText(jsonCommand);
    }

    //Close the connection
    public void close() throws Exception{
        if(session != null && session.isOpen()){
            session.close();;
        }
    }

    public void setObserver(ServerMessageObserver observer){
        this.observer = observer;
    }
}
