package server;

import dataaccess.MemoryDataAccess;
import server.handlers.*;
import io.javalin.*;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);
        var registerHandler = new RegisterHandler(new Gson(), userService);
        var loginHandler = new LoginHandler(new Gson(), userService);
        var logoutHandler = new LogoutHandler(new Gson(), userService);
        var listHandler = new ListGamesHandler(gameService);
        var createHandler = new CreateGamesHandler(gameService);
        var joinHandler = new JoinGameHandler(gameService);
        // endpoints
        javalin.delete("/db", ctx -> {
            dao.clear();
            ctx.status(200).
                    contentType("application/json").result("{}");
        });
        javalin.post("/user", registerHandler::register);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.get("/game", listHandler);
        javalin.post("/game", createHandler);
        javalin.put("/game", joinHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}