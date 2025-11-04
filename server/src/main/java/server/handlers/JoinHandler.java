package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.requests.JoinRequest;

import java.util.Map;

public class JoinHandler {
    private final Gson gson;
    private final GameService service;

    public JoinHandler(Gson gson, GameService service) {
        this.gson = gson;
        this.service = service;
    }

    public void join(Context ctx) {
        try {
            String token = ctx.header("authorization");

            var body = gson.fromJson(ctx.body(), java.util.Map.class);
            Integer gameID = null;
            String playerColor = null;

            if (body != null) {
                Object idRaw = body.get("gameID");
                if (idRaw instanceof Number n) {
                    gameID = n.intValue();
                }

                Object colorRaw = body.get("playerColor");
                if (colorRaw instanceof String s && !s.isBlank()) {
                    playerColor = s;
                }
            }

            var req = new JoinRequest(token, gameID, playerColor);
            service.joinGame(req);

            ctx.status(200).contentType("application/json").result("{}");

        } catch (DataAccessException e) {
            String m = e.getMessage().toLowerCase();

            if (m.contains("bad request")) {
                ctx.status(400).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: bad request")));
            } else if (m.contains("unauthorized")) {
                ctx.status(401).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else if (m.contains("already taken")) {
                ctx.status(403).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: already taken")));
            } else {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        }
    }
}

