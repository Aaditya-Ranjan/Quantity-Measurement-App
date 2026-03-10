package com.apps.quantitymeasurement;

/**
 * Quantity<U> - Generic immutable value object for any measurement category.
 * Replaces QuantityLength and QuantityWeight with a single, reusable class.
 * Delegates conversion logic to the unit (IMeasurable).
 *
 * @param <U> the unit type, must implement IMeasurable
 */
public class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    /**
     * @param value numeric measurement value
     * @param unit  measurement unit (must not be null, must be finite)
     * @throws NullPointerException     if unit is null
     * @throws IllegalArgumentException if value is NaN or infinite
     */
    public Quantity(double value, U unit) {
        if (unit == null) throw new NullPointerException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return this.value;
    }

    public U getUnit() {
        return this.unit;
    }

    /**
     * Converts value to base unit for comparison/arithmetic.
     */
    private double toBaseUnit() {
        return this.unit.convertToBaseUnit(this.value);
    }

    /**
     * Converts this quantity to the target unit.
     *
     * @param targetUnit unit to convert to
     * @return new Quantity in target unit
     */
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
        double baseValue = this.toBaseUnit();
        double converted = Math.round(
                targetUnit.convertFromBaseUnit(baseValue) * 1e10) / 1e10;
        return new Quantity<>(converted, targetUnit);
    }

    /**
     * Static conversion utility.
     */
    public static <U extends IMeasurable> double convert(
            double value, U sourceUnit, U targetUnit) {
        if (sourceUnit == null || targetUnit == null)
            throw new IllegalArgumentException("Units cannot be null");
        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Value must be finite");
        return sourceUnit.convertToBaseUnit(value) / targetUnit.getConversionFactor();
    }

    /**
     * Private utility method for addition - DRY principle.
     */
    private static <U extends IMeasurable> Quantity<U> addInTargetUnit(
            Quantity<U> first, Quantity<U> second, U targetUnit) {
        if (first == null || second == null)
            throw new IllegalArgumentException("Operands cannot be null");
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
        double sumInBase = first.toBaseUnit() + second.toBaseUnit();
        double result = Math.round(
                targetUnit.convertFromBaseUnit(sumInBase) * 1e10) / 1e10;
        return new Quantity<>(result, targetUnit);
    }

    /**
     * UC6 equivalent: result in unit of first operand.
     */
    public static <U extends IMeasurable> Quantity<U> add(
            Quantity<U> first, Quantity<U> second) {
        return addInTargetUnit(first, second, first.unit);
    }

    /**
     * UC7 equivalent: result in explicit target unit.
     */
    public static <U extends IMeasurable> Quantity<U> add(
            Quantity<U> first, Quantity<U> second, U targetUnit) {
        return addInTargetUnit(first, second, targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Quantity<?> other = (Quantity<?>) obj;
        // Cross-category prevention
        if (this.unit.getClass() != other.unit.getClass()) return false;
        return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.toBaseUnit());
    }

    @Override
    public String toString() {
        return value + " " + unit.getUnitName();
    }

    /**
     * Validates that another quantity is non-null and in the same measurement category.
     */
    private void validateSameCategory(Quantity<?> other) {
        if (other == null)
            throw new IllegalArgumentException("Operand cannot be null");
        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException(
                    "Cannot operate on different measurement categories");
    }

    /**
     * Private utility method for subtraction - DRY principle.
     * Mirrors addInTargetUnit() pattern.
     */
    private static <U extends IMeasurable> Quantity<U> subtractInTargetUnit(
            Quantity<U> first, Quantity<U> second, U targetUnit) {
        if (first == null || second == null)
            throw new IllegalArgumentException("Operands cannot be null");
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
        if (first.unit.getClass() != second.unit.getClass())
            throw new IllegalArgumentException(
                    "Cannot subtract different measurement categories");
        double diffInBase = first.toBaseUnit() - second.toBaseUnit();
        double resultValue = Math.round(
                targetUnit.convertFromBaseUnit(diffInBase) * 1e10) / 1e10;
        return new Quantity<>(resultValue, targetUnit);
    }

    /**
     * UC12: Subtracts another quantity from this quantity.
     * Result is expressed in this quantity's unit (implicit target).
     *
     * @param other quantity to subtract (must be same measurement category)
     * @return new Quantity with the difference in this unit
     * @throws IllegalArgumentException if other is null or different category
     */
    public Quantity<U> subtract(Quantity<U> other) {
        return subtractInTargetUnit(this, other, this.unit);
    }

    /**
     * UC12: Subtracts another quantity from this quantity.
     * Result is expressed in specified target unit (explicit target).
     *
     * @param other      quantity to subtract (must be same measurement category)
     * @param targetUnit unit for the result
     * @return new Quantity with the difference in target unit
     * @throws IllegalArgumentException if other/targetUnit is null or different category
     */
    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        return subtractInTargetUnit(this, other, targetUnit);
    }

    /**
     * UC12: Divides this quantity by another, returning a dimensionless scalar ratio.
     * Both quantities are converted to base unit before division.
     *
     * @param other the divisor quantity (must be same measurement category, non-zero)
     * @return dimensionless double representing the ratio this / other
     * @throws IllegalArgumentException if other is null or different category
     * @throws ArithmeticException      if other represents a zero quantity
     */
    public double divide(Quantity<U> other) {
        if (other == null)
            throw new IllegalArgumentException("Divisor cannot be null");
        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException(
                    "Cannot divide different measurement categories");
        double divisorBase = other.toBaseUnit();
        if (divisorBase == 0.0)
            throw new ArithmeticException("Division by zero is not allowed");
        return this.toBaseUnit() / divisorBase;
    }
}