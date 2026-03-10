package com.apps.quantitymeasurement;

/**
 * QuantityMeasurementApp - Simplified orchestration class.
 * Single Responsibility: demonstration and orchestration only.
 * Generic methods handle all measurement categories.
 */
public class QuantityMeasurementApp {

    /**
     * Generic equality demonstration - works for any measurement category.
     */
    public static <U extends IMeasurable> void demonstrateEquality(
            Quantity<U> q1, Quantity<U> q2) {
        System.out.printf("equals(%s, %s) = %b%n", q1, q2, q1.equals(q2));
    }

    /**
     * Generic conversion demonstration - works for any measurement category.
     */
    public static <U extends IMeasurable> void demonstrateConversion(
            Quantity<U> quantity, U targetUnit) {
        Quantity<U> result = quantity.convertTo(targetUnit);
        System.out.printf("convert(%s → %s) = %s%n",
                quantity, targetUnit.getUnitName(), result);
    }

    /**
     * Generic addition demonstration - works for any measurement category.
     */
    public static <U extends IMeasurable> void demonstrateAddition(
            Quantity<U> q1, Quantity<U> q2) {
        Quantity<U> result = Quantity.add(q1, q2);
        System.out.printf("add(%s, %s) = %s%n", q1, q2, result);
    }

    /**
     * Generic addition with explicit target unit.
     */
    public static <U extends IMeasurable> void demonstrateAddition(
            Quantity<U> q1, Quantity<U> q2, U targetUnit) {
        Quantity<U> result = Quantity.add(q1, q2, targetUnit);
        System.out.printf("add(%s, %s, %s) = %s%n",
                q1, q2, targetUnit.getUnitName(), result);
    }

    public static void main(String[] args) {
        System.out.println("=== Length Operations ===");
        demonstrateEquality(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(12.0, LengthUnit.INCHES));
        demonstrateConversion(
                new Quantity<>(1.0, LengthUnit.FEET), LengthUnit.INCHES);
        demonstrateAddition(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(12.0, LengthUnit.INCHES),
                LengthUnit.FEET);

        System.out.println("\n=== Weight Operations ===");
        demonstrateEquality(
                new Quantity<>(1.0, WeightUnit.KILOGRAM),
                new Quantity<>(1000.0, WeightUnit.GRAM));
        demonstrateConversion(
                new Quantity<>(1.0, WeightUnit.KILOGRAM), WeightUnit.GRAM);
        demonstrateAddition(
                new Quantity<>(1.0, WeightUnit.KILOGRAM),
                new Quantity<>(1000.0, WeightUnit.GRAM),
                WeightUnit.KILOGRAM);

        System.out.println("\n=== Cross-Category Prevention ===");
        Quantity<LengthUnit> length = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        System.out.println("1 foot == 1 kg: " + length.equals(weight));

        System.out.println("\n=== UC11: Volume Measurements ===");
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> millilitres = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> gallon = new Quantity<>(1.0, VolumeUnit.GALLON);

        demonstrateEquality(litre, millilitres);
        demonstrateConversion(litre, VolumeUnit.MILLILITRE);
        demonstrateAddition(litre, millilitres, VolumeUnit.LITRE);
        demonstrateEquality(
                new Quantity<>(3.78541, VolumeUnit.LITRE),
                gallon);
    }
}