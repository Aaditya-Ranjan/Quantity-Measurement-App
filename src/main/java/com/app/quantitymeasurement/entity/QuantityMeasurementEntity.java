package com.app.quantitymeasurement.entity;

import java.io.Serializable;

/**
 * QuantityMeasurementEntity - Persistence model for recording operation history.
 * Implements Serializable for disk-based storage.
 * UC15: Immutable-by-design; fields set only via constructors.
 * Note: Fields are NOT final to allow Java serialization compatibility.
 */
public class QuantityMeasurementEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private QuantityDTO operand1;
    private QuantityDTO operand2;   // null for single-operand operations
    private String operation;       // "COMPARE", "CONVERT", "ADD", "SUBTRACT", "DIVIDE"
    private String result;          // String representation of result
    private boolean hasError;
    private String errorMessage;
    private long timestamp;

    /**
     * Constructor for single-operand operations (e.g., CONVERT).
     */
    public QuantityMeasurementEntity(QuantityDTO operand1, String operation, String result) {
        this.operand1 = operand1;
        this.operation = operation;
        this.result = result;
        this.hasError = false;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor for binary operations (COMPARE, ADD, SUBTRACT, DIVIDE).
     */
    public QuantityMeasurementEntity(QuantityDTO operand1, QuantityDTO operand2,
                                     String operation, String result) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operation = operation;
        this.result = result;
        this.hasError = false;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor for error cases.
     */
    public QuantityMeasurementEntity(QuantityDTO operand1, QuantityDTO operand2,
                                     String operation, String errorMessage, boolean hasError) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operation = operation;
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    public QuantityDTO getOperand1()   { return operand1; }
    public QuantityDTO getOperand2()   { return operand2; }
    public String getOperation()       { return operation; }
    public String getResult()          { return result; }
    public boolean hasError()          { return hasError; }
    public String getErrorMessage()    { return errorMessage; }
    public long getTimestamp()         { return timestamp; }

    @Override
    public String toString() {
        if (hasError) {
            return String.format("[%s] %s ⊕ %s → ERROR: %s",
                    operation, operand1, operand2, errorMessage);
        }
        return String.format("[%s] %s ⊕ %s → %s",
                operation, operand1, operand2 != null ? operand2.toString() : "N/A", result);
    }
}