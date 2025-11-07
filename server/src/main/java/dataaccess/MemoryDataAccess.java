package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess{
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new Hashtable<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private int nextGameId = 1;

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        if(users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        UserData user = users.get(username);

        if(user == null){
            throw new DataAccessException("User doesn't exist");
        }
        return user;
    }

    @Override
    public int addGame(GameData game) throws DataAccessException{
        if (games.containsKey(game.gameID())){
            throw new DataAccessException("Game already exist");
        }
        games.put(game.gameID(),game);

        return game.gameID();
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException{
        GameData game = games.get(gameId);
        if (game == null){
            throw new DataAccessException("Game doesn't exist");
        }

        return game;
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException{
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{

        if (!games.containsKey(game.gameID())){
            throw new DataAccessException("The game doesn't exist");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void addAuth(AuthData authToken) throws DataAccessException{
        if(authTokens.containsKey(authToken.authToken())){
            throw new DataAccessException("AuthToken already exists");
        }
        authTokens.put(authToken.authToken(),authToken);
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException{
        AuthData token = authTokens.get(authToken);
        if(token == null){
            throw new DataAccessException("AuthToken doesn't exist");
        }
        return token;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if(!authTokens.containsKey(authToken)){
            throw new DataAccessException("AuthToken doesn't exists");
        }

        authTokens.remove(authToken);
    }
}
