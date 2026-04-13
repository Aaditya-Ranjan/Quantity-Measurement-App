package com.app.quantitymeasurement;

import com.app.quantitymeasurement.model.QuantityInputDTO;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests - full Spring Boot context with TestRestTemplate.
 * UC17: End-to-end tests across all layers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuantityMeasurementRepository repository;

    private String url(String path) {
        return "http://localhost:" + port + "/api/v1/quantities" + path;
    }

    private QuantityInputDTO input(double v1, String u1, String t1,
                                   double v2, String u2, String t2) {
        return new QuantityInputDTO(
                new QuantityDTO(v1, u1, t1),
                new QuantityDTO(v2, u2, t2));
    }

    @BeforeEach
    void cleanDB() { repository.deleteAll(); }

    @Test
    @Order(1)
    void testApplicationContextLoads() {
        assertNotNull(restTemplate);
    }

    @Test
    @Order(2)
    void testCompare_FeetToInches_True() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/compare"),
                input(1.0,"FEET","LengthUnit",12.0,"INCHES","LengthUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("true", response.getBody().getResultString());
        assertFalse(response.getBody().isError());
    }

    @Test
    @Order(3)
    void testCompare_FeetToFeet_False() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/compare"),
                input(1.0,"FEET","LengthUnit",2.0,"FEET","LengthUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("false", response.getBody().getResultString());
    }

    @Test
    @Order(4)
    void testConvert_FeetToInches() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/convert"),
                input(1.0,"FEET","LengthUnit",0.0,"INCHES","LengthUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(12.0, response.getBody().getResultValue(), 1e-6);
    }

    @Test
    @Order(5)
    void testConvert_CelsiusToFahrenheit() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/convert"),
                input(100.0,"CELSIUS","TemperatureUnit",
                        0.0,"FAHRENHEIT","TemperatureUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(212.0, response.getBody().getResultValue(), 1e-4);
    }

    @Test
    @Order(6)
    void testAdd_FeetPlusInches() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/add"),
                input(1.0,"FEET","LengthUnit",12.0,"INCHES","LengthUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 1e-6);
        assertEquals("FEET", response.getBody().getResultUnit());
    }

    @Test
    @Order(7)
    void testAdd_KilogramPlusGram() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/add"),
                input(1.0,"KILOGRAM","WeightUnit",1000.0,"GRAM","WeightUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 1e-6);
    }

    @Test
    @Order(8)
    void testAdd_Temperature_ReturnsError() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/add"),
                input(100.0,"CELSIUS","TemperatureUnit",
                        50.0,"CELSIUS","TemperatureUnit"),
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(9)
    void testSubtract_FeetMinusInches() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/subtract"),
                input(10.0,"FEET","LengthUnit",6.0,"INCHES","LengthUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(9.5, response.getBody().getResultValue(), 1e-6);
    }

    @Test
    @Order(10)
    void testDivide_FeetByFeet() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/divide"),
                input(10.0,"FEET","LengthUnit",2.0,"FEET","LengthUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5.0, response.getBody().getResultValue(), 1e-6);
    }

    @Test
    @Order(11)
    void testDivide_ByZero_Returns500() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/divide"),
                input(10.0,"FEET","LengthUnit",0.0,"FEET","LengthUnit"),
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(12)
    void testCrossCategory_Returns400() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/add"),
                input(1.0,"FEET","LengthUnit",1.0,"KILOGRAM","WeightUnit"),
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(13)
    void testHistoryByOperation_AfterCompare() {
        restTemplate.postForEntity(url("/compare"),
                input(1.0,"FEET","LengthUnit",12.0,"INCHES","LengthUnit"),
                QuantityMeasurementDTO.class);
        ResponseEntity<QuantityMeasurementDTO[]> history = restTemplate.getForEntity(
                url("/history/operation/compare"),
                QuantityMeasurementDTO[].class);
        assertEquals(HttpStatus.OK, history.getStatusCode());
        assertTrue(history.getBody().length >= 1);
    }

    @Test
    @Order(14)
    void testHistoryByType_AfterConvert() {
        restTemplate.postForEntity(url("/convert"),
                input(1.0,"FEET","LengthUnit",0.0,"INCHES","LengthUnit"),
                QuantityMeasurementDTO.class);
        ResponseEntity<QuantityMeasurementDTO[]> history = restTemplate.getForEntity(
                url("/history/type/LengthUnit"),
                QuantityMeasurementDTO[].class);
        assertEquals(HttpStatus.OK, history.getStatusCode());
        assertTrue(history.getBody().length >= 1);
    }

    @Test
    @Order(15)
    void testOperationCount() {
        restTemplate.postForEntity(url("/compare"),
                input(1.0,"FEET","LengthUnit",12.0,"INCHES","LengthUnit"),
                QuantityMeasurementDTO.class);
        restTemplate.postForEntity(url("/compare"),
                input(1.0,"FEET","LengthUnit",1.0,"FEET","LengthUnit"),
                QuantityMeasurementDTO.class);
        ResponseEntity<Long> count = restTemplate.getForEntity(
                url("/count/compare"), Long.class);
        assertEquals(HttpStatus.OK, count.getStatusCode());
        assertTrue(count.getBody() >= 2);
    }

    @Test
    @Order(16)
    void testErrorHistory_AfterFailedOperation() {
        restTemplate.postForEntity(url("/add"),
                input(100.0,"CELSIUS","TemperatureUnit",
                        50.0,"CELSIUS","TemperatureUnit"),
                String.class);
        ResponseEntity<QuantityMeasurementDTO[]> errors = restTemplate.getForEntity(
                url("/history/errored"),
                QuantityMeasurementDTO[].class);
        assertEquals(HttpStatus.OK, errors.getStatusCode());
        assertTrue(errors.getBody().length >= 1);
    }

    @Test
    @Order(17)
    void testActuatorHealth() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health",
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("UP"));
    }

    @Test
    @Order(18)
    void testVolumeOperations() {
        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                url("/compare"),
                input(1.0,"LITRE","VolumeUnit",1000.0,"MILLILITRE","VolumeUnit"),
                QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody().getResultString());
    }
}