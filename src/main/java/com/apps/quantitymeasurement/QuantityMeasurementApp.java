package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.controller.QuantityMeasurementController;
import com.apps.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.apps.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;
import com.apps.quantitymeasurement.service.QuantityMeasurementServiceImpl;

/**
 * QuantityMeasurementApp - Application Entry Point.
 * UC15: Simplified to initialization and delegation only (SRP).
 *
 * Design Patterns used:
 *  - Factory Pattern:   Creates controller, service, repository instances
 *  - Facade Pattern:    Controller hides service complexity
 *  - Singleton Pattern: CacheRepository has one instance
 *  - Dependency Injection: Service injected into controller
 */
public class QuantityMeasurementApp {

    public static void main(String[] args) {
        // Factory Pattern: Initialize all layers
        IQuantityMeasurementRepository repository =
                QuantityMeasurementCacheRepository.getInstance();   // Singleton

        IQuantityMeasurementService service =
                new QuantityMeasurementServiceImpl(repository);     // DI: repo → service

        QuantityMeasurementController controller =
                new QuantityMeasurementController(service);         // DI: service → controller

        // Facade Pattern: Single call orchestrates all demonstrations
        controller.runAllDemonstrations();
    }
}