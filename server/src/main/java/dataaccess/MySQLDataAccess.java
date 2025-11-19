package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySQLDataAccess implements DataAccess{

    public void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            String createUserTable = """
                    CREATE TABLE IF NOT EXISTS user (
                    username VARCHAR(255) NOT NULL PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL
                    )
                    """;

            try (var preparedStatement = conn.prepareStatement(createUserTable)) {
                preparedStatement.executeUpdate();
            }

            String createAuthTable = """
                    CREATE TABLE IF NOT EXISTS auth(
                    authToken VARCHAR(255) NOT NULL PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    FOREIGN KEY (username) REFERENCES user(username)
                    )
                    """;
            try (var preparedStatement = conn.prepareStatement(createAuthTable)){
                preparedStatement.executeUpdate();
            }

            String createGameTable = """
                    CREATE TABLE IF NOT EXISTS game(
                    gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL,
                    game TEXT NOT NULL
                    )
                    """;

            try(var preparedStatement =conn.prepareStatement(createGameTable)){
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }

    @Override
    public void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            String deleteAuthTable = """
                    DELETE FROM auth
                    """;

            try (var preparedStatement = conn.prepareStatement(deleteAuthTable)){
                preparedStatement.executeUpdate();
            }

            String deleteGameTable = """
                    DELETE FROM game
                    """;

            try (var preparedStatement = conn.prepareStatement(deleteGameTable)) {
                preparedStatement.executeUpdate();
            }

            String deleteUserTable = """
                    DELETE FROM user
                    """;

            try (var preparedStatement = conn.prepareStatement(deleteUserTable)){
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable clear database", e);
        }

    }

    @Override
    public void addUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public int addGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void addAuthToken(AuthData authToken) throws DataAccessException {

    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {

    }
}
