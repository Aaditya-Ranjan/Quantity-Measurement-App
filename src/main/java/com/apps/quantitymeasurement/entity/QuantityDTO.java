package com.apps.quantitymeasurement.entity;

import java.io.Serializable;

/**
 * QuantityDTO - Data Transfer Object for input/output between layers.
 * Contains self-contained nested enums for external representation.
 * UC15: External API contract — decoupled from internal IMeasurable enums.
 */
public class QuantityDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double value;
    private final IMeasurableUnit unit;

    public QuantityDTO(double value, IMeasurableUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() { return value; }
    public IMeasurableUnit getUnit() { return unit; }

    @Override
    public String toString() {
        return value + " " + (unit != null ? unit.getUnitName() : "null");
    }

    // ===== Nested Interface =====

    /**
     * IMeasurableUnit - Internal interface within QuantityDTO.
     * Different from application's IMeasurable; maps to it via service layer.
     */
    public interface IMeasurableUnit extends Serializable {
        String getUnitName();
        String getMeasurementType();
    }

    // ===== Nested Enums =====

    public enum LengthUnit implements IMeasurableUnit {
        FEET, INCHES, YARDS, CENTIMETERS;
        @Override public String getUnitName() { return name(); }
        @Override public String getMeasurementType() { return "LENGTH"; }
    }

    public enum WeightUnit implements IMeasurableUnit {
        KILOGRAM, GRAM, POUND;
        @Override public String getUnitName() { return name(); }
        @Override public String getMeasurementType() { return "WEIGHT"; }
    }

    public enum VolumeUnit implements IMeasurableUnit {
        LITRE, MILLILITRE, GALLON;
        @Override public String getUnitName() { return name(); }
        @Override public String getMeasurementType() { return "VOLUME"; }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT;
        @Override public String getUnitName() { return name(); }
        @Override public String getMeasurementType() { return "TEMPERATURE"; }
    }
}