package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import java.util.List;

/**
 * IQuantityMeasurementService - Business logic contract.
 * UC17: Methods now return QuantityMeasurementDTO for structured API responses.
 */
public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compareEquality(QuantityDTO qty1, QuantityDTO qty2);

    QuantityMeasurementDTO convert(QuantityDTO quantity, QuantityDTO targetUnit);

    QuantityMeasurementDTO add(QuantityDTO qty1, QuantityDTO qty2);

    QuantityMeasurementDTO subtract(QuantityDTO qty1, QuantityDTO qty2);

    QuantityMeasurementDTO divide(QuantityDTO qty1, QuantityDTO qty2);

    List<QuantityMeasurementDTO> getHistoryByOperation(String operation);

    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType);

    long getOperationCount(String operation);

    List<QuantityMeasurementDTO> getErrorHistory();
}