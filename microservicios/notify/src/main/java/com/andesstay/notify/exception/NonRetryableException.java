package com.andesstay.notify.exception;

/** Error NO recuperable (payload inválido, dato faltante): va directo a la DLQ sin reintentos. */
public class NonRetryableException extends RuntimeException {
    public NonRetryableException(String message) {
        super(message);
    }

    public NonRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
