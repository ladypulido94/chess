package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTest {

    private MySQLDataAccess dataAccess;

    @BeforeEach
    public  void setUp() throws DataAccessException{
        dataAccess = new MySQLDataAccess();
        dataAccess.configureDatabase();
        dataAccess.clear();
    }

    //USER DAO
    @Test
    public void positiveAddUser() throws DataAccessException {
        UserData user = new UserData("testName","1234","test@test.com");
        dataAccess.addUser(user);
        UserData result = dataAccess.getUser("testName");
        assertEquals(user.username(), result.username());
        assertEquals(user.email(), result.email());
        assertTrue(BCrypt.checkpw(user.password(), result.password()));
    }

    @Test
    public void negativeAddUser() {
        UserData user = new UserData("testName","1234","test@test.com");
        assertDoesNotThrow(() -> dataAccess.addUser(user));

        assertThrows(DataAccessException.class, () -> dataAccess.addUser(user));
    }

    @Test
    public void positiveGetUser() throws DataAccessException{
        UserData user = new UserData("testName","1234","test@test.com");
        dataAccess.addUser(user);
        UserData result = dataAccess.getUser("testName");

        assertNotNull(result);
        assertEquals(user.username(), result.username());
        assertEquals(user.email(), result.email());
        assertTrue(BCrypt.checkpw(user.password(), result.password()));

    }

    @Test
    public void negativeGetUser() throws DataAccessException{
        UserData result = dataAccess.getUser("nonExistentUser");
        assertNull(result);

    }

    //GAME DAO

    @Test
    public void positiveAddGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "testWhite", "testBlack", "testGame", chessGame);
        int generateId = dataAccess.addGame(game);

        GameData retrieve = dataAccess.getGame(generateId);
        assertEquals("testWhite", retrieve.whiteUsername());
        assertEquals("testBlack", retrieve.blackUsername());
    }

    @Test
    public void negativeAddGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, null, chessGame);

        assertThrows(DataAccessException.class, ()-> dataAccess.addGame(game));
    }

    @Test
    public void positiveGetGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "testWhite", "testBlack", "testGame", chessGame);
        int generateId = dataAccess.addGame(game);
        GameData retrieve = dataAccess.getGame(generateId);

        assertNotNull(retrieve);
        assertEquals("testWhite", retrieve.whiteUsername());
        assertEquals("testBlack", retrieve.blackUsername());

    }

    @Test
    public void negativeGetGame() throws DataAccessException{
        GameData retrieve = dataAccess.getGame(12);
        assertNull(retrieve);

    }

    @Test
    public void positiveGetAllGames() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "testWhite", "testBlack", "testGame", chessGame);
        dataAccess.addGame(game);
        GameData game1 = new GameData(0, "testWhite1", "testBlack1", "testGame1", chessGame);
        dataAccess.addGame(game1);
        GameData game2 = new GameData(0, "testWhite2", "testBlack2", "testGame2", chessGame);
        dataAccess.addGame(game2);
        Collection<GameData> allGames = dataAccess.getAllGames();
        assertEquals(3, allGames.size());

    }

    @Test
    public void negativeGetAllGames() throws DataAccessException{
        Collection<GameData> allGames = dataAccess.getAllGames();
        assertTrue(allGames.isEmpty(),"No games in DAO");
    }

    @Test
    public void positiveUpdateGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "testWhite", "testBlack", "testGame", chessGame);
        dataAccess.addGame(game);
        int gameId = dataAccess.addGame(game);
        GameData updatedGame = new GameData(gameId,"testWhite", "updatedBlack", "testGame", chessGame);
        dataAccess.updateGame(updatedGame);
        GameData result = dataAccess.getGame(gameId);

        assertEquals("updatedBlack", result.blackUsername());
        assertEquals("testWhite", result.whiteUsername());

    }

    @Test
    public void negativeUpdateGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "testWhite", "testBlack", "testGame", chessGame);

        assertThrows(DataAccessException.class, () -> dataAccess.updateGame(game));

    }

    //AUTH DAO

    @Test
    public void positiveAddAuth() throws DataAccessException{
        UserData user = new UserData("test", "testPassword", "test@test.com");
        dataAccess.addUser(user);

        AuthData authToken = new AuthData("1h3", "test");
        dataAccess.addAuthToken(authToken);
        AuthData result = dataAccess.getAuthToken("1h3");

        assertEquals(authToken, result);
    }

    @Test
    public void negativeAddAuth() throws DataAccessException{
        UserData user = new UserData("test", "testPassword", "test@test.com");
        dataAccess.addUser(user);

        AuthData authToken = new AuthData("1h3", "test");
        dataAccess.addAuthToken(authToken);

        assertThrows(DataAccessException.class, () -> dataAccess.addAuthToken(authToken));
    }

    @Test
    public void positiveDeleteAuth() throws DataAccessException{
        UserData user = new UserData("test", "testPassword", "test@test.com");
        dataAccess.addUser(user);

        AuthData authToken = new AuthData("1h3","test");
        dataAccess.addAuthToken(authToken);
        dataAccess.deleteAuthToken("1h3");

        assertNull(dataAccess.getAuthToken("1h3"));
    }

    @Test
    public void negativeDeleteAuth() {
        assertThrows(DataAccessException.class, () -> dataAccess.deleteAuthToken("No Token"));

    }

    @Test
    public void positiveGetAuthToken() throws DataAccessException{
        UserData user = new UserData("test", "testPassword", "test@test.com");
        dataAccess.addUser(user);

        AuthData authToken = new AuthData("1h3", "test");
        dataAccess.addAuthToken(authToken);
        AuthData result = dataAccess.getAuthToken("1h3");

        assertNotNull(result);
        assertEquals(authToken, result);

    }

    @Test
    public void negativeGetAuthToken() throws DataAccessException{
        AuthData result = dataAccess.getAuthToken("nonExistentAuthToken");
        assertNull(result);

    }

    @Test
    public void clearPositive() throws DataAccessException{
        UserData userData = new UserData("test", "testPassword", "test@test.com");
        dataAccess.addUser(userData);

        ChessGame game = new ChessGame();
        GameData gameData = new GameData(0,null, null, "testGame", game);
        int gameId = dataAccess.addGame(gameData);

        AuthData authData = new AuthData("testToken", "test");
        dataAccess.addAuthToken(authData);

        dataAccess.clear();
        assertNull(dataAccess.getUser("test"));
        assertNull(dataAccess.getGame(gameId));
        assertNull(dataAccess.getAuthToken("testToken"));
    }

}
