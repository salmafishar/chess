package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.requests.LoginRequest;

import java.util.Map;

public class LoginHandler implements Handler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        try {
            LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);
            var result = userService.login(request);
            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(result));

        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("bad request")) {
                context.status(400).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: bad request")));
            } else if (message.toLowerCase().contains("unauthorized")) {
                context.status(401).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: unauthorized")));
            } else {
                context.status(500).contentType("application/json")
                        .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        } catch (Exception e) {
            context.status(500).contentType("application/json")
                    .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
