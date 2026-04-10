package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * QuantityMeasurementCacheRepository - Singleton in-memory cache with disk persistence.
 * UC15: Singleton Pattern — single instance across entire application.
 * Thread-safe via double-checked locking.
 * Persists history to disk via Java Serialization.
 */
public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    private static final String FILE_PATH = "quantity_measurement_history.ser";
    private static volatile QuantityMeasurementCacheRepository instance;
    private final List<QuantityMeasurementEntity> cache;

    private QuantityMeasurementCacheRepository() {
        this.cache = new ArrayList<>();
        loadFromDisk();
    }

    /**
     * Thread-safe Singleton access via double-checked locking.
     */
    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            synchronized (QuantityMeasurementCacheRepository.class) {
                if (instance == null) {
                    instance = new QuantityMeasurementCacheRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        cache.add(entity);
        saveToDisk();
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return Collections.unmodifiableList(cache);
    }

    @Override
    public void clearAll() {
        cache.clear();
        new File(FILE_PATH).delete();
    }

    private void saveToDisk() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(cache));
        } catch (IOException e) {
            System.err.println("Warning: Could not persist to disk: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromDisk() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<QuantityMeasurementEntity> saved =
                    (List<QuantityMeasurementEntity>) ois.readObject();
            cache.addAll(saved);
        } catch (Exception e) {
            System.err.println("Warning: Could not load history: " + e.getMessage());
        }
    }
    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        List<QuantityMeasurementEntity> result = new ArrayList<>();
        for (QuantityMeasurementEntity e : cache)
            if (operation.equalsIgnoreCase(e.getOperation())) result.add(e);
        return result;
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        List<QuantityMeasurementEntity> result = new ArrayList<>();
        for (QuantityMeasurementEntity e : cache) {
            if (e.getOperand1() != null &&
                    measurementType.equalsIgnoreCase(e.getOperand1().getUnit().getMeasurementType()))
                result.add(e);
        }
        return result;
    }

    @Override
    public int getTotalCount() {
        return cache.size();
    }
}