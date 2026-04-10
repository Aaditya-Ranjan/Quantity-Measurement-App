package com.app.quantitymeasurement.unit;

/**
 * VolumeUnit - Standalone enum implementing IMeasurable.
 * Base unit: LITRE
 * Validates UC10 generic design scales seamlessly to new categories.
 */
public enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double conversionFactor;

    VolumeUnit(double conversionFactor) {
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
    public String getMeasurementType() { return "VOLUME"; }

}