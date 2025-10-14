package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import io.javalin.Javalin;
import service.*;

import javax.naming.Context;

public class Server {
    private final Javalin server;
    private final UserService userService;

    public Server(){
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(null);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
    }

    private void register(Context ctx){
        //Call the service and register
        try{
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);
            var registrationResponse = userService.register(user);

            ctx.result(serializer.toJson(registrationResponse));

        } catch (Exception ex) {
            var errorMsg = String.format("{\"message\": \"Error\"}");
            ctx.status(403).result(ex.getMessage());
        }

    }

    public int run(int desiredPort){
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {server.port();}

}
