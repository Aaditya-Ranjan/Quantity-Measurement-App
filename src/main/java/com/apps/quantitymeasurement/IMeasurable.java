package com.apps.quantitymeasurement;

/**
 * IMeasurable - Interface defining the contract for all measurement units.
 * UC14: Refactored with default methods for optional operation support.
 * Follows Interface Segregation Principle — categories implement only what they support.
 */
public interface IMeasurable {

    // ===== Mandatory Conversion Methods =====

    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();

    // ===== Functional Interface for Operation Support =====

    /**
     * Functional interface to indicate whether a unit supports arithmetic operations.
     * Enables concise lambda expression assignment per unit.
     */
    @FunctionalInterface
    interface SupportsArithmetic {
        boolean isSupported();
    }

    // ===== Default Lambda — All units support arithmetic by default =====

    SupportsArithmetic supportsArithmetic = () -> true;

    // ===== Default Methods — Optional Operation Support =====

    /**
     * Returns true if this unit supports arithmetic operations.
     * Default: true (all existing units support arithmetic).
     * Override in units like TemperatureUnit to return false.
     */
    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    /**
     * Validates that the given operation is supported for this unit.
     * Default: no-op (all existing units allow all operations).
     * Override in TemperatureUnit to throw UnsupportedOperationException.
     *
     * @param operation name of the operation being attempted
     * @throws UnsupportedOperationException if operation is not supported
     */
    default void validateOperationSupport(String operation) {
        // Default: all units support all operations — no-op
    }
}