package socialnetwork.domain.exceptions;

import socialnetwork.domain.validators.ValidationException;

public class EventValidationException extends ValidationException {
    public EventValidationException() { }

    public EventValidationException(String message){ super(message); }
    public EventValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    public EventValidationException(Throwable cause) {
        super(cause);
    }
    public EventValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
