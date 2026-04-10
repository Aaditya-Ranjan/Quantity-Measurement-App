package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.*;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.unit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * QuantityMeasurementServiceImpl - Spring @Service with JPA repository.
 * UC17: Uses Spring DI (@Autowired), returns QuantityMeasurementDTO.
 */
@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger =
            LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    @Autowired
    private QuantityMeasurementRepository repository;

    // ===== Core Operations =====

    @Override
    public QuantityMeasurementDTO compareEquality(QuantityDTO qty1DTO, QuantityDTO qty2DTO) {
        QuantityMeasurementEntity entity = buildEntity(qty1DTO, qty2DTO, "compare");
        try {
            validateSameType(qty1DTO.getMeasurementType(), qty2DTO.getMeasurementType());
            Quantity<?> q1 = convertDtoToQuantity(qty1DTO);
            Quantity<?> q2 = convertDtoToQuantity(qty2DTO);
            boolean result = q1.equals(q2);
            entity.setResultString(String.valueOf(result));
            logger.debug("Compare: {} == {} → {}", qty1DTO, qty2DTO, result);
        } catch (Exception e) {
            setError(entity, e.getMessage());
            repository.save(entity);
            throw new QuantityMeasurementException("compare Error: " + e.getMessage(), e);
        }
        repository.save(entity);
        return QuantityMeasurementDTO.fromEntity(entity);
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO quantityDTO, QuantityDTO targetUnitDTO) {
        QuantityMeasurementEntity entity = buildEntity(quantityDTO, targetUnitDTO, "convert");
        try {
            validateSameType(quantityDTO.getMeasurementType(),
                    targetUnitDTO.getMeasurementType());
            @SuppressWarnings("unchecked")
            Quantity<IMeasurable> q = (Quantity<IMeasurable>) convertDtoToQuantity(quantityDTO);
            IMeasurable targetUnit = resolveUnit(
                    targetUnitDTO.getMeasurementType(), targetUnitDTO.getUnit());
            @SuppressWarnings("unchecked")
            Quantity<IMeasurable> result = q.convertTo(targetUnit);
            entity.setResultValue(result.getValue());
            logger.debug("Convert: {} → {} = {}", quantityDTO, targetUnitDTO.getUnit(),
                    result.getValue());
        } catch (Exception e) {
            setError(entity, e.getMessage());
            repository.save(entity);
            throw new QuantityMeasurementException("convert Error: " + e.getMessage(), e);
        }
        repository.save(entity);
        return QuantityMeasurementDTO.fromEntity(entity);
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO qty1DTO, QuantityDTO qty2DTO) {
        QuantityMeasurementEntity entity = buildEntity(qty1DTO, qty2DTO, "add");
        try {
            validateSameType(qty1DTO.getMeasurementType(), qty2DTO.getMeasurementType());
            @SuppressWarnings({"unchecked","rawtypes"})
            Quantity result = Quantity.add(
                    convertDtoToQuantity(qty1DTO), convertDtoToQuantity(qty2DTO));
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(result.getUnit().getMeasurementType());
        } catch (UnsupportedOperationException e) {
            setError(entity, e.getMessage());
            repository.save(entity);
            throw new QuantityMeasurementException("add Error: " + e.getMessage(), e);
        } catch (Exception e) {
            setError(entity, e.getMessage());
            repository.save(entity);
            throw new QuantityMeasurementException("add Error: " + e.getMessage(), e);
        }
        repository.save(entity);
        return QuantityMeasurementDTO.fromEntity(entity);
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO qty1DTO, QuantityDTO qty2DTO) {
        QuantityMeasurementEntity entity = buildEntity(qty1DTO, qty2DTO, "subtract");
        try {
            validateSameType(qty1DTO.getMeasurementType(), qty2DTO.getMeasurementType());
            @SuppressWarnings({"unchecked","rawtypes"})
            Quantity result = convertDtoToQuantity(qty1DTO)
                    .subtract(convertDtoToQuantity(qty2DTO));
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(result.getUnit().getMeasurementType());
        } catch (Exception e) {
            setError(entity, e.getMessage());
            repository.save(entity);
            throw new QuantityMeasurementException("subtract Error: " + e.getMessage(), e);
        }
        repository.save(entity);
        return QuantityMeasurementDTO.fromEntity(entity);
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO qty1DTO, QuantityDTO qty2DTO) {
        QuantityMeasurementEntity entity = buildEntity(qty1DTO, qty2DTO, "divide");
        try {
            validateSameType(qty1DTO.getMeasurementType(), qty2DTO.getMeasurementType());
            double result = convertDtoToQuantity(qty1DTO)
                    .divide(convertDtoToQuantity(qty2DTO));
            entity.setResultValue(result);
        } catch (ArithmeticException | UnsupportedOperationException e) {
            setError(entity, e.getMessage());
            repository.save(entity);
            throw new QuantityMeasurementException(e.getMessage(), e);
        } catch (Exception e) {
            setError(entity, e.getMessage());
            repository.save(entity);
            throw new QuantityMeasurementException("divide Error: " + e.getMessage(), e);
        }
        repository.save(entity);
        return QuantityMeasurementDTO.fromEntity(entity);
    }

    // ===== History / Query Methods =====

    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByOperation(operation.toUpperCase()));
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByThisMeasurementType(measurementType));
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation.toUpperCase());
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByIsErrorTrue());
    }

    // ===== Private Helpers =====

    private void validateSameType(String type1, String type2) {
        if (!type1.equals(type2))
            throw new QuantityMeasurementException(
                    "Cannot perform arithmetic between different measurement categories: "
                            + type1 + " and " + type2);
    }

    private void setError(QuantityMeasurementEntity entity, String message) {
        entity.setError(true);
        entity.setErrorMessage(message);
    }

    private QuantityMeasurementEntity buildEntity(
            QuantityDTO q1, QuantityDTO q2, String operation) {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.setThisValue(q1.getValue() != null ? q1.getValue() : 0.0);
        e.setThisUnit(q1.getUnit());
        e.setThisMeasurementType(q1.getMeasurementType());
        if (q2 != null) {
            e.setThatValue(q2.getValue() != null ? q2.getValue() : 0.0);
            e.setThatUnit(q2.getUnit());
            e.setThatMeasurementType(q2.getMeasurementType());
        }
        e.setOperation(operation);
        e.setError(false);
        return e;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private Quantity convertDtoToQuantity(QuantityDTO dto) {
        IMeasurable unit = resolveUnit(dto.getMeasurementType(), dto.getUnit());
        return new Quantity<>(dto.getValue(), unit);
    }

    private IMeasurable resolveUnit(String measurementType, String unitName) {
        try {
            switch (measurementType) {
                case "LengthUnit":      return LengthUnit.valueOf(unitName);
                case "WeightUnit":      return WeightUnit.valueOf(unitName);
                case "VolumeUnit":      return VolumeUnit.valueOf(unitName);
                case "TemperatureUnit": return TemperatureUnit.valueOf(unitName);
                default: throw new QuantityMeasurementException(
                        "Unknown measurement type: " + measurementType);
            }
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException(
                    "Invalid unit name: " + unitName +
                            " for type: " + measurementType);
        }
    }
}