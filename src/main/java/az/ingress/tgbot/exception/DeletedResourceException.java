package az.ingress.tgbot.exception;

public class DeletedResourceException extends RuntimeException {

    public DeletedResourceException(String message) {
        super(message);
    }

}