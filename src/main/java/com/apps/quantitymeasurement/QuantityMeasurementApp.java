package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

    public static class Feet {
        private final double value;

        public Feet(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            Feet other = (Feet) obj;
            return Double.compare(this.value, other.value) == 0;
        }
    }

    public static class Inches {
        private final double value;

        public Inches(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            Inches other = (Inches) obj;
            return Double.compare(this.value, other.value) == 0;
        }
    }

    // Static method for Feet equality
    public static boolean areFeetEqual(double value1, double value2) {
        Feet feet1 = new Feet(value1);
        Feet feet2 = new Feet(value2);
        return feet1.equals(feet2);
    }

    // Static method for Inches equality
    public static boolean areInchesEqual(double value1, double value2) {
        Inches inch1 = new Inches(value1);
        Inches inch2 = new Inches(value2);
        return inch1.equals(inch2);
    }

    public static void main(String[] args) {
        System.out.println("Feet Equal (1.0, 1.0): " + areFeetEqual(1.0, 1.0));
        System.out.println("Inches Equal (1.0, 1.0): " + areInchesEqual(1.0, 1.0));
    }
}