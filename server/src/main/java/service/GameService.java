package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final DataAccess dataAccess;

    public GameService (DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public int createGame(AuthData token, String gameName) throws DataAccessException{

        if(token.authToken() == null || token.authToken().isEmpty()
                || dataAccess.getAuthToken(token.authToken()) == null){
            throw new DataAccessException("Error: Unauthorize");
        }

        if(gameName.isEmpty()){
            throw new DataAccessException("Error: Bad Request");
        }

        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, gameName, chessGame);

        return dataAccess.addGame(game);
    }

    public void joinGame(String authToken, String playerColor,int gameID) throws DataAccessException {
        GameData game = dataAccess.getGame(gameID);
        AuthData token = dataAccess.getAuthToken(authToken);

        if(token == null){
            throw new DataAccessException("Error: Unauthorized");
        }

        if(game == null){
            throw new DataAccessException("Error: Bad Request");
        }

        playerColor = playerColor.toUpperCase();

        if(playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))){
            throw new DataAccessException("Error: Bad Request");
        }

        String color = playerColor.toUpperCase();

        if(color.equals("WHITE") && game.whiteUsername() != null){
            throw new DataAccessException("Error: Already Taken");
        }

        if(color.equals("BLACK") && game.blackUsername() != null){
            throw new DataAccessException("Error: Already Taken");
        }

        String username = token.username();
        GameData updatedGame;

        if(color.equals("WHITE")){
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }

        dataAccess.updateGame(updatedGame);

    }

    public Collection<GameData> listAllGames(String authToken) throws DataAccessException{
        AuthData token = dataAccess.getAuthToken(authToken);

        if(token == null){
            throw new DataAccessException("Error: Unauthorized");
        }

        Collection<GameData> games = dataAccess.getAllGames();

        return games;
    }

}
