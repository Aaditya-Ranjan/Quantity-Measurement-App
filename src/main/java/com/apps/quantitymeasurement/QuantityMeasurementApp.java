package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

    public enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.393701 / 12.0);  // 1cm = 0.393701 inches = 0.393701/12 feet

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() {
            return conversionFactor;
        }
    }

    public static class QuantityLength {

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) throw new NullPointerException("Unit cannot be null");
            this.value = value;
            this.unit = unit;
        }

        private double toBaseUnit() {
            return this.value * this.unit.getConversionFactor();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            QuantityLength other = (QuantityLength) obj;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }
    }

    public static void main(String[] args) {
        QuantityLength oneYard = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength threeFeet = new QuantityLength(3.0, LengthUnit.FEET);
        QuantityLength thirtySixInches = new QuantityLength(36.0, LengthUnit.INCHES);
        QuantityLength oneCm = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        QuantityLength cmInInches = new QuantityLength(0.393701, LengthUnit.INCHES);

        System.out.println("1 yard == 3 feet: " + oneYard.equals(threeFeet));
        System.out.println("1 yard == 36 inches: " + oneYard.equals(thirtySixInches));
        System.out.println("1 cm == 0.393701 inches: " + oneCm.equals(cmInInches));
    }
}