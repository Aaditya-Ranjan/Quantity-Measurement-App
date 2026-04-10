package com.app.quantitymeasurement.util;

import com.app.quantitymeasurement.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ConnectionPool - Thread-safe pool of reusable JDBC connections.
 * Singleton pattern: one pool per application.
 * UC16: Reduces overhead of creating/closing connections repeatedly.
 */
public class ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private static ConnectionPool instance;

    private final BlockingQueue<Connection> pool;
    private final ApplicationConfig config;
    private final List<Connection> allConnections;

    private ConnectionPool(ApplicationConfig config) {
        this.config = config;
        int poolSize = config.getPoolSize();
        this.pool = new ArrayBlockingQueue<>(poolSize);
        this.allConnections = new ArrayList<>();
        initializePool(poolSize);
        logger.info("ConnectionPool initialized with {} connections", poolSize);
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool(ApplicationConfig.getInstance());
                }
            }
        }
        return instance;
    }

    private void initializePool(int size) {
        try {
            Class.forName(config.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("JDBC driver not found: " + config.getDbDriver(), e);
        }
        for (int i = 0; i < size; i++) {
            try {
                Connection conn = DriverManager.getConnection(
                        config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
                pool.offer(conn);
                allConnections.add(conn);
            } catch (SQLException e) {
                throw new DatabaseException("Failed to create connection #" + i, e);
            }
        }
    }

    /**
     * Acquires a connection from the pool (waits up to timeout ms).
     */
    public Connection acquire() {
        try {
            Connection conn = pool.poll(config.getPoolTimeout(), TimeUnit.MILLISECONDS);
            if (conn == null)
                throw new DatabaseException("Connection pool exhausted — timeout waiting");
            if (!conn.isValid(5)) {
                conn = DriverManager.getConnection(
                        config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
            }
            return conn;
        } catch (SQLException | InterruptedException e) {
            throw new DatabaseException("Failed to acquire connection", e);
        }
    }

    /**
     * Returns a connection back to the pool.
     */
    public void release(Connection conn) {
        if (conn != null) {
            pool.offer(conn);
        }
    }

    /**
     * Pool statistics for monitoring.
     */
    public String getPoolStatistics() {
        return String.format("ConnectionPool[available=%d, total=%d]",
                pool.size(), allConnections.size());
    }

    /**
     * Closes all connections (call on application shutdown).
     */
    public void closeAll() {
        for (Connection conn : allConnections) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
        pool.clear();
        allConnections.clear();
        logger.info("ConnectionPool closed all connections");
    }
}