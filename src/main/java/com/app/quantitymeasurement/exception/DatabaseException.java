package com.app.quantitymeasurement.exception;

/**
 * DatabaseException - Custom unchecked exception for database-layer errors.
 * UC16: Wraps JDBC exceptions with meaningful context for upper layers.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}