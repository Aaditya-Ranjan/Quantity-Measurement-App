package com.apps.quantitymeasurement;

/**
 * QuantityMeasurementApp - Main application class for quantity measurement operations.
 * Provides length comparison, conversion, and addition functionality.
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
     * Supports equality comparison, unit conversion, and addition.
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

        public double getValue() {
            return this.value;
        }

        /**
         * Converts value to base unit (FEET) for comparison/arithmetic.
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

        /**
         * Private utility method for addition - converts both lengths to base unit,
         * sums them, and converts result to target unit.
         * Avoids code duplication between overloaded add() methods.
         *
         * @param first      first QuantityLength operand
         * @param second     second QuantityLength operand
         * @param targetUnit unit for the result
         * @return new QuantityLength in target unit
         */
        private static QuantityLength addInTargetUnit(QuantityLength first, QuantityLength second, LengthUnit targetUnit) {
            if (first == null || second == null)
                throw new IllegalArgumentException("Operands cannot be null");
            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit cannot be null");
            if (!Double.isFinite(first.value) || !Double.isFinite(second.value))
                throw new IllegalArgumentException("Values must be finite numbers");

            double sumInBase = first.toBaseUnit() + second.toBaseUnit();
            double resultValue = Math.round((sumInBase / targetUnit.getConversionFactor()) * 1e10) / 1e10;
            return new QuantityLength(resultValue, targetUnit);
        }

        /**
         * UC6: Adds two lengths, result in unit of first operand.
         */
        public static QuantityLength add(QuantityLength first, QuantityLength second) {
            return addInTargetUnit(first, second, first.unit);
        }

        /**
         * UC7: Adds two lengths with explicit target unit specification.
         *
         * @param first      first QuantityLength operand
         * @param second     second QuantityLength operand
         * @param targetUnit explicitly specified result unit
         * @return new QuantityLength in specified target unit
         */
        public static QuantityLength add(QuantityLength first, QuantityLength second, LengthUnit targetUnit) {
            return addInTargetUnit(first, second, targetUnit);
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

    /**
     * Demonstrates addition of two QuantityLength objects.
     */
    public static void demonstrateLengthAddition(QuantityLength q1, QuantityLength q2) {
        QuantityLength result = QuantityLength.add(q1, q2);
        System.out.printf("add(%s, %s) = %s%n", q1, q2, result);
    }


    public static void main(String[] args) {
        demonstrateLengthAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(2.0, LengthUnit.FEET));
        demonstrateLengthAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES));
        demonstrateLengthAddition(
                new QuantityLength(12.0, LengthUnit.INCHES),
                new QuantityLength(1.0, LengthUnit.FEET));
        demonstrateLengthAddition(
                new QuantityLength(1.0, LengthUnit.YARDS),
                new QuantityLength(3.0, LengthUnit.FEET));
        demonstrateLengthAddition(
                new QuantityLength(2.54, LengthUnit.CENTIMETERS),
                new QuantityLength(1.0, LengthUnit.INCHES));
        // UC6 demos
            demonstrateLengthAddition(
                    new QuantityLength(1.0, LengthUnit.FEET),
                    new QuantityLength(12.0, LengthUnit.INCHES));

            // UC7 demos
            System.out.println(QuantityLength.add(
                    new QuantityLength(1.0, LengthUnit.FEET),
                    new QuantityLength(12.0, LengthUnit.INCHES),
                    LengthUnit.YARDS));

            System.out.println(QuantityLength.add(
                    new QuantityLength(36.0, LengthUnit.INCHES),
                    new QuantityLength(1.0, LengthUnit.YARDS),
                    LengthUnit.FEET));

    }
}