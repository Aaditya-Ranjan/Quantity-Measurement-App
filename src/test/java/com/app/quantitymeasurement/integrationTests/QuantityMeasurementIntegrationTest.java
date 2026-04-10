package com.app.quantitymeasurement.integrationTests;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test — full stack: Controller → Service → Repository → H2 DB.
 */
class QuantityMeasurementIntegrationTest {

    private static IQuantityMeasurementRepository repo;
    private IQuantityMeasurementService service;
    private QuantityMeasurementController controller;

    @BeforeAll
    static void initPool() {
        System.setProperty("db.url",
                "jdbc:h2:mem:integrationdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        System.setProperty("db.driver", "org.h2.Driver");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        System.setProperty("db.pool.size", "3");
        repo = new QuantityMeasurementDatabaseRepository(ConnectionPool.getInstance());
    }

    @BeforeEach
    void setup() {
        repo.clearAll();
        service = new QuantityMeasurementServiceImpl(repo);
        controller = new QuantityMeasurementController(service);
    }

    @Test
    void testEndToEnd_LengthComparison_PersistsToDatabase() {
        controller.performEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1, repo.getTotalCount());
        List<QuantityMeasurementEntity> ops = repo.getMeasurementsByOperation("COMPARE");
        assertEquals(1, ops.size());
        assertEquals("true", ops.get(0).getResult());
    }

    @Test
    void testEndToEnd_LengthConversion_PersistsToDatabase() {
        controller.performConversion(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                QuantityDTO.LengthUnit.INCHES);
        assertEquals(1, repo.getTotalCount());
        List<QuantityMeasurementEntity> ops = repo.getMeasurementsByOperation("CONVERT");
        assertEquals(1, ops.size());
    }

    @Test
    void testEndToEnd_WeightAddition_PersistsToDatabase() {
        controller.performAddition(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                QuantityDTO.WeightUnit.KILOGRAM);
        assertEquals(1, repo.getTotalCount());
        assertEquals("ADD", repo.getAllMeasurements().get(0).getOperation());
    }

    @Test
    void testEndToEnd_TemperatureAddition_ErrorSavedToDatabase() {
        controller.performAddition(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS),
                QuantityDTO.TemperatureUnit.CELSIUS);
        assertEquals(1, repo.getTotalCount());
        assertTrue(repo.getAllMeasurements().get(0).hasError());
    }

    @Test
    void testEndToEnd_MultipleOperations_AllPersisted() {
        controller.performEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performConversion(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                QuantityDTO.WeightUnit.GRAM);
        controller.performDivision(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET));
        assertEquals(3, repo.getTotalCount());
    }

    @Test
    void testEndToEnd_QueryByMeasurementType() {
        controller.performEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performEquality(
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM));
        List<QuantityMeasurementEntity> lengthOps = repo.getMeasurementsByType("LENGTH");
        assertEquals(1, lengthOps.size());
    }

    @Test
    void testEndToEnd_DataIsolation_CleanBetweenTests() {
        assertEquals(0, repo.getTotalCount());
    }
}