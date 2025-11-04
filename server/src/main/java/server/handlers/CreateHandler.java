package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.requests.CreateRequest;
import service.results.CreateResult;

import java.util.Map;

public class CreateHandler {
    private final Gson gson;
    private final GameService game;

    public CreateHandler(Gson gson, GameService game) {
        this.gson = gson;
        this.game = game;
    }

    public void createGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            var body = gson.fromJson(ctx.body(), java.util.Map.class);
            String gameName = (String) body.get("gameName");
            var request = new CreateRequest(token, gameName);
            var result = game.createGame(request);
            ctx.status(200).contentType("application/json").
                    result(gson.toJson(result, CreateResult.class));
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
