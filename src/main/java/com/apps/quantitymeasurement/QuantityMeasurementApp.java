package com.apps.quantitymeasurement;

/**
 * QuantityMeasurementApp - Main application class for quantity measurement operations.
 * Provides length comparison and conversion functionality using the QuantityLength class.
 */
public class QuantityMeasurementApp {

    /**
     * LengthUnit enum defines supported units with conversion factors relative to FEET as base unit.
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
    }

    /**
     * QuantityLength represents an immutable length measurement with a value and unit.
     * Supports equality comparison and unit conversion.
     */
    public static class QuantityLength {

        private final double value;
        private final LengthUnit unit;

        /**
         * @param value numeric measurement value
         * @param unit  LengthUnit (must not be null)
         * @throws NullPointerException if unit is null
         */
        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) throw new NullPointerException("Unit cannot be null");
            this.value = value;
            this.unit = unit;
        }

        /**
         * Converts value to base unit (FEET) for comparison.
         */
        private double toBaseUnit() {
            return this.value * this.unit.getConversionFactor();
        }

        /**
         * Converts this length to the target unit and returns a new QuantityLength instance.
         *
         * @param targetUnit the unit to convert to
         * @return new QuantityLength in target unit
         * @throws IllegalArgumentException if targetUnit is null
         */
        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");
            double convertedValue = this.toBaseUnit() / targetUnit.getConversionFactor();
            return new QuantityLength(convertedValue, targetUnit);
        }

        /**
         * Static conversion method.
         *
         * @param value      numeric value to convert
         * @param sourceUnit source LengthUnit
         * @param targetUnit target LengthUnit
         * @return converted double value
         * @throws IllegalArgumentException for null units, NaN, or infinite values
         */
        public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
            if (sourceUnit == null || targetUnit == null)
                throw new IllegalArgumentException("Units cannot be null");
            if (!Double.isFinite(value))
                throw new IllegalArgumentException("Value must be a finite number");
            return value * (sourceUnit.getConversionFactor() / targetUnit.getConversionFactor());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            QuantityLength other = (QuantityLength) obj;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }

        @Override
        public String toString() {
            return value + " " + unit.name();
        }
    }

    // ===== Demonstration Methods =====

    /**
     * Demonstrates conversion from raw value and units.
     */
    public static void demonstrateLengthConversion(double value, LengthUnit from, LengthUnit to) {
        double result = QuantityLength.convert(value, from, to);
        System.out.printf("convert(%.4f %s → %s) = %.4f%n", value, from, to, result);
    }

    /**
     * Demonstrates conversion from an existing QuantityLength instance.
     */
    public static void demonstrateLengthConversion(QuantityLength length, LengthUnit to) {
        QuantityLength result = length.convertTo(to);
        System.out.printf("convert(%s → %s) = %s%n", length, to, result);
    }

    /**
     * Demonstrates equality between two QuantityLength objects.
     */
    public static void demonstrateLengthEquality(QuantityLength q1, QuantityLength q2) {
        System.out.printf("equals(%s, %s) = %b%n", q1, q2, q1.equals(q2));
    }

    public static void main(String[] args) {
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES);
        demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
        demonstrateLengthConversion(0.0, LengthUnit.FEET, LengthUnit.INCHES);

        QuantityLength lengthInYards = new QuantityLength(1.0, LengthUnit.YARDS);
        demonstrateLengthConversion(lengthInYards, LengthUnit.INCHES);
    }
}