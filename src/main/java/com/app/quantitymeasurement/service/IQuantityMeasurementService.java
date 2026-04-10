package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.entity.QuantityDTO;

/**
 * IQuantityMeasurementService - Business logic contract.
 * UC15: Interface Segregation — controller depends on abstraction, not implementation.
 * All methods accept and return QuantityDTO (external contract).
 */
public interface IQuantityMeasurementService {

    boolean compareEquality(QuantityDTO quantity1, QuantityDTO quantity2);

    QuantityDTO convert(QuantityDTO quantity, QuantityDTO.IMeasurableUnit targetUnit);

    QuantityDTO add(QuantityDTO quantity1, QuantityDTO quantity2,
                    QuantityDTO.IMeasurableUnit targetUnit);

    QuantityDTO subtract(QuantityDTO quantity1, QuantityDTO quantity2,
                         QuantityDTO.IMeasurableUnit targetUnit);

    double divide(QuantityDTO quantity1, QuantityDTO quantity2);
}