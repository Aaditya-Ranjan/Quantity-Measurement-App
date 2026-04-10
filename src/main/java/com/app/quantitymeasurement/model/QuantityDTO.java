package com.app.quantitymeasurement.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * QuantityDTO - API input DTO with validation annotations.
 * UC17: Adds @NotNull, @NotEmpty, @Pattern for input validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Value cannot be null")
    private Double value;

    @NotEmpty(message = "Unit cannot be empty")
    private String unit;

    @NotEmpty(message = "Measurement type cannot be empty")
    @Pattern(regexp = "LengthUnit|WeightUnit|VolumeUnit|TemperatureUnit",
            message = "measurementType must be LengthUnit, WeightUnit, VolumeUnit, or TemperatureUnit")
    private String measurementType;

    // Legacy nested enums preserved for backward compatibility
    public interface IMeasurableUnit extends java.io.Serializable {
        String getUnitName();
        String getMeasurementType();
    }

    public enum LengthUnit implements IMeasurableUnit {
        FEET, INCHES, YARDS, CENTIMETERS;
        public String getUnitName() { return name(); }
        public String getMeasurementType() { return "LengthUnit"; }
    }

    public enum WeightUnit implements IMeasurableUnit {
        KILOGRAM, GRAM, POUND;
        public String getUnitName() { return name(); }
        public String getMeasurementType() { return "WeightUnit"; }
    }

    public enum VolumeUnit implements IMeasurableUnit {
        LITRE, MILLILITRE, GALLON;
        public String getUnitName() { return name(); }
        public String getMeasurementType() { return "VolumeUnit"; }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT;
        public String getUnitName() { return name(); }
        public String getMeasurementType() { return "TemperatureUnit"; }
    }
}