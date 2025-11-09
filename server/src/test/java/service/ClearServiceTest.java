package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClearServiceTest {

    private MemoryDataAccess dao;
    private ClearService clearService;

    @BeforeEach
    public void setup(){
        dao = new MemoryDataAccess();
        clearService = new ClearService(dao);
    }

    @Test
    public void positiveClearService() throws DataAccessException {
        UserData user = new UserData("test", "testPassword","test@gmail.com");
        dao.addUser(user);
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0,"testBlack", "testWhite", "testGame" ,chessGame);
        int gameId = dao.addGame(game);

        AuthData authToken = new AuthData("1h3","test");
        dao.addAuthToken(authToken);

        clearService.clear();

        assertNull(dao.getUser("test"));
        assertNull(dao.getGame(gameId));
        assertNull(dao.getAuthToken("1h3"));
    }
}
