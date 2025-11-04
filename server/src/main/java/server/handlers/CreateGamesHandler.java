package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.requests.CreateGameRequest;
import service.results.CreateGameResult;

import java.util.Map;

public class CreateGamesHandler {
    private final Gson gson;
    private final GameService game;

    public CreateGamesHandler(Gson gson, GameService game) {
        this.gson = gson;
        this.game = game;
    }

    public void createGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            var body = gson.fromJson(ctx.body(), java.util.Map.class);
            String gameName = (String) body.get("gameName");
            var request = new CreateGameRequest(token, gameName);
            var result = game.createGame(request);
            ctx.status(200).contentType("application/json").
                    result(gson.toJson(result, CreateGameResult.class));
        } catch (DataAccessException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("bad request")) {
                ctx.status(400).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: bad request")));
            } else if (message.contains("unauthorized")) {
                ctx.status(401).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else {

                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + message)));
            }
        }
    }
}
