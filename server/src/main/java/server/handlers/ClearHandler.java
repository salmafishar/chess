package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import java.util.Map;

public class ClearHandler {
    private final Gson gson;
    private final DataAccess data;

    public ClearHandler(Gson gson, DataAccess data) {
        this.gson = gson;
        this.data = data;
    }

    public void clear(Context ctx) {
        try {
            data.clear();
            ctx.status(200).contentType("application/json").result("{}");
        } catch (DataAccessException e) {
            ctx.status(500).contentType("application/json")
                    .result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
