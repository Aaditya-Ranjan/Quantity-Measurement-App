package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import java.util.List;

/**
 * IQuantityMeasurementRepository - Data access contract.
 * UC16: Extended with query methods and resource management defaults.
 * Interface Segregation Principle — swap cache vs. database without service changes.
 */
public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> getAllMeasurements();

    List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation);

    List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType);

    int getTotalCount();

    void clearAll();

    // ===== Default Methods (optional override) =====

    default String getPoolStatistics() {
        return "No connection pool for this repository";
    }

    default void releaseResources() {
        // no-op by default
    }
}