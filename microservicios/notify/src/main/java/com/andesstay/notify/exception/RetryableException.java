package com.andesstay.notify.exception;

/** Error recuperable (timeout SMTP, servicio externo caído): se reintenta y, si se agotan, va a la DLQ. */
public class RetryableException extends RuntimeException {
    public RetryableException(String message) {
        super(message);
    }

    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
