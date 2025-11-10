package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private MemoryDataAccess dao;
    private UserService userService;

    @BeforeEach
    public void setUp(){
        dao = new MemoryDataAccess();
        userService = new UserService(dao);
    }

    @Test
    public void positiveRegisterService() throws DataAccessException{
        UserData user = new UserData("test", "testPassword", "test@gmail.com");
        AuthData authData = userService.register(user);

        assertNotNull(authData);
        assertEquals("test",authData.username());
    }

    @Test
    public void negativeRegisterService() throws DataAccessException{
        UserData user = new UserData("", "testPassword", "test@gmail.com");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> userService.register(user));
        assertEquals("Error: Bad Request", exception.getMessage());

        UserData user1 = new UserData("test", "", "test@gmail.com");

        DataAccessException exception1 = assertThrows(DataAccessException.class, () -> userService.register(user1));
        assertEquals("Error: Bad Request", exception1.getMessage());

        UserData user2 = new UserData("test", "testPassword", null);

        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> userService.register(user2));
        assertEquals("Error: Bad Request", exception2.getMessage());

        UserData user3 = new UserData("test", "testPassword", "test@gmail.com");
        userService.register(user3);

        UserData user4 = new UserData("test", "testPassword1", "test1@gmail.com");
        DataAccessException exception3 = assertThrows(DataAccessException.class, () -> userService.register(user4));
        assertEquals("Error: User already taken", exception3.getMessage());

    }

    @Test
    public void positiveLoginService() throws DataAccessException{
        UserData user = new UserData("test", "testPassword", "test@gmail.com");
        userService.register(user);
        assertNotNull(userService.login(user));

    }

    @Test
    public void negativeLoginService() throws DataAccessException{
        UserData user = new UserData("", "testPassword", "test@gmail.com");
        UserData user1 = new UserData("test1", "", "test1@gmail.com");
        UserData user2 = new UserData("test2", "testPassword2", "test2@gmail.com");
        UserData rightPassword = new UserData("test3", "testPassword3", "test2@gmail.com");
        userService.register(rightPassword);
        UserData wrongPassword = new UserData("test3", "testPassword2", "test3@gmail.com");

        assertThrows(DataAccessException.class, () -> userService.login(user));
        assertThrows(DataAccessException.class, () -> userService.login(user1));
        assertThrows(DataAccessException.class, () -> userService.login(user2));
        assertThrows(DataAccessException.class, () -> userService.login(wrongPassword));

    }
}
