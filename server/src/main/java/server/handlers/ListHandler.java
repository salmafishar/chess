package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.requests.ListRequest;
import service.results.ListResult;

import java.util.Map;

public class ListHandler {
    private final Gson gson;
    private final GameService game;

    public ListHandler(Gson gson, GameService game) {
        this.gson = gson;
        this.game = game;
    }

    public void list(Context ctx) {
        try {
            String token = ctx.header("authorization");
            var req = new ListRequest(token);
            var res = game.listGames(req);
            ctx.status(200).contentType("application/json").
                    result(gson.toJson(res, ListResult.class));
        } catch (DataAccessException e) {
            String m = e.getMessage().toLowerCase();
            if (m.contains("unauthorized")) {
                ctx.status(401).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + m)));
            }
        } catch (Exception e) {
            ctx.status(500).contentType("application/json")
                    .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
