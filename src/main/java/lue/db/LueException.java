/*
 * see license.txt
 */
package lue.db;

/**
 * @author Tony
 *
 */
public class LueException extends RuntimeException {

    private static final long serialVersionUID = -1318057345789697197L;

    public LueException() {
    }

    /**
     * @param message
     */
    public LueException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public LueException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public LueException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public LueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
