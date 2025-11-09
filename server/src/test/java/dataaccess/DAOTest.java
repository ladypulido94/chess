package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTest {

    private MemoryDataAccess dao;

    @BeforeEach
    public  void setUp() {
        dao = new MemoryDataAccess();
        dao.clear();
    }

    //USER DAO
    @Test
    public void positiveAddUser() throws DataAccessException {
        UserData user = new UserData("testName","1234","test@test.com");
        dao.addUser(user);
        UserData result = dao.getUser("testName");
        assertEquals(user,result);
    }

    @Test
    public void negativeAddUser() {
        UserData user = new UserData("testName","1234","test@test.com");
        assertDoesNotThrow(() -> dao.addUser(user));

        assertThrows(DataAccessException.class, () -> dao.addUser(user));
    }

    //GAME DAO

    @Test
    public void positiveAddGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "testWhite", "testBlack", "testGame", chessGame);
        int generateId = dao.addGame(game);

        GameData retrieve = dao.getGame(generateId);
        assertEquals("testWhite", retrieve.whiteUsername());
        assertEquals("testBlack", retrieve.blackUsername());
    }

    @Test
    public void positiveGetAllGames() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "testWhite", "testBlack", "testGame", chessGame);
        dao.addGame(game);
        GameData game1 = new GameData(0, "testWhite1", "testBlack1", "testGame1", chessGame);
        dao.addGame(game1);
        GameData game2 = new GameData(0, "testWhite2", "testBlack2", "testGame2", chessGame);
        dao.addGame(game2);
        Collection<GameData> allGames = dao.getAllGames();
        assertEquals(3, allGames.size());

    }

    @Test
    public void negativeGetAllGames() throws DataAccessException{
        Collection<GameData> allGames = dao.getAllGames();
        assertTrue(allGames.isEmpty(),"No games in DAO");
    }

    @Test
    public void positiveUpdateGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "testWhite", "testBlack", "testGame", chessGame);
        dao.addGame(game);
        int gameId = dao.addGame(game);
        GameData updatedGame = new GameData(gameId,"testWhite", "updatedBlack", "testGame", chessGame);
        dao.updateGame(updatedGame);
        GameData result = dao.getGame(gameId);

        assertEquals("updatedBlack", result.blackUsername());
        assertEquals("testWhite", result.whiteUsername());

    }

    @Test
    public void negativeUpdateGame() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "testWhite", "testBlack", "testGame", chessGame);

        assertThrows(DataAccessException.class, () -> dao.updateGame(game));

    }

    //AUTH DAO

    @Test
    public void positiveAddAuth() throws DataAccessException{
        AuthData authToken = new AuthData("1h3", "test");
        dao.addAuthToken(authToken);
        AuthData result = dao.getAuthToken("1h3");

        assertEquals(authToken, result);
    }

    @Test
    public void negativeAddAuth() throws DataAccessException{
        AuthData authToken = new AuthData("1h3", "test");
        dao.addAuthToken(authToken);

        assertThrows(DataAccessException.class, () -> dao.addAuthToken(authToken));
    }

    @Test
    public void positiveDeleteAuth() throws DataAccessException{
        AuthData authToken = new AuthData("1h3","test");
        dao.addAuthToken(authToken);
        dao.deleteAuthToken("1h3");

        assertNull(dao.getAuthToken("1h3"));
    }

    @Test
    public void negativeDeleteAuth() {
        assertThrows(DataAccessException.class, () -> dao.deleteAuthToken("No Token"));

    }

}
