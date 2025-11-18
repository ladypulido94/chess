package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.json.JavalinGson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();

        clearService = new ClearService(dataAccess);
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
                });

        record CreateGameRequest(String gameName){
        }

        record JoinGameRequest(String playerColor, int gameID){}

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", (ctx) -> {
            try {
                clearService.clear();
                ctx.status(200);
                ctx.json(Map.of());
            } catch (DataAccessException e) {
                ctx.status(500);
                ctx.json(Map.of("message",e.getMessage()));
            }
        });

        javalin.post("/user", (ctx) -> {
           try {
               UserData user = ctx.bodyAsClass(UserData.class);
               AuthData token = userService.register(user);
               ctx.status(200);
               ctx.json(token);
           } catch (DataAccessException e) {
               String message = e.getMessage();
               if(message.equals("Error: Bad Request")){
                   ctx.status(400);
               } else if (message.equals("Error: User already taken")){
                   ctx.status(403);
               } else {
                   ctx.status(500);
               }
               ctx.json(Map.of("message", message));
           }
        });

        javalin.post("/session", (ctx) -> {
           try {
               UserData user = ctx.bodyAsClass(UserData.class);
               AuthData token = userService.login(user);
               ctx.status(200);
               ctx.json(token);

           } catch (DataAccessException e) {
               String message = e.getMessage();
               if(message.equals("Error: Bad Request")){
                   ctx.status(400);
               } else if (message.equals("Error: Unauthorized")){
                   ctx.status(401);
               } else {
                   ctx.status(500);
               }
               ctx.json(Map.of("message", message));
           }
        });

        javalin.delete("/session", (ctx) -> {
            try {
                String authToken = ctx.header("authorization");
                userService.logout(authToken);
                ctx.status(200);
                ctx.json(Map.of());

            } catch (DataAccessException e) {
                ctx.status(401);
                ctx.json(Map.of("message", e.getMessage()));

            }
        });

        javalin.get("/game", (ctx) -> {
            try{
                String authToken = ctx.header("authorization");
                Collection<GameData> games = gameService.listAllGames(authToken);
                ctx.status(200);
                ctx.json(Map.of("games", games));

            } catch (DataAccessException e) {
                ctx.status(401);
                ctx.json(Map.of("message",e.getMessage()));
            }
        });

        javalin.post("/game", (ctx) -> {
            try {
                String authToken = ctx.header("authorization");
                CreateGameRequest gameRequest = ctx.bodyAsClass(CreateGameRequest.class);
                int gameId = gameService.createGame(authToken, gameRequest.gameName);
                ctx.status(200);
                ctx.json(Map.of("gameID",gameId));
            } catch (DataAccessException e) {
                String message = e.getMessage();

                if (message.equals("Error: Unauthorized")){
                    ctx.status(401);

                } else if (message.equals("Error: Bad Request")){
                    ctx.status(400);
                } else {
                    ctx.status(500);
                }
                ctx.json(Map.of("message", message));
            }
        });

        javalin.put("/game", (ctx) -> {
            try {
                String token = ctx.header("authorization");
                JoinGameRequest gameRequest = ctx.bodyAsClass(JoinGameRequest.class);
                gameService.joinGame(token, gameRequest.playerColor, gameRequest.gameID);
                ctx.status(200);
                ctx.json(Map.of());
            } catch (DataAccessException e) {
                String message = e.getMessage();

                if (message.equals("Error: Bad Request")){
                    ctx.status(400);

                } else if (message.equals("Error: Unauthorized")){
                    ctx.status(401);

                } else if (message.equals("Error: Already Taken")){
                    ctx.status(403);

                } else {
                    ctx.status(500);
                }

                ctx.json(Map.of("message", message));
            }
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
