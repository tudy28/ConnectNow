package socialnetwork.domain.exceptions;

public class ServiceException extends RuntimeException{
    /**
     * Variatiuni de constructori ale clasei care genereaza o exceptie de tipul ServiceException
     * care mosteneste din clasa RuntimeException
     */

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
