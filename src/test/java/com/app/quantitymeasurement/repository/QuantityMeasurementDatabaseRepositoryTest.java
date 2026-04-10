package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.util.ConnectionPool;
import com.app.quantitymeasurement.util.ApplicationConfig;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuantityMeasurementDatabaseRepository using H2 in-memory DB.
 */
class QuantityMeasurementDatabaseRepositoryTest {

    private static ConnectionPool pool;
    private QuantityMeasurementDatabaseRepository repo;

    @BeforeAll
    static void setupPool() {
        System.setProperty("db.url",
                "jdbc:h2:mem:testdb_repo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        System.setProperty("db.driver", "org.h2.Driver");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        System.setProperty("db.pool.size", "3");
        pool = ConnectionPool.getInstance();
    }

    @BeforeEach
    void setup() {
        repo = new QuantityMeasurementDatabaseRepository(pool);
        repo.clearAll();
    }

    private QuantityDTO feet(double v) {
        return new QuantityDTO(v, QuantityDTO.LengthUnit.FEET);
    }

    private QuantityDTO kg(double v) {
        return new QuantityDTO(v, QuantityDTO.WeightUnit.KILOGRAM);
    }

    @Test
    void testSaveEntity_And_RetrieveAll() {
        repo.save(new QuantityMeasurementEntity(feet(1.0), feet(12.0), "COMPARE", "true"));
        List<QuantityMeasurementEntity> all = repo.getAllMeasurements();
        assertEquals(1, all.size());
        assertEquals("COMPARE", all.get(0).getOperation());
    }

    @Test
    void testGetTotalCount() {
        assertEquals(0, repo.getTotalCount());
        repo.save(new QuantityMeasurementEntity(feet(1.0), "CONVERT", "12.0 INCHES"));
        repo.save(new QuantityMeasurementEntity(feet(1.0), feet(2.0), "ADD", "3.0 FEET"));
        assertEquals(2, repo.getTotalCount());
    }

    @Test
    void testGetMeasurementsByOperation() {
        repo.save(new QuantityMeasurementEntity(feet(1.0), feet(1.0), "COMPARE", "true"));
        repo.save(new QuantityMeasurementEntity(feet(1.0), "CONVERT", "12.0 INCHES"));
        repo.save(new QuantityMeasurementEntity(feet(1.0), feet(2.0), "COMPARE", "false"));

        List<QuantityMeasurementEntity> compareOps =
                repo.getMeasurementsByOperation("COMPARE");
        assertEquals(2, compareOps.size());
    }

    @Test
    void testGetMeasurementsByType() {
        repo.save(new QuantityMeasurementEntity(feet(1.0), feet(12.0), "COMPARE", "true"));
        repo.save(new QuantityMeasurementEntity(kg(1.0), kg(2.0), "ADD", "3.0 KILOGRAM"));

        List<QuantityMeasurementEntity> lengthOps =
                repo.getMeasurementsByType("LENGTH");
        assertEquals(1, lengthOps.size());
    }

    @Test
    void testClearAll() {
        repo.save(new QuantityMeasurementEntity(feet(1.0), feet(1.0), "COMPARE", "true"));
        repo.save(new QuantityMeasurementEntity(feet(1.0), feet(2.0), "ADD", "3.0 FEET"));
        repo.clearAll();
        assertEquals(0, repo.getTotalCount());
    }

    @Test
    void testSaveErrorEntity() {
        repo.save(new QuantityMeasurementEntity(
                feet(10.0), null, "DIVIDE", "Division by zero", true));
        List<QuantityMeasurementEntity> all = repo.getAllMeasurements();
        assertEquals(1, all.size());
        assertTrue(all.get(0).hasError());
    }

    @Test
    void testPoolStatistics_NotNull() {
        assertNotNull(repo.getPoolStatistics());
        assertTrue(repo.getPoolStatistics().contains("ConnectionPool"));
    }

    @Test
    void testDataIsolation_BetweenTests() {
        // clearAll() in @BeforeEach guarantees clean state
        assertEquals(0, repo.getTotalCount());
    }

    @Test
    void testSQLInjectionPrevention() {
        // Parameterized query treats this as a literal string — no SQL injection
        List<QuantityMeasurementEntity> result =
                repo.getMeasurementsByOperation("'; DROP TABLE quantity_measurement_entity; --");
        assertNotNull(result);
        assertEquals(0, result.size()); // No match, table still exists
        assertEquals(0, repo.getTotalCount()); // Table still intact
    }

    @Test
    void testBatchInsert_MultipleEntities() {
        for (int i = 0; i < 20; i++) {
            repo.save(new QuantityMeasurementEntity(
                    feet(i), feet(i + 1), "ADD", String.valueOf(i * 2.0 + 1)));
        }
        assertEquals(20, repo.getTotalCount());
    }
}