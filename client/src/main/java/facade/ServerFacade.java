package facade;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String url){
        this.serverUrl = url;
    }

    public ServerFacade(int port){
        this.serverUrl = "http://localhost:" + port;
    }

    public String getServerUrl(){
        return serverUrl;
    }

    /**
     * Registers a new user
     * @param username the user's username
     * @param password the user's password
     * @param email the user's email
     * @return AuthData containing authToken and username
     * @throws Exception if register fails
     */
    public AuthData register (String username, String password, String email) throws Exception{
        var path = "/user";
        var request = new UserData(username, password, email);
        return makeRequest("POST", path, request, null, AuthData.class);
    }

    /**
     * Logs in an existing user
     * @param username the user's username
     * @param password the user's password
     * @return AuthData containing authToken and username
     * @throws Exception if login fails
     */
    public AuthData login(String username, String password) throws Exception{
        var path = "/session";
        var request = new UserData(username, password, null);
        return makeRequest("POST", path, request, null, AuthData.class);
    }

    /**
     * Logs out the current user
     * @param authToken the authentication token of the user to log out
     * @throws Exception if log out fails
     */
    public void logout(String authToken) throws Exception{
        var path = "/session";
        makeRequest("DELETE", path, null, authToken, null);
    }

    /**
     * Creates a new game
     * @param authToken the authentication token of the user to create a game
     * @param gameName the name of the new game
     * @return the gameID from the game
     * @throws Exception if create a game fails
     */
    public int createGame(String authToken, String gameName) throws Exception{
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        var response = makeRequest("POST", path, request, authToken, CreateGameResponse.class);
        return response.gameID();
    }


    /**
     * Lists all games
     * @param authToken the authentication token of the user to list all games
     * @return A list of all games
     * @throws Exception if listing all games fails
     */
    public Collection<GameData> listAllGames(String authToken) throws Exception{
        var path = "/game";
        var response = makeRequest("GET", path, null, authToken, ListGamesResponse.class);
        if(response == null || response.games() == null){
            return List.of();
        }

        return List.of(response.games());
    }

    /**
     * Joins a game as a player
     * @param authToken the authentication token of the user to join a game
     * @param gameId the ID of the game
     * @param playerColor the color of the player to join the game
     * @throws Exception if joining a game fails
     */
    public void joinGame(String authToken, int gameId, String playerColor) throws Exception{
        var path = "/game";
        var request = new JoinGameRequest(playerColor, gameId);
        makeRequest("PUT", path, request, authToken, null);

    }

    /**
     * Clears all data from the server
     * @throws Exception if clearing the database fails
     */
    public void clear() throws Exception{
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    // ------------ Helper Methods ----------- //

    /**
     * Makes an HTTP request to the server
     * @param method the HTTP method (GET, POST, PUT, DELETE)
     * @param path the API endpoint path
     * @param request the request body object (null if no body required)
     * @param authToken the authentication token (null if not required)
     * @param responseClass the class type to deserialize the response into
     * @return the deserialized response object, or null if no response
     * @param <T> the type of the response object
     * @throws Exception if the request fails or server returns an error
     */
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

    // ------------ Response and Request record classes ------------ //
    public record CreateGameRequest(String gameName){}
    public record CreateGameResponse(int gameID){}
    public record ListGamesResponse(GameData[] games){}
    public record JoinGameRequest(String playerColor, int gameID){}
    private record ErrorResponse(String message){}
}
