package com.apps.quantitymeasurement.controller;

import com.apps.quantitymeasurement.entity.QuantityDTO;
import com.apps.quantitymeasurement.exception.QuantityMeasurementException;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;

/**
 * QuantityMeasurementController - Orchestration and presentation layer.
 * UC15: Facade Pattern — simplified interface hiding service complexity.
 * Dependency Injection: IQuantityMeasurementService injected via constructor.
 * REST-Ready: perform* methods map to future HTTP endpoints.
 */
public class QuantityMeasurementController {

    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        if (service == null)
            throw new IllegalArgumentException("Service cannot be null");
        this.service = service;
    }

    // ===== API Methods (REST-Ready) =====

    /** POST /api/quantity/compare */
    public void performEquality(QuantityDTO qty1, QuantityDTO qty2) {
        try {
            boolean result = service.compareEquality(qty1, qty2);
            System.out.printf("equals(%s, %s) = %b%n", qty1, qty2, result);
        } catch (QuantityMeasurementException e) {
            System.out.println("ERROR [compare]: " + e.getMessage());
        }
    }

    /** POST /api/quantity/convert */
    public void performConversion(QuantityDTO quantity,
                                  QuantityDTO.IMeasurableUnit targetUnit) {
        try {
            QuantityDTO result = service.convert(quantity, targetUnit);
            System.out.printf("convert(%s → %s) = %s%n",
                    quantity, targetUnit.getUnitName(), result);
        } catch (QuantityMeasurementException e) {
            System.out.println("ERROR [convert]: " + e.getMessage());
        }
    }

    /** POST /api/quantity/add */
    public void performAddition(QuantityDTO qty1, QuantityDTO qty2,
                                QuantityDTO.IMeasurableUnit targetUnit) {
        try {
            QuantityDTO result = service.add(qty1, qty2, targetUnit);
            System.out.printf("add(%s, %s) → %s = %s%n",
                    qty1, qty2, targetUnit.getUnitName(), result);
        } catch (QuantityMeasurementException e) {
            System.out.println("ERROR [add]: " + e.getMessage());
        }
    }

    /** POST /api/quantity/subtract */
    public void performSubtraction(QuantityDTO qty1, QuantityDTO qty2,
                                   QuantityDTO.IMeasurableUnit targetUnit) {
        try {
            QuantityDTO result = service.subtract(qty1, qty2, targetUnit);
            System.out.printf("subtract(%s, %s) → %s = %s%n",
                    qty1, qty2, targetUnit.getUnitName(), result);
        } catch (QuantityMeasurementException e) {
            System.out.println("ERROR [subtract]: " + e.getMessage());
        }
    }

    /** POST /api/quantity/divide */
    public void performDivision(QuantityDTO qty1, QuantityDTO qty2) {
        try {
            double result = service.divide(qty1, qty2);
            System.out.printf("divide(%s, %s) = %.6f%n", qty1, qty2, result);
        } catch (QuantityMeasurementException e) {
            System.out.println("ERROR [divide]: " + e.getMessage());
        }
    }

    // ===== Demonstration Runner =====

    public void runAllDemonstrations() {
        System.out.println("=== UC15: N-Tier Architecture Demo ===");

        System.out.println("\n--- Length Operations ---");
        performEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        performConversion(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                QuantityDTO.LengthUnit.INCHES);
        performAddition(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES),
                QuantityDTO.LengthUnit.FEET);
        performSubtraction(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(6.0, QuantityDTO.LengthUnit.INCHES),
                QuantityDTO.LengthUnit.FEET);
        performDivision(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET));

        System.out.println("\n--- Weight Operations ---");
        performEquality(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM));
        performConversion(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                QuantityDTO.WeightUnit.GRAM);
        performAddition(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(500.0, QuantityDTO.WeightUnit.GRAM),
                QuantityDTO.WeightUnit.KILOGRAM);

        System.out.println("\n--- Volume Operations ---");
        performEquality(
                new QuantityDTO(1.0, QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE));
        performConversion(
                new QuantityDTO(1.0, QuantityDTO.VolumeUnit.LITRE),
                QuantityDTO.VolumeUnit.MILLILITRE);

        System.out.println("\n--- Temperature Operations ---");
        performEquality(
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(32.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));
        performConversion(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                QuantityDTO.TemperatureUnit.FAHRENHEIT);

        System.out.println("\n--- Unsupported Temperature Addition ---");
        performAddition(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS),
                QuantityDTO.TemperatureUnit.CELSIUS);

        System.out.println("\n--- Cross-Category Prevention ---");
        performEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM));
    }
}