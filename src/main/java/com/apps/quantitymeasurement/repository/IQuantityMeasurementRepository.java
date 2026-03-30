package com.apps.quantitymeasurement.repository;

import com.apps.quantitymeasurement.entity.QuantityMeasurementEntity;
import java.util.List;

/**
 * IQuantityMeasurementRepository - Data access contract.
 * UC15: Interface Segregation Principle — allows swapping in-memory vs. database.
 */
public interface IQuantityMeasurementRepository {
    void save(QuantityMeasurementEntity entity);
    List<QuantityMeasurementEntity> getAllMeasurements();
    void clearAll();
}