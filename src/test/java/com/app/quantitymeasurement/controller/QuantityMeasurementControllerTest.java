package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuantityMeasurementControllerTest {

    @Mock
    private IQuantityMeasurementService mockService;

    private QuantityMeasurementController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new QuantityMeasurementController(mockService);
    }

    @Test
    void testConstructor_NullService_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuantityMeasurementController(null));
    }

    @Test
    void testPerformEquality_CallsService() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        when(mockService.compareEquality(q1, q2)).thenReturn(true);
        assertDoesNotThrow(() -> controller.performEquality(q1, q2));
        verify(mockService, times(1)).compareEquality(q1, q2);
    }

    @Test
    void testPerformEquality_ServiceThrows_HandledGracefully() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        when(mockService.compareEquality(q1, q2))
                .thenThrow(new QuantityMeasurementException("Category mismatch"));
        assertDoesNotThrow(() -> controller.performEquality(q1, q2));
    }

    @Test
    void testPerformConversion_CallsService() {
        QuantityDTO input = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO expected = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        when(mockService.convert(input, QuantityDTO.LengthUnit.INCHES)).thenReturn(expected);
        assertDoesNotThrow(() ->
                controller.performConversion(input, QuantityDTO.LengthUnit.INCHES));
        verify(mockService).convert(input, QuantityDTO.LengthUnit.INCHES);
    }

    @Test
    void testPerformAddition_CallsService() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO result = new QuantityDTO(3.0, QuantityDTO.LengthUnit.FEET);
        when(mockService.add(q1, q2, QuantityDTO.LengthUnit.FEET)).thenReturn(result);
        assertDoesNotThrow(() ->
                controller.performAddition(q1, q2, QuantityDTO.LengthUnit.FEET));
        verify(mockService).add(q1, q2, QuantityDTO.LengthUnit.FEET);
    }

    @Test
    void testPerformAddition_UnsupportedOperation_HandledGracefully() {
        QuantityDTO t1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO t2 = new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS);
        when(mockService.add(t1, t2, QuantityDTO.TemperatureUnit.CELSIUS))
                .thenThrow(new QuantityMeasurementException("Addition not supported"));
        assertDoesNotThrow(() ->
                controller.performAddition(t1, t2, QuantityDTO.TemperatureUnit.CELSIUS));
    }

    @Test
    void testPerformDivision_CallsService() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        when(mockService.divide(q1, q2)).thenReturn(5.0);
        assertDoesNotThrow(() -> controller.performDivision(q1, q2));
        verify(mockService).divide(q1, q2);
    }
}