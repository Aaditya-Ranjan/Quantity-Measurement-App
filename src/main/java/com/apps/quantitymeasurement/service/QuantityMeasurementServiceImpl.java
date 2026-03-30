package com.apps.quantitymeasurement.service;

import com.apps.quantitymeasurement.*;
import com.apps.quantitymeasurement.entity.QuantityDTO;
import com.apps.quantitymeasurement.exception.QuantityMeasurementException;
import com.apps.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.apps.quantitymeasurement.entity.QuantityMeasurementEntity;

/**
 * QuantityMeasurementServiceImpl - Core business logic layer.
 * UC15: Maps QuantityDTO ↔ Quantity<U>, executes operations, persists results.
 * Dependency Injection: repository injected via constructor.
 * SRP: Only responsible for quantity measurement business operations.
 */
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private final IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        if (repository == null)
            throw new IllegalArgumentException("Repository cannot be null");
        this.repository = repository;
    }

    // ===== Public Service Methods =====

    @Override
    public boolean compareEquality(QuantityDTO qty1DTO, QuantityDTO qty2DTO) {
        validateNotNull(qty1DTO, "First quantity");
        validateNotNull(qty2DTO, "Second quantity");
        validateSameMeasurementType(qty1DTO.getUnit(), qty2DTO.getUnit());
        try {
            Quantity<?> q1 = toQuantity(qty1DTO);
            Quantity<?> q2 = toQuantity(qty2DTO);
            boolean result = q1.equals(q2);
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "COMPARE", String.valueOf(result)));
            return result;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "COMPARE", e.getMessage(), true));
            throw new QuantityMeasurementException("Comparison failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public QuantityDTO convert(QuantityDTO quantityDTO, QuantityDTO.IMeasurableUnit targetUnitDTO) {
        validateNotNull(quantityDTO, "Quantity");
        validateNotNull(targetUnitDTO, "Target unit");
        validateSameMeasurementType(quantityDTO.getUnit(), targetUnitDTO);
        try {
            Quantity q = toRawQuantity(quantityDTO);
            IMeasurable targetUnit = resolveUnit(
                    targetUnitDTO.getMeasurementType(), targetUnitDTO.getUnitName());
            Quantity result = q.convertTo(targetUnit);
            QuantityDTO resultDTO = toDTO(result);
            repository.save(new QuantityMeasurementEntity(
                    quantityDTO, "CONVERT", resultDTO.toString()));
            return resultDTO;
        } catch (QuantityMeasurementException e) {
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(
                    quantityDTO, null, "CONVERT", e.getMessage(), true));
            throw new QuantityMeasurementException("Conversion failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public QuantityDTO add(QuantityDTO qty1DTO, QuantityDTO qty2DTO,
                           QuantityDTO.IMeasurableUnit targetUnitDTO) {
        validateNotNull(qty1DTO, "First quantity");
        validateNotNull(qty2DTO, "Second quantity");
        validateNotNull(targetUnitDTO, "Target unit");
        validateSameMeasurementType(qty1DTO.getUnit(), qty2DTO.getUnit());
        validateSameMeasurementType(qty1DTO.getUnit(), targetUnitDTO);
        try {
            Quantity q1 = toRawQuantity(qty1DTO);
            Quantity q2 = toRawQuantity(qty2DTO);
            IMeasurable targetUnit = resolveUnit(
                    targetUnitDTO.getMeasurementType(), targetUnitDTO.getUnitName());
            Quantity result = Quantity.add(q1, q2, targetUnit);
            QuantityDTO resultDTO = toDTO(result);
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "ADD", resultDTO.toString()));
            return resultDTO;
        } catch (UnsupportedOperationException e) {
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "ADD", e.getMessage(), true));
            throw new QuantityMeasurementException("Addition not supported: " + e.getMessage(), e);
        } catch (QuantityMeasurementException e) {
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "ADD", e.getMessage(), true));
            throw new QuantityMeasurementException("Addition failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public QuantityDTO subtract(QuantityDTO qty1DTO, QuantityDTO qty2DTO,
                                QuantityDTO.IMeasurableUnit targetUnitDTO) {
        validateNotNull(qty1DTO, "First quantity");
        validateNotNull(qty2DTO, "Second quantity");
        validateNotNull(targetUnitDTO, "Target unit");
        validateSameMeasurementType(qty1DTO.getUnit(), qty2DTO.getUnit());
        validateSameMeasurementType(qty1DTO.getUnit(), targetUnitDTO);
        try {
            Quantity q1 = toRawQuantity(qty1DTO);
            Quantity q2 = toRawQuantity(qty2DTO);
            IMeasurable targetUnit = resolveUnit(
                    targetUnitDTO.getMeasurementType(), targetUnitDTO.getUnitName());
            Quantity result = q1.subtract(q2, targetUnit);
            QuantityDTO resultDTO = toDTO(result);
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "SUBTRACT", resultDTO.toString()));
            return resultDTO;
        } catch (UnsupportedOperationException e) {
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "SUBTRACT", e.getMessage(), true));
            throw new QuantityMeasurementException("Subtraction not supported: " + e.getMessage(), e);
        } catch (QuantityMeasurementException e) {
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "SUBTRACT", e.getMessage(), true));
            throw new QuantityMeasurementException("Subtraction failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public double divide(QuantityDTO qty1DTO, QuantityDTO qty2DTO) {
        validateNotNull(qty1DTO, "First quantity");
        validateNotNull(qty2DTO, "Second quantity");
        validateSameMeasurementType(qty1DTO.getUnit(), qty2DTO.getUnit());
        try {
            Quantity q1 = toRawQuantity(qty1DTO);
            Quantity q2 = toRawQuantity(qty2DTO);
            double result = q1.divide(q2);
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "DIVIDE", String.valueOf(result)));
            return result;
        } catch (ArithmeticException | UnsupportedOperationException e) {
            repository.save(new QuantityMeasurementEntity(
                    qty1DTO, qty2DTO, "DIVIDE", e.getMessage(), true));
            throw new QuantityMeasurementException("Division failed: " + e.getMessage(), e);
        }
    }

    // ===== Private Helper Methods =====

    private void validateNotNull(Object obj, String name) {
        if (obj == null)
            throw new QuantityMeasurementException(name + " cannot be null");
    }

    private void validateSameMeasurementType(QuantityDTO.IMeasurableUnit u1,
                                             QuantityDTO.IMeasurableUnit u2) {
        if (!u1.getMeasurementType().equals(u2.getMeasurementType()))
            throw new QuantityMeasurementException(
                    "Measurement type mismatch: " +
                            u1.getMeasurementType() + " vs " + u2.getMeasurementType());
    }

    /**
     * Maps QuantityDTO unit strings to actual IMeasurable enum constants.
     */
    private IMeasurable resolveUnit(String measurementType, String unitName) {
        try {
            switch (measurementType.toUpperCase()) {
                case "LENGTH":      return LengthUnit.valueOf(unitName);
                case "WEIGHT":      return WeightUnit.valueOf(unitName);
                case "VOLUME":      return VolumeUnit.valueOf(unitName);
                case "TEMPERATURE": return TemperatureUnit.valueOf(unitName);
                default: throw new QuantityMeasurementException(
                        "Unknown measurement type: " + measurementType);
            }
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException(
                    "Unknown unit '" + unitName + "' for type: " + measurementType);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Quantity<?> toQuantity(QuantityDTO dto) {
        IMeasurable unit = resolveUnit(
                dto.getUnit().getMeasurementType(), dto.getUnit().getUnitName());
        return new Quantity<>(dto.getValue(), unit);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Quantity toRawQuantity(QuantityDTO dto) {
        IMeasurable unit = resolveUnit(
                dto.getUnit().getMeasurementType(), dto.getUnit().getUnitName());
        return new Quantity<>(dto.getValue(), unit);
    }

    /**
     * Maps Quantity<U> result back to QuantityDTO for layer communication.
     */
    private QuantityDTO toDTO(Quantity<?> quantity) {
        IMeasurable unit = quantity.getUnit();
        QuantityDTO.IMeasurableUnit dtoUnit = resolveDTOUnit(unit);
        return new QuantityDTO(quantity.getValue(), dtoUnit);
    }

    private QuantityDTO.IMeasurableUnit resolveDTOUnit(IMeasurable unit) {
        String type = unit.getMeasurementType();
        String name = unit.getUnitName();
        switch (type) {
            case "LENGTH":      return QuantityDTO.LengthUnit.valueOf(name);
            case "WEIGHT":      return QuantityDTO.WeightUnit.valueOf(name);
            case "VOLUME":      return QuantityDTO.VolumeUnit.valueOf(name);
            case "TEMPERATURE": return QuantityDTO.TemperatureUnit.valueOf(name);
            default: throw new QuantityMeasurementException("Unknown type: " + type);
        }
    }
}