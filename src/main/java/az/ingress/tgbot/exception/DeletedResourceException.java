package az.ingress.hrms.exception;

public class DeletedResourceException extends RuntimeException {

    public DeletedResourceException(String message) {
        super(message);
    }

}