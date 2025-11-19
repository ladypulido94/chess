package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.sql.Statement;

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
        if(getUser(user.username()) != null){
            throw new DataAccessException("User already exists");
        }

        String hashPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection()){
            String insertUser = """
                    INSERT INTO user (username, password, email) VALUES (?,?,?)
                    """;

            try (var preparedStatement = conn.prepareStatement(insertUser)){
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, hashPassword);
                preparedStatement.setString(3, user.email());

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e){
            throw new DataAccessException("Unable to add user", e);
        }

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {

            try(var preparedStatement = conn.prepareStatement("SELECT username, password, email " +
                    "FROM user" +
                    "WHERE username = ?")){
                preparedStatement.setString(1, username);

                try(var rs = preparedStatement.executeQuery()){
                    if(rs.next()){
                        String user = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");

                        UserData userData = new UserData(user, password, email);
                        return userData;
                    }
                }
            }

        } catch (SQLException e){
            throw new DataAccessException("Unable to find user", e);
        }
        return null;
    }

    @Override
    public int addGame(GameData game) throws DataAccessException {
        Gson gson = new Gson();
        String gameJson = gson.toJson(game.game());

        try(var conn = DatabaseManager.getConnection()){
            String insertGame = """
                    INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?)
                    """;

            try(var preparedStatement = conn.prepareStatement(insertGame, Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, gameJson);

                preparedStatement.executeUpdate();

                try(var rs = preparedStatement.getGeneratedKeys()){
                    if(rs.next()){
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to add game", e);
        }
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
