package dataaccess;

import chess.ChessGame;
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
        GameData game = new GameData(12, "testWhite", "testBlack", "testGame", chessGame);
        dao.addGame(game);
        GameData retrieve = dao.getGame(12);
        assertEquals(game, retrieve);
    }

    @Test
    public void negativeAddGame(){
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "testWhite", "testBlack", "testGame", chessGame);

        assertDoesNotThrow(() -> dao.addGame(game));
        assertThrows(DataAccessException.class, () -> dao.addGame(game));

    }

    @Test
    public void positiveGetAllGames() throws DataAccessException{
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(12, "testWhite", "testBlack", "testGame", chessGame);
        dao.addGame(game);
        GameData game1 = new GameData(13, "testWhite1", "testBlack1", "testGame1", chessGame);
        dao.addGame(game1);
        GameData game2 = new GameData(14, "testWhite2", "testBlack2", "testGame2", chessGame);
        dao.addGame(game2);
        Collection<GameData> allGames = dao.getAllGames();
        assertEquals(3, allGames.size());
        assertTrue(allGames.contains(game));
        assertTrue(allGames.contains(game1));
        assertTrue(allGames.contains(game2));

    }

    @Test
    public void negativeGetAllGames() throws DataAccessException{
        Collection<GameData> allGames = dao.getAllGames();
        assertTrue(allGames.isEmpty(),"No games in DAO");
    }

    @Test
    public void positiveUpdateGame() {

    }

    @Test
    public void negativeUpdateGame() {

    }

    //AUTH DAO

    @Test
    public void positiveAddAuth() {

    }

    @Test
    public void negativeAddAuth() {

    }

    @Test
    public void positiveDeleteAuth() {

    }

    @Test
    public void negativeDeleteAuth() {

    }

}
