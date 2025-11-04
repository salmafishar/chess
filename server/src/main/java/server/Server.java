package server;

import com.google.gson.Gson;
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
        var listHandler = new ListGamesHandler(new Gson(), gameService);
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
        javalin.get("/game", listHandler::list);
        javalin.post("/game", createHandler);
        javalin.put("/game", joinHandler);
    }

    private static final Gson GSON = new Gson();

    private void exceptionHandler(Exception ex, Context ctx) {
        ctx.contentType("application/json");

        if (ex instanceof server.exception.BaseException base) {
            ctx.status(base.toHttpStatusCode()).result(base.toJson());
            return;
        }

        if (ex instanceof dataaccess.DataAccessException dao) {
            String message = dao.getMessage();
            int code;
            if (message.equals("unauthorized") || message.contains("unauthor") || message.contains("not found") || message.contains("invalid credential")) {
                code = 401;
            } else if (message.equals("already taken") || message.contains("taken") || message.contains("conflict")) {
                code = 403;
            } else if (message.equals("bad request") || message.contains("bad") || message.contains("malformed") || message.contains("invalid") || message.contains("missing")) {
                code = 400;
            } else {
                code = 500;
            }
            String canon = switch (code) {
                case 400 -> "bad request";
                case 401 -> "unauthorized";
                case 403 -> "already taken";
                default -> "server error";
            };
            ctx.status(code).result(GSON.toJson(Map.of("message", "Error: " + canon)));
            return;
        }
        ctx.status(500).result(GSON.toJson(Map.of("message", "Error: server error")));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}