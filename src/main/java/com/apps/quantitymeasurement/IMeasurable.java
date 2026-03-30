package com.apps.quantitymeasurement;

/**
 * IMeasurable - Interface for all measurement units.
 * UC15: Added getMeasurementType() to support N-Tier service layer mapping.
 */
public interface IMeasurable {

    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
    String getMeasurementType(); // UC15: "LENGTH", "WEIGHT", "VOLUME", "TEMPERATURE"

    @FunctionalInterface
    interface SupportsArithmetic {
        boolean isSupported();
    }

    SupportsArithmetic supportsArithmetic = () -> true;

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    default void validateOperationSupport(String operation) {
        // no-op by default
    }
}