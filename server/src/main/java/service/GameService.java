package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

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
    
}
