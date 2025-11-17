package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private MemoryDataAccess dao;
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    public void setUp(){
        dao = new MemoryDataAccess();
        gameService = new GameService(dao);
        userService = new UserService(dao);
    }

    @Test
    public void positiveCreateGame() throws DataAccessException {
        UserData user = new UserData("test", "testPassword","test@test.com");
        AuthData authToken = userService.register(user);

        int gameId = gameService.createGame(authToken, "testGame");
        assertTrue(gameId > 0);
        GameData game = dao.getGame(gameId);
        assertNotNull(game);
        assertEquals("testGame", game.gameName());

    }

    @Test
    public void negativeCreateGame() throws DataAccessException{
        AuthData wrongToken = new AuthData("invalidToken", "user");
        assertThrows(DataAccessException.class,
                () -> gameService.createGame(wrongToken, "testName"));
        UserData user = new UserData("test", "testPassword", "test@test.com");
        AuthData authToken = userService.register(user);
        assertThrows(DataAccessException.class, () -> gameService.createGame(authToken, ""));

    }
}
