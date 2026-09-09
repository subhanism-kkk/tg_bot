
package az.ingress.tgbot.exception;

public class ResourceAlreadyExistsException
        extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

}