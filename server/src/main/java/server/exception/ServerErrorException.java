package server.exception;

public class ServerErrorException extends BaseException {
    public ServerErrorException(String message) {
        super(message, 500);
    }
}
