package server.exception;

public class AlreadyTakenException extends BaseException {
    public AlreadyTakenException(String message) {
        super(message, 403);
    }
}
