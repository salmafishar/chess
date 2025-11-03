package server.handlers;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import service.UserService;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

import java.util.Map;


// request -> name, password, email
// result ->name, token

public class RegisterHandler {
    private final Gson gson;
    private final UserService user;

    public RegisterHandler(Gson gson, UserService user) {
        this.gson = gson;
        this.user = user;
    }

    // handler, reads JSON, call server, write JSON, map errors
    public void register(Context ctx) {
        try {
            var request = gson.fromJson(ctx.body(), RegisterRequest.class);// name, pass, email
            var result = user.register(request);
            ctx.status(200).contentType("application/json").
                    result(gson.toJson(result, RegisterResult.class));
        } catch (DataAccessException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("bad request")) {
                ctx.status(400).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: bad request")));
            } else if (message.contains("already")) {
                ctx.status(403).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: already taken")));
            } else {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + message)));
            }
        }

    }
}
