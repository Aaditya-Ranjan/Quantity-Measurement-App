package com.app.quantitymeasurement;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.util.ApplicationConfig;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QuantityMeasurementApp - Application entry point.
 * UC16: Factory Pattern selects repository (cache vs. database) from config.
 * Adds proper SLF4J logging and resource cleanup on shutdown.
 */
public class QuantityMeasurementApp {

    private static final Logger logger =
            LoggerFactory.getLogger(QuantityMeasurementApp.class);

    private final IQuantityMeasurementRepository repository;
    private final IQuantityMeasurementService service;
    private final QuantityMeasurementController controller;

    public QuantityMeasurementApp() {
        ApplicationConfig config = ApplicationConfig.getInstance();
        String repoType = config.getRepositoryType();
        logger.info("Initializing with repository type: {}", repoType);

        // Factory Pattern: choose repository based on config
        if ("database".equalsIgnoreCase(repoType)) {
            this.repository = new QuantityMeasurementDatabaseRepository(
                    ConnectionPool.getInstance());
            logger.info("Using DATABASE repository. Pool: {}",
                    repository.getPoolStatistics());
        } else {
            this.repository = QuantityMeasurementCacheRepository.getInstance();
            logger.info("Using CACHE repository");
        }

        this.service    = new QuantityMeasurementServiceImpl(repository);
        this.controller = new QuantityMeasurementController(service);
        logger.info("QuantityMeasurementApp initialized successfully");
    }

    public void run() {
        controller.runAllDemonstrations();

        logger.info("Total measurements stored: {}", repository.getTotalCount());
        repository.getAllMeasurements().forEach(e ->
                logger.debug("  -> {}", e));
    }

    public void deleteAllMeasurements() {
        repository.clearAll();
        logger.info("All measurements deleted from repository");
    }

    public void closeResources() {
        repository.releaseResources();
        logger.info("Application resources released");
    }

    public static void main(String[] args) {
        logger.info("=== Quantity Measurement App Starting ===");
        QuantityMeasurementApp app = new QuantityMeasurementApp();
        try {
            app.run();
        } finally {
            app.deleteAllMeasurements();
            app.closeResources();
            logger.info("=== Quantity Measurement App Shutdown Complete ===");
        }
    }
}