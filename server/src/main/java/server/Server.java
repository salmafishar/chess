package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import io.javalin.Javalin;
import server.handlers.*;
import service.GameService;
import service.UserService;
import io.javalin.websocket.WsContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private final Javalin javalin;
    private final GameService gameService;
    private final DataAccess dao;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        final DataAccess dao;
        try {
            this.dao = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database", e);

        }

        // Register your endpoints and exception handlers here.
        var userService = new UserService(this.dao);
        this.gameService = new GameService(this.dao);
        var registerHandler = new RegisterHandler(new Gson(), userService);
        var loginHandler = new LoginHandler(new Gson(), userService);
        var logoutHandler = new LogoutHandler(new Gson(), userService);
        var listHandler = new ListHandler(new Gson(), this.gameService);
        var createHandler = new CreateHandler(new Gson(), this.gameService);
        var joinHandler = new JoinHandler(new Gson(), this.gameService);
        var clearHandler = new ClearHandler(new Gson(), this.dao);

        // endpoints
        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", registerHandler::register);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.get("/game", listHandler::list);
        javalin.post("/game", createHandler::createGame);
        javalin.put("/game", joinHandler::join);
        var webSocketHandler = new WebSocketHandler(this.gameService, this.dao);
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
            ws.onError(webSocketHandler);
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