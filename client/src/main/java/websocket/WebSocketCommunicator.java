package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;

import java.net.URI;

@ClientEndpoint
public class WebSocketCommunicator extends Endpoint {
    private Session session;
    private final ServerMessageObserver observer;
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

           }
        });

    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
