package dataaccess;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    final private Code code;

    public DataAccessException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public DataAccessException(String message) {
        super(message);
        this.code = Code.ClientError;
    }

    public DataAccessException(String message, Throwable ex, Code code) {
        super(message, ex);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public int toHttpStatusCode() {
        return switch (code) {
            case ServerError -> 500;
            case ClientError -> 400;
        };
    }

    public enum Code {
        ServerError,
        ClientError
    }
}