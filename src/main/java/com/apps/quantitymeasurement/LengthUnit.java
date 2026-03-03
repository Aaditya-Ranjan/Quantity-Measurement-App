package com.apps.quantitymeasurement;

/**
 * LengthUnit - Standalone enum responsible for unit conversion logic.
 * Single Responsibility: handles all conversion to/from base unit (FEET).
 */
public enum LengthUnit {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(0.393701 / 12.0);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    /**
     * Converts a value in this unit to the base unit (FEET).
     * @param value value in this unit
     * @return value in feet
     */
    public double convertToBaseUnit(double value) {
        return value * this.conversionFactor;
    }

    /**
     * Converts a value from the base unit (FEET) to this unit.
     * @param baseValue value in feet
     * @return value in this unit
     */
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / this.conversionFactor;
    }
}