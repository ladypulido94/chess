package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

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

    @Test
    public void positiveUpdateGame() throws DataAccessException{
        UserData user = new UserData("test", "testPassword","test@test.com");
        AuthData token = userService.register(user);

        int gameId = gameService.createGame(token, "testGame");

        assertDoesNotThrow(() -> gameService.joinGame(token.authToken(),"white", gameId));

        GameData updatedGame = dao.getGame(gameId);

        assertEquals("test", updatedGame.whiteUsername());
        assertNull(updatedGame.blackUsername());
    }

    @Test
    public void negativeUpdateGame() throws DataAccessException{
        UserData user = new UserData("test", "testPassword", "test@test.com");
        AuthData token = userService.register(user);

        int gameId = gameService.createGame(token, "testGame");

        assertThrows(DataAccessException.class, () -> gameService.joinGame(token.authToken(),"purple",gameId));
    }

    @Test
    public void positiveListAllGames() throws DataAccessException {
        UserData user = new UserData("test", "testPassword", "test@test.com");
        AuthData token = userService.register(user);

        gameService.createGame(token, "testGame1");
        gameService.createGame(token, "testGame2");
        gameService.createGame(token, "testGame3");

        Collection<GameData> games = gameService.listAllGames(token.authToken());

        assertNotNull(games);
    }

    @Test
    public void negativeListAllGames() throws DataAccessException{

        String invalidToken = "InvalidToken";

        assertThrows(DataAccessException.class, () -> gameService.listAllGames(invalidToken));

    }
}
