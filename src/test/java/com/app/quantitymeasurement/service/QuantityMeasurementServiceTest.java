package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementServiceTest {

    private IQuantityMeasurementService service;
    private List<QuantityMeasurementEntity> store;

    @BeforeEach
    void setup() {
        store = new ArrayList<>();
        IQuantityMeasurementRepository mockRepo = new IQuantityMeasurementRepository() {
            public void save(QuantityMeasurementEntity e) { store.add(e); }
            public List<QuantityMeasurementEntity> getAllMeasurements() { return store; }
            public List<QuantityMeasurementEntity> getMeasurementsByOperation(String op) {
                List<QuantityMeasurementEntity> r = new ArrayList<>();
                for (QuantityMeasurementEntity e : store)
                    if (e.getOperation().equals(op)) r.add(e);
                return r;
            }
            public List<QuantityMeasurementEntity> getMeasurementsByType(String t) { return store; }
            public int getTotalCount() { return store.size(); }
            public void clearAll() { store.clear(); }
        };
        service = new QuantityMeasurementServiceImpl(mockRepo);
    }

    @Test
    void testCompare_SameUnit_True() {
        assertTrue(service.compareEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET)));
    }

    @Test
    void testCompare_CrossUnit_FeetToInches_True() {
        assertTrue(service.compareEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test
    void testCompare_CrossCategory_ThrowsException() {
        assertThrows(QuantityMeasurementException.class, () ->
                service.compareEquality(
                        new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                        new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM)));
    }

    @Test
    void testConvert_FeetToInches() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                QuantityDTO.LengthUnit.INCHES);
        assertEquals(12.0, result.getValue(), 1e-6);
    }

    @Test
    void testConvert_CelsiusToFahrenheit() {
        QuantityDTO result = service.convert(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                QuantityDTO.TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, result.getValue(), 1e-4);
    }

    @Test
    void testAdd_Length() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES),
                QuantityDTO.LengthUnit.FEET);
        assertEquals(2.0, result.getValue(), 1e-6);
    }

    @Test
    void testAdd_Temperature_ThrowsException() {
        assertThrows(QuantityMeasurementException.class, () ->
                service.add(
                        new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                        new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS),
                        QuantityDTO.TemperatureUnit.CELSIUS));
    }

    @Test
    void testSubtract_Weight() {
        QuantityDTO result = service.subtract(
                new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5000.0, QuantityDTO.WeightUnit.GRAM),
                QuantityDTO.WeightUnit.KILOGRAM);
        assertEquals(5.0, result.getValue(), 1e-6);
    }

    @Test
    void testDivide_Length() {
        double result = service.divide(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET));
        assertEquals(5.0, result, 1e-6);
    }

    @Test
    void testDivide_ByZero_ThrowsException() {
        assertThrows(QuantityMeasurementException.class, () ->
                service.divide(
                        new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                        new QuantityDTO(0.0, QuantityDTO.LengthUnit.FEET)));
    }

    @Test
    void testOperation_SavedToRepository() {
        service.compareEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1, store.size());
        assertEquals("COMPARE", store.get(0).getOperation());
    }
}