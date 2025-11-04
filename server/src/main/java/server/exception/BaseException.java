package server.exception;


import com.google.gson.Gson;

import java.util.Map;


public class BaseException extends RuntimeException {
    private final int code;

    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int toHttpStatusCode() {
        return code;
    }

    private static final Gson GSON = new Gson();

    public String toJson() {
        return GSON.toJson(Map.of("message", "Error: " + getMessage()));
    }

}
