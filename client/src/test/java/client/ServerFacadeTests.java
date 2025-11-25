package client;

import facade.ServerFacade;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void setUp() throws Exception{
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositive() throws Exception{
        AuthData token = facade.register("test", "testPassword", "test@test.com");
        assertNotNull(token);
        assertNotNull(token.authToken());
    }

    @Test
    public void registerNegative() throws Exception{
        facade.register("test", "testPassword", "test@test.com");
        assertThrows(Exception.class, () -> facade.register("test","testPassword","test@test.com"));
    }

    @Test
    public void loginPositive() throws Exception{
        facade.register("test","testPassword","test@test.com");
        AuthData token = facade.login("test", "testPassword");

        assertNotNull(token);
        assertNotNull(token.authToken());
    }

    @Test
    public void loginNegative() throws Exception{
        facade.register("test", "testPassword","test@test.com");
        assertThrows(Exception.class, () -> facade.login("test","tesPass"));
    }

    @Test
    public void logoutPositive() throws Exception{
        facade.register("test","testPassword","test@test.com");
        AuthData token = facade.login("test","testPassword");
        facade.logout(token.authToken());

        assertThrows(Exception.class, () -> facade.listAllGames(token.authToken()));
    }

    @Test
    public void logoutNegative() throws Exception{
        assertThrows(Exception.class, () -> facade.logout("invalidToken"));

    }

    @Test
    public void createGamePositive() throws Exception{
        AuthData token = facade.register("test", "testPassword", "test@test.com");
        assertDoesNotThrow(() -> facade.createGame(token.authToken(), "testGame"));
    }

    @Test
    public void createGameNegative() throws Exception{
        AuthData token = facade.register("test","testPassword","test@test.com");
        assertThrows(Exception.class, () -> facade.createGame(token.authToken(), null));

    }

    @Test
    public void listAllGamesPositive() throws Exception{
        AuthData token = facade.register("test","testPassword","test@test.com");
        facade.createGame(token.authToken(),"testGame");
        facade.createGame(token.authToken(), "testGame1");
        facade.createGame(token.authToken(),"testGame2");

        assertDoesNotThrow(() -> facade.listAllGames(token.authToken()));
    }

    @Test
    public void listAllGamesNegative() throws Exception{
        assertThrows(Exception.class, () -> facade.listAllGames("invalidToken"));
    }

    @Test
    public void joinGamePositive() throws Exception{

    }

    @Test
    public void joinGameNegative() throws Exception{

    }

}
