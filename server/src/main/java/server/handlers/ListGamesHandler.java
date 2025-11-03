package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.requests.ListGameRequest;
import service.results.ListGamesResult;

import java.util.Map;

public class ListGamesHandler {
    private final Gson gson;
    private final GameService game;

    public ListGamesHandler(Gson gson, GameService game) {
        this.gson = gson;
        this.game = game;
    }

    public void list(Context ctx) {
        try {
            String token = ctx.header("authorization");
            var request = new ListGameRequest(token);
            var result = game.listGames(request);
            ctx.status(200).contentType("application/json").
                    result(gson.toJson(result, ListGamesResult.class));
        } catch (DataAccessException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("unauthorized")) {
                ctx.status(401).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + message)));
            }
        } catch (Exception e) {
            ctx.status(500).contentType("application/json")
                    .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
