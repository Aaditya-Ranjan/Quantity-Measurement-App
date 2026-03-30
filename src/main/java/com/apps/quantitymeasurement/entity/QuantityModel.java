package com.apps.quantitymeasurement.entity;

import com.apps.quantitymeasurement.IMeasurable;

/**
 * QuantityModel<U> - Generic internal model for service layer operations.
 * Used within the service layer; never exposed to controller or external clients.
 * UC15: Separates internal processing model from external DTO.
 *
 * @param <U> unit type implementing IMeasurable
 */
public class QuantityModel<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public QuantityModel(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() { return value; }
    public U getUnit() { return unit; }

    @Override
    public String toString() {
        return value + " " + (unit != null ? unit.getUnitName() : "null");
    }
}