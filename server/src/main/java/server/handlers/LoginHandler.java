package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserService;
import service.requests.LoginRequest;
import service.results.LoginResult;

import java.util.Map;

public class LoginHandler {
    private final Gson gson;
    private final UserService user;

    public LoginHandler(Gson gson, UserService user) {
        this.gson = gson;
        this.user = user;
    }

    public void login(Context ctx) {
        try {
            var request = gson.fromJson(ctx.body(), LoginRequest.class);// name, pass, email
            var result = user.login(request);

            ctx.status(200).contentType("application/json").
                    result(gson.toJson(result, LoginResult.class));
        } catch (DataAccessException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("bad request")) {
                ctx.status(400).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: bad request")));
            } else if (message.contains("unauthorized") || message.contains("not found")) {
                ctx.status(401).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + message)));
            }
        }
    }
}
