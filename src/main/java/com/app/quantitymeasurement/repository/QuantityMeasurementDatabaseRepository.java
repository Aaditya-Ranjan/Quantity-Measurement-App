package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.DatabaseException;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * QuantityMeasurementDatabaseRepository - JDBC-based persistence implementation.
 * UC16: Replaces in-memory cache with relational database storage.
 * Uses parameterized queries (SQL injection prevention).
 * Delegates connection management to ConnectionPool.
 */
public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

    private static final Logger logger =
            LoggerFactory.getLogger(QuantityMeasurementDatabaseRepository.class);

    private static final String INSERT_SQL =
            "INSERT INTO quantity_measurement_entity " +
                    "(operand1_value, operand1_unit, operand1_type, " +
                    " operand2_value, operand2_unit, operand2_type, " +
                    " operation, result, has_error, error_message, timestamp_ms) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT * FROM quantity_measurement_entity ORDER BY id";

    private static final String SELECT_BY_OPERATION_SQL =
            "SELECT * FROM quantity_measurement_entity WHERE operation = ? ORDER BY id";

    private static final String SELECT_BY_TYPE_SQL =
            "SELECT * FROM quantity_measurement_entity " +
                    "WHERE operand1_type = ? OR operand2_type = ? ORDER BY id";

    private static final String COUNT_SQL =
            "SELECT COUNT(*) FROM quantity_measurement_entity";

    private static final String DELETE_ALL_SQL =
            "DELETE FROM quantity_measurement_entity";

    private final ConnectionPool connectionPool;

    public QuantityMeasurementDatabaseRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        initializeSchema();
        logger.info("QuantityMeasurementDatabaseRepository initialized");
    }

    // ===== Schema Initialization =====

    private void initializeSchema() {
        String createTable =
                "CREATE TABLE IF NOT EXISTS quantity_measurement_entity (" +
                        "  id             BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "  operand1_value DOUBLE," +
                        "  operand1_unit  VARCHAR(50)," +
                        "  operand1_type  VARCHAR(50)," +
                        "  operand2_value DOUBLE," +
                        "  operand2_unit  VARCHAR(50)," +
                        "  operand2_type  VARCHAR(50)," +
                        "  operation      VARCHAR(50) NOT NULL," +
                        "  result         VARCHAR(255)," +
                        "  has_error      BOOLEAN DEFAULT FALSE," +
                        "  error_message  VARCHAR(500)," +
                        "  timestamp_ms   BIGINT" +
                        ")";
        Connection conn = connectionPool.acquire();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            logger.info("Database schema verified/created");
        } catch (SQLException e) {
            throw new DatabaseException("Schema initialization failed", e);
        } finally {
            connectionPool.release(conn);
        }
    }

    // ===== CRUD Operations =====

    @Override
    public void save(QuantityMeasurementEntity entity) {
        Connection conn = connectionPool.acquire();
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            // Operand 1
            ps.setDouble(1, entity.getOperand1() != null ? entity.getOperand1().getValue() : 0.0);
            ps.setString(2, entity.getOperand1() != null ?
                    entity.getOperand1().getUnit().getUnitName() : null);
            ps.setString(3, entity.getOperand1() != null ?
                    entity.getOperand1().getUnit().getMeasurementType() : null);
            // Operand 2
            ps.setObject(4, entity.getOperand2() != null ? entity.getOperand2().getValue() : null);
            ps.setString(5, entity.getOperand2() != null ?
                    entity.getOperand2().getUnit().getUnitName() : null);
            ps.setString(6, entity.getOperand2() != null ?
                    entity.getOperand2().getUnit().getMeasurementType() : null);
            // Operation + Result
            ps.setString(7, entity.getOperation());
            ps.setString(8, entity.getResult());
            ps.setBoolean(9, entity.hasError());
            ps.setString(10, entity.getErrorMessage());
            ps.setLong(11, entity.getTimestamp());
            ps.executeUpdate();
            logger.debug("Saved entity: operation={}, result={}", entity.getOperation(), entity.getResult());
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save entity: " + e.getMessage(), e);
        } finally {
            connectionPool.release(conn);
        }
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        Connection conn = connectionPool.acquire();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRowToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all measurements", e);
        } finally {
            connectionPool.release(conn);
        }
        return results;
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        Connection conn = connectionPool.acquire();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_OPERATION_SQL)) {
            ps.setString(1, operation.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRowToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query by operation: " + operation, e);
        } finally {
            connectionPool.release(conn);
        }
        return results;
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        Connection conn = connectionPool.acquire();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_TYPE_SQL)) {
            ps.setString(1, measurementType.toUpperCase());
            ps.setString(2, measurementType.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRowToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query by type: " + measurementType, e);
        } finally {
            connectionPool.release(conn);
        }
        return results;
    }

    @Override
    public int getTotalCount() {
        Connection conn = connectionPool.acquire();
        try (PreparedStatement ps = conn.prepareStatement(COUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count measurements", e);
        } finally {
            connectionPool.release(conn);
        }
        return 0;
    }

    @Override
    public void clearAll() {
        Connection conn = connectionPool.acquire();
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ALL_SQL)) {
            int deleted = ps.executeUpdate();
            logger.info("Deleted {} measurement records", deleted);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete all measurements", e);
        } finally {
            connectionPool.release(conn);
        }
    }

    @Override
    public String getPoolStatistics() {
        return connectionPool.getPoolStatistics();
    }

    @Override
    public void releaseResources() {
        connectionPool.closeAll();
        logger.info("Database repository resources released");
    }

    // ===== Row Mapper =====

    private QuantityMeasurementEntity mapRowToEntity(ResultSet rs) throws SQLException {
        String op1Unit  = rs.getString("operand1_unit");
        String op1Type  = rs.getString("operand1_type");
        String op2Unit  = rs.getString("operand2_unit");
        String op2Type  = rs.getString("operand2_type");
        double op1Value = rs.getDouble("operand1_value");
        double op2Value = rs.getDouble("operand2_value");
        boolean hasError = rs.getBoolean("has_error");
        String operation = rs.getString("operation");

        QuantityDTO operand1 = null;
        if (op1Unit != null && op1Type != null) {
            operand1 = new QuantityDTO(op1Value, resolveDTOUnit(op1Type, op1Unit));
        }
        QuantityDTO operand2 = null;
        if (op2Unit != null && op2Type != null) {
            operand2 = new QuantityDTO(op2Value, resolveDTOUnit(op2Type, op2Unit));
        }

        if (hasError) {
            return new QuantityMeasurementEntity(
                    operand1, operand2, operation, rs.getString("error_message"), true);
        } else if (operand2 == null) {
            return new QuantityMeasurementEntity(
                    operand1, operation, rs.getString("result"));
        } else {
            return new QuantityMeasurementEntity(
                    operand1, operand2, operation, rs.getString("result"));
        }
    }

    private QuantityDTO.IMeasurableUnit resolveDTOUnit(String type, String name) {
        try {
            switch (type.toUpperCase()) {
                case "LENGTH":      return QuantityDTO.LengthUnit.valueOf(name);
                case "WEIGHT":      return QuantityDTO.WeightUnit.valueOf(name);
                case "VOLUME":      return QuantityDTO.VolumeUnit.valueOf(name);
                case "TEMPERATURE": return QuantityDTO.TemperatureUnit.valueOf(name);
                default: return null;
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown unit '{}' for type '{}'", name, type);
            return null;
        }
    }
}