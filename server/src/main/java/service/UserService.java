package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException{

        if (user.username() == null || user.username().isEmpty() ||
        user.password() == null || user.password().isEmpty() ||
        user.email() == null || user.email().isEmpty()){
            throw new DataAccessException("Error: Bad Request");
        }

        UserData existingUser = dataAccess.getUser(user.username());

        if(existingUser != null){
            throw new DataAccessException("Error: User already taken");
        }

        dataAccess.addUser(user);

        String authenticator = UUID.randomUUID().toString();
        AuthData authToken = new AuthData(authenticator, user.username());
        dataAccess.addAuthToken(authToken);

        return authToken;
    }

    public AuthData login (UserData user) throws DataAccessException{
        if (user.username() == null || user.username().isEmpty() ||
        user.password() == null || user.password().isEmpty()){
            throw new DataAccessException("Error: Bad Request");
        }

        UserData existingUser = dataAccess.getUser(user.username());

        if (existingUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if(!user.password().equals(existingUser.password())){
            throw new DataAccessException("Error: Incorrect Password");
        }

        String authenticator = UUID.randomUUID().toString();
        AuthData authToken = new AuthData(authenticator, user.username());
        dataAccess.addAuthToken(authToken);

        return authToken;
    }

    public void logout (String authToken) throws DataAccessException {
        AuthData token = dataAccess.getAuthToken(authToken);
        if (token == null){
            throw new DataAccessException("Error: AuthToken doesn't exist");
        }

        dataAccess.deleteAuthToken(authToken);
    }
}
