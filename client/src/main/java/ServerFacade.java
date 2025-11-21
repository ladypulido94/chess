import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

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
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            if(authToken != null){
                conn.setRequestProperty("authorization", authToken);
            }

            if(request != null){
                conn.setRequestProperty("Content-Type", "application/json");
                String reqData = gson.toJson(request);
                try(OutputStream os = conn.getOutputStream()){
                    os.write(reqData.getBytes());
                }
            }

            conn.connect();
            int responseCode = conn.getResponseCode();

            if(responseCode == 200){
                if(responseClass == null){
                    return null;
                }

                try (InputStream inputStream = conn.getInputStream()){
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    return gson.fromJson(reader, responseClass);
                }
            } else {
                try(InputStream errorStream = conn.getErrorStream()){
                    if(errorStream != null){
                        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
                        record ErrorResponse(String message){}
                        ErrorResponse error = gson.fromJson(inputStreamReader, ErrorResponse.class);
                        throw new Exception(error.message());
                    }
                }

                throw new Exception("Error: Unknown server error");
            }

        } catch (IOException e){
            throw new Exception("Error: Unable to connect to server");
        }
    }

}
