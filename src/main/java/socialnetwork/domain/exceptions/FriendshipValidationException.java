package socialnetwork.domain.exceptions;

import socialnetwork.domain.validators.ValidationException;

public class FriendshipValidationException extends ValidationException {
    /**
     * Variatiuni de constructori ale clasei care genereaza o exceptie de tipul FriendshipValidationException
     * care mosteneste din clasa ValidationException
     */


    public FriendshipValidationException() { }

    public FriendshipValidationException(String message){ super(message); }
    public FriendshipValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    public FriendshipValidationException(Throwable cause) {
        super(cause);
    }
    public FriendshipValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
