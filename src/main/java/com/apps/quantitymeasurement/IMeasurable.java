package com.apps.quantitymeasurement;

/**
 * IMeasurable - Interface defining the contract for all measurement units.
 * All unit enums (LengthUnit, WeightUnit, etc.) must implement this interface.
 * Enables generic Quantity<U> class to work with any measurement category.
 */
public interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}