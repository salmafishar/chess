package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.Javalin;
import server.handlers.*;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

//        MySqlDataAccess dao;
        final DataAccess dao;  // interface type
        try {
            dao = new MySqlDataAccess();    // constructor throws -> catch
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database", e);

        }

        // Register your endpoints and exception handlers here.
        var userService = new UserService(dao);
        var gameService = new GameService(dao);
        var registerHandler = new RegisterHandler(new Gson(), userService);
        var loginHandler = new LoginHandler(new Gson(), userService);
        var logoutHandler = new LogoutHandler(new Gson(), userService);
        var listHandler = new ListHandler(new Gson(), gameService);
        var createHandler = new CreateHandler(new Gson(), gameService);
        var joinHandler = new JoinHandler(new Gson(), gameService);
        var clearHandler = new ClearHandler(new Gson(), dao);

        // endpoints
        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", registerHandler::register);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.get("/game", listHandler::list);
        javalin.post("/game", createHandler::createGame);
        javalin.put("/game", joinHandler::join);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}