package com.app.quantitymeasurement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QuantityMeasurementApplication - Spring Boot entry point.
 * UC17: Replaces UC16 QuantityMeasurementApp with Spring Boot bootstrap.
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Quantity Measurement API",
        version = "1.0",
        description = "REST API for quantity measurement operations (UC17)"))
public class QuantityMeasurementApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementApplication.class, args);
    }
}