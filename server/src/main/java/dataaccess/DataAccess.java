package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear();
    void addUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    int addGame(GameData game) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    Collection<GameData> getAllGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void addAuthToken(AuthData authToken) throws DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
}
