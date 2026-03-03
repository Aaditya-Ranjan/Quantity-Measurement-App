package com.apps.quantitymeasurement;

/**
 * QuantityWeight - Immutable value object for weight measurements.
 * Mirrors QuantityLength design for consistency across measurement categories.
 * Delegates unit conversion to WeightUnit enum (SRP).
 */
public class QuantityWeight {

    private final double value;
    private final WeightUnit unit;

    /**
     * @param value numeric measurement value
     * @param unit  WeightUnit (must not be null)
     * @throws NullPointerException     if unit is null
     * @throws IllegalArgumentException if value is NaN or infinite
     */
    public QuantityWeight(double value, WeightUnit unit) {
        if (unit == null) throw new NullPointerException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return this.value;
    }

    public WeightUnit getUnit() {
        return this.unit;
    }

    /**
     * Converts value to base unit (KILOGRAM).
     */
    private double toBaseUnit() {
        return this.unit.convertToBaseUnit(this.value);
    }

    /**
     * Converts this weight to target unit.
     */
    public QuantityWeight convertTo(WeightUnit targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
        double baseValue = this.toBaseUnit();
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        return new QuantityWeight(
                Math.round(convertedValue * 1e10) / 1e10,
                targetUnit);
    }

    /**
     * Static conversion method.
     */
    public static double convert(double value, WeightUnit sourceUnit, WeightUnit targetUnit) {
        if (sourceUnit == null || targetUnit == null)
            throw new IllegalArgumentException("Units cannot be null");
        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Value must be finite");
        return sourceUnit.convertToBaseUnit(value) / targetUnit.getConversionFactor();
    }

    /**
     * Private utility method - DRY principle for addition.
     */
    private static QuantityWeight addInTargetUnit(
            QuantityWeight first, QuantityWeight second, WeightUnit targetUnit) {
        if (first == null || second == null)
            throw new IllegalArgumentException("Operands cannot be null");
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
        double sumInBase = first.toBaseUnit() + second.toBaseUnit();
        double resultValue = Math.round(
                targetUnit.convertFromBaseUnit(sumInBase) * 1e10) / 1e10;
        return new QuantityWeight(resultValue, targetUnit);
    }

    /**
     * UC6 equivalent: result in unit of first operand.
     */
    public static QuantityWeight add(QuantityWeight first, QuantityWeight second) {
        return addInTargetUnit(first, second, first.unit);
    }

    /**
     * UC7 equivalent: result in explicit target unit.
     */
    public static QuantityWeight add(
            QuantityWeight first, QuantityWeight second, WeightUnit targetUnit) {
        return addInTargetUnit(first, second, targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        QuantityWeight other = (QuantityWeight) obj;
        return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.toBaseUnit());
    }

    @Override
    public String toString() {
        return value + " " + unit.name();
    }
}