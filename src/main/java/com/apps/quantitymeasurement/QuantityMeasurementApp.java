package com.apps.quantitymeasurement;

/**
 * QuantityMeasurementApp - Main application class.
 * QuantityLength delegates all conversion logic to LengthUnit (SRP).
 */
public class QuantityMeasurementApp {

    /**
     * QuantityLength - Immutable value object for length measurements.
     * Focuses solely on equality, conversion, and arithmetic.
     * Delegates unit conversion to LengthUnit enum.
     */
    public static class QuantityLength {

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) throw new NullPointerException("Unit cannot be null");
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
            this.value = value;
            this.unit = unit;
        }

        public double getValue() {
            return this.value;
        }

        public LengthUnit getUnit() {
            return this.unit;
        }

        // Delegates to LengthUnit
        private double toBaseUnit() {
            return this.unit.convertToBaseUnit(this.value);
        }

        /**
         * Converts this length to target unit.
         */
        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit cannot be null");
            double baseValue = this.toBaseUnit();
            double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
            return new QuantityLength(
                    Math.round(convertedValue * 1e10) / 1e10,
                    targetUnit);
        }

        /**
         * Static conversion method.
         */
        public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
            if (sourceUnit == null || targetUnit == null)
                throw new IllegalArgumentException("Units cannot be null");
            if (!Double.isFinite(value))
                throw new IllegalArgumentException("Value must be finite");
            double baseValue = sourceUnit.convertToBaseUnit(value);
            return targetUnit.convertFromBaseUnit(baseValue);
        }

        /**
         * Private utility method for addition - DRY principle.
         */
        private static QuantityLength addInTargetUnit(
                QuantityLength first, QuantityLength second, LengthUnit targetUnit) {
            if (first == null || second == null)
                throw new IllegalArgumentException("Operands cannot be null");
            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit cannot be null");
            double sumInBase = first.toBaseUnit() + second.toBaseUnit();
            double resultValue = Math.round(
                    targetUnit.convertFromBaseUnit(sumInBase) * 1e10) / 1e10;
            return new QuantityLength(resultValue, targetUnit);
        }

        // UC6: result in unit of first operand
        public static QuantityLength add(QuantityLength first, QuantityLength second) {
            return addInTargetUnit(first, second, first.unit);
        }

        // UC7: result in explicit target unit
        public static QuantityLength add(
                QuantityLength first, QuantityLength second, LengthUnit targetUnit) {
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

    public static void demonstrateLengthConversion(double value, LengthUnit from, LengthUnit to) {
        double result = QuantityLength.convert(value, from, to);
        System.out.printf("convert(%.4f %s → %s) = %.4f%n", value, from, to, result);
    }

    public static void demonstrateLengthConversion(QuantityLength length, LengthUnit to) {
        QuantityLength result = length.convertTo(to);
        System.out.printf("convert(%s → %s) = %s%n", length, to, result);
    }

    public static void demonstrateLengthEquality(QuantityLength q1, QuantityLength q2) {
        System.out.printf("equals(%s, %s) = %b%n", q1, q2, q1.equals(q2));
    }

    public static void demonstrateLengthAddition(QuantityLength q1, QuantityLength q2) {
        QuantityLength result = QuantityLength.add(q1, q2);
        System.out.printf("add(%s, %s) = %s%n", q1, q2, result);
    }

    public static void main(String[] args) {
        System.out.println("=== UC8: Refactored Design ===");
        System.out.println(LengthUnit.FEET.convertToBaseUnit(12.0));
        System.out.println(LengthUnit.INCHES.convertToBaseUnit(12.0));
        demonstrateLengthConversion(
                new QuantityLength(1.0, LengthUnit.FEET), LengthUnit.INCHES);
        demonstrateLengthAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES));
        // UC9 demos
        System.out.println("\n=== UC9: Weight Measurements ===");
        QuantityWeight kg1 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight g1000 = new QuantityWeight(1000.0, WeightUnit.GRAM);
        System.out.println("1kg == 1000g: " + kg1.equals(g1000));
        System.out.println("1kg to grams: " + kg1.convertTo(WeightUnit.GRAM));
        System.out.println("1kg + 1000g: " + QuantityWeight.add(kg1, g1000));
    }
}