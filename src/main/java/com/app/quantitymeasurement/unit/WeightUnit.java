package com.app.quantitymeasurement.unit;

/**
 * WeightUnit - Standalone enum implementing IMeasurable.
 * Base unit: KILOGRAM
 */
public enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double conversionFactor;

    WeightUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * this.conversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / this.conversionFactor;
    }

    @Override
    public String getUnitName() {
        return this.name();
    }
    @Override
    public String getMeasurementType() { return "WEIGHT"; }
}