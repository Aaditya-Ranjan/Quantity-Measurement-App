package com.app.quantitymeasurement.exception;

/**
 * QuantityMeasurementException - Custom unchecked exception for domain errors.
 * UC15: Centralizes all quantity measurement-related exceptions.
 * Extends RuntimeException — no forced try-catch blocks required.
 */
public class QuantityMeasurementException extends RuntimeException {

    public QuantityMeasurementException(String message) {
        super(message);
    }

    public QuantityMeasurementException(String message, Throwable cause) {
        super(message, cause);
    }
}