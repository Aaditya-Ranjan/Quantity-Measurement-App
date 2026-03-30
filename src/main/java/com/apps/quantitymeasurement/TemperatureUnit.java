package com.apps.quantitymeasurement;

import java.util.function.Function;

/**
 * TemperatureUnit - Enum implementing IMeasurable for temperature measurements.
 * Base unit: CELSIUS (for internal normalization)
 * UC14: Uses non-linear conversion formulas via Function<Double,Double> lambdas.
 * Arithmetic operations are NOT supported (unsupported by design).
 */
public enum TemperatureUnit implements IMeasurable {

    CELSIUS(
            celsius -> celsius,                          // toCelsius: identity
            celsius -> celsius                           // fromCelsius: identity
    ),
    FAHRENHEIT(
            fahrenheit -> (fahrenheit - 32.0) * 5.0 / 9.0,   // toCelsius
            celsius -> celsius * 9.0 / 5.0 + 32.0             // fromCelsius
    );

    // Lambda functions for non-linear conversion
    private final Function<Double, Double> toCelsius;
    private final Function<Double, Double> fromCelsius;

    // Lambda expression: TemperatureUnit does NOT support arithmetic
    private final SupportsArithmetic arithmeticSupport = () -> false;

    TemperatureUnit(Function<Double, Double> toCelsius,
                    Function<Double, Double> fromCelsius) {
        this.toCelsius = toCelsius;
        this.fromCelsius = fromCelsius;
    }

    /**
     * Converts value to base unit (CELSIUS) using unit-specific formula.
     * NOTE: getConversionFactor() is meaningless for non-linear conversions.
     */
    @Override
    public double getConversionFactor() {
        return 1.0; // Not used for temperature — conversion is non-linear
    }

    /**
     * Converts value in this unit to CELSIUS (base unit).
     */
    @Override
    public double convertToBaseUnit(double value) {
        return toCelsius.apply(value);
    }

    /**
     * Converts value from CELSIUS (base unit) to this unit.
     */
    @Override
    public double convertFromBaseUnit(double baseValue) {
        return fromCelsius.apply(baseValue);
    }

    @Override
    public String getUnitName() {
        return this.name();
    }

    /**
     * TemperatureUnit does NOT support arithmetic operations.
     */
    @Override
    public boolean supportsArithmetic() {
        return arithmeticSupport.isSupported();
    }

    /**
     * Throws UnsupportedOperationException for any arithmetic operation.
     * Provides a clear, descriptive error message.
     */
    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
                "Temperature does not support " + operation +
                        ". Arithmetic on absolute temperatures is not physically meaningful.");
    }
    @Override
    public String getMeasurementType() { return "TEMPERATURE"; }
}