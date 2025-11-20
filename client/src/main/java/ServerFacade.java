import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.Objects;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String url){
        this.serverUrl = url;
    }

    public ServerFacade(int port){
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register (String username, String password, String email) throws Exception{
        return null;
    }

    public AuthData login(String username, String password) throws Exception{
        return null;
    }

    public void logout(String authToken) throws Exception{

    }

    public int createGame(String authToken, String gameName) throws Exception{
        return 0;
    }


    public Collection<GameData> listAllGames(String authToken) throws Exception{
        return null;
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws Exception{

    }

    public void clear() throws Exception{

    }

    //Helper Methods
    private <T> T makeRequest (String method, String path, Object request, String authToken, Class<T> responseClass) throws Exception{
        return null;
    }

}
