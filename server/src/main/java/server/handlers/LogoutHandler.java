package server.handlers;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import service.UserService;
import service.requests.LogoutRequest;

import java.util.Map;

public class LogoutHandler {
    private final Gson gson;
    private final UserService user;

    public LogoutHandler(Gson gson, UserService user) {
        this.gson = gson;
        this.user = user;
    }

    public void logout(Context ctx) {
        try {
            String token = ctx.header("authorization");
            var request = new LogoutRequest(token);
            user.logout(request);
            ctx.status(200).contentType("application/json").
                    result("{}");
        } catch (DataAccessException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("unauthorized")) {
                ctx.status(401).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else {
                ctx.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + message)));
            }
        }
    }
}
