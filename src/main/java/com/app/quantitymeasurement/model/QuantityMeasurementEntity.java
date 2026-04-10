package com.app.quantitymeasurement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * QuantityMeasurementEntity - JPA entity mapped to DB table.
 * UC17: Adds @Entity, @Table, Lombok annotations, JPA lifecycle callbacks.
 */
@Entity
@Table(name = "quantity_measurement_entity", indexes = {
        @Index(name = "idx_operation", columnList = "operation"),
        @Index(name = "idx_measurement_type", columnList = "thisMeasurementType")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double thisValue;

    @Column(length = 50)
    private String thisUnit;

    @Column(length = 50)
    private String thisMeasurementType;

    private double thatValue;

    @Column(length = 50)
    private String thatUnit;

    @Column(length = 50)
    private String thatMeasurementType;

    @Column(nullable = false, length = 50)
    private String operation;

    @Column(length = 255)
    private String resultString;

    private double resultValue;

    @Column(length = 50)
    private String resultUnit;

    @Column(length = 50)
    private String resultMeasurementType;

    @Column(length = 500)
    private String errorMessage;

    private boolean isError;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}