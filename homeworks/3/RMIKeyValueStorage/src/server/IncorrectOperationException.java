package server;

/**
 * Created by arseny on 08.12.14.
 */
public class IncorrectOperationException extends RuntimeException {
    IncorrectOperationException(String message) {
        super(message);
    }
}
