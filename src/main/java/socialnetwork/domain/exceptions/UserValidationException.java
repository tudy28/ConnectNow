package socialnetwork.domain.exceptions;

import socialnetwork.domain.validators.ValidationException;

public class UserValidationException extends ValidationException {
    /**
     * Variatiuni de constructori ale clasei care genereaza o exceptie de tipul UserValidationException
     * care mosteneste din clasa ValidationException
     */

    public UserValidationException() { }

    public UserValidationException(String message){ super(message); }
    public UserValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserValidationException(Throwable cause) {
        super(cause);
    }
    public UserValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
