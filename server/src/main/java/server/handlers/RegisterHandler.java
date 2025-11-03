package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;
import service.requests.RegisterRequest;

import java.util.Map;

public class RegisterHandler implements Handler {
    private final UserService userService;
    private Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            var result = userService.register(request);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));

        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("bad request")) {
                ctx.status(400).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: bad request")));
            } else if (message.toLowerCase().contains("already taken")) {
                ctx.status(403).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: already taken")));
            } else {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        } catch (Exception e) {
            ctx.status(500).contentType("application/json")
                    .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
