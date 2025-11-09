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
        games.clear();
        authTokens.clear();
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
        return users.get(username);
    }

    @Override
    public int addGame(GameData game) throws DataAccessException{
        int newGameId = nextGameId++;
        GameData newGame = new GameData(newGameId, game.whiteUsername(), game.blackUsername(),
                game.gameName(), game.game());
        games.put(newGameId, newGame);
        return newGameId;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException{
        return games.get(gameId);
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
    public void addAuthToken(AuthData authToken) throws DataAccessException{
        if(authTokens.containsKey(authToken.authToken())){
            throw new DataAccessException("AuthToken already exists");
        }
        authTokens.put(authToken.authToken(),authToken);
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException{
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException{
        if(!authTokens.containsKey(authToken)){
            throw new DataAccessException("AuthToken doesn't exists");
        }

        authTokens.remove(authToken);
    }
}
