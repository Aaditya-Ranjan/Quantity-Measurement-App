package com.app.quantitymeasurement.model;

/**
 * OperationType - Enum for type-safe operation representation.
 * UC17: Used in service and controller to identify operation kind.
 */
public enum OperationType {
    ADD, SUBTRACT, DIVIDE, COMPARE, CONVERT, MULTIPLY;
}