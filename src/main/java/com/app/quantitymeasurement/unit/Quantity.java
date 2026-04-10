package com.app.quantitymeasurement.unit;

import java.util.function.DoubleBinaryOperator;

/**
 * Quantity<U> - Generic immutable value object for any measurement category.
 * UC13: Arithmetic operations centralized via ArithmeticOperation enum + helpers.
 * Eliminates code duplication across add(), subtract(), divide() — enforces DRY.
 *
 * @param <U> the unit type, must implement IMeasurable
 */
public class Quantity<U extends IMeasurable> {

    // ===== ArithmeticOperation Enum (Lambda Style) =====

    /**
     * Enum encapsulating arithmetic operations via DoubleBinaryOperator lambdas.
     * Each constant holds its own compute logic — no switch/if-else needed.
     * Adding new operations (MULTIPLY, MODULO) requires only a new constant.
     */
    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0.0) throw new ArithmeticException("Division by zero is not allowed");
            return a / b;
        });

        private final DoubleBinaryOperator operator;

        ArithmeticOperation(DoubleBinaryOperator operator) {
            this.operator = operator;
        }

        /**
         * Executes the arithmetic operation on two base-unit values.
         */
        public double compute(double a, double b) {
            return this.operator.applyAsDouble(a, b);
        }
    }

    // ===== Fields =====

    private final double value;
    private final U unit;

    // ===== Constructor =====

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

    // ===== Getters =====

    public double getValue() {
        return this.value;
    }

    public U getUnit() {
        return this.unit;
    }

    // ===== Private Helpers =====

    /**
     * Converts this quantity's value to base unit.
     */
    private double toBaseUnit() {
        return this.unit.convertToBaseUnit(this.value);
    }

    /**
     * Rounds a double value to 10 decimal places for precision consistency.
     */
    private static double roundResult(double value) {
        return Math.round(value * 1e10) / 1e10;
    }

    /**
     * UC13: Centralized validation helper — Single Source of Truth.
     * Validates null operand, same measurement category, finiteness,
     * and optionally validates target unit for add/subtract operations.
     *
     * @param other              the other quantity operand
     * @param targetUnit         target unit (may be null if targetUnitRequired is false)
     * @param targetUnitRequired whether to validate the target unit
     * @throws IllegalArgumentException for any validation failure
     */
    private void validateArithmeticOperands(Quantity<U> other,
                                            U targetUnit,
                                            boolean targetUnitRequired) {
        if (other == null)
            throw new IllegalArgumentException("Operand cannot be null");
        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException(
                    "Cannot operate on different measurement categories");
        if (!Double.isFinite(other.value))
            throw new IllegalArgumentException("Operand value must be finite");
        if (targetUnitRequired && targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
    }

    /**
     * UC13: Core arithmetic helper — performs base-unit conversion and operation.
     * All arithmetic methods delegate here; validation must be called first.
     *
     * @param other     the other quantity operand
     * @param operation the arithmetic operation to perform
     * @return result in base unit (dimensionless for DIVIDE)
     */
    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
        // UC14: Validate that this unit supports the requested arithmetic operation
        this.unit.validateOperationSupport(operation.name());
        double thisBase = this.toBaseUnit();
        double otherBase = other.toBaseUnit();
        return operation.compute(thisBase, otherBase);
    }

    // ===== Conversion =====

    /**
     * Converts this quantity to the target unit.
     */
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
        double celsiusBase = this.unit.convertToBaseUnit(this.value);
        double converted = roundResult(targetUnit.convertFromBaseUnit(celsiusBase));
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

    // ===== Addition =====

    /**
     * Adds two quantities; result in first operand's unit (implicit).
     */
    public static <U extends IMeasurable> Quantity<U> add(
            Quantity<U> first, Quantity<U> second) {
        return add(first, second, first.unit);
    }

    /**
     * Adds two quantities; result in explicit target unit.
     */
    public static <U extends IMeasurable> Quantity<U> add(
            Quantity<U> first, Quantity<U> second, U targetUnit) {
        first.validateArithmeticOperands(second, targetUnit, true);
        double baseResult = first.performBaseArithmetic(second, ArithmeticOperation.ADD);
        return new Quantity<>(roundResult(targetUnit.convertFromBaseUnit(baseResult)), targetUnit);
    }

    // ===== Subtraction =====

    /**
     * Subtracts other from this; result in this quantity's unit (implicit).
     */
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    /**
     * Subtracts other from this; result in explicit target unit.
     */
    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        double baseResult = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
        return new Quantity<>(roundResult(targetUnit.convertFromBaseUnit(baseResult)), targetUnit);
    }

    // ===== Division =====

    /**
     * Divides this by other; returns dimensionless scalar ratio.
     * ArithmeticException thrown for division by zero.
     */
    public double divide(Quantity<U> other) {
        validateArithmeticOperands(other, null, false);
        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

    // ===== equals / hashCode / toString =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Quantity<?> other = (Quantity<?>) obj;
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
}