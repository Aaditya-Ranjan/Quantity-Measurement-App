package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.apps.quantitymeasurement.QuantityMeasurementApp.QuantityLength;
import com.apps.quantitymeasurement.QuantityMeasurementApp.LengthUnit;

public class QuantityMeasurementAppTest {

    // ===== UC3: Previous Tests =====

    @Test
    public void testEquality_FeetToFeet_SameValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.FEET);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_InchToInch_SameValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.INCHES);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_FeetToInch_EquivalentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_InchToFeet_EquivalentValue() {
        QuantityLength q1 = new QuantityLength(12.0, LengthUnit.INCHES);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.FEET);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_FeetToFeet_DifferentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(2.0, LengthUnit.FEET);
        assertFalse(q1.equals(q2));
    }

    @Test
    public void testEquality_InchToInch_DifferentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.INCHES);
        QuantityLength q2 = new QuantityLength(2.0, LengthUnit.INCHES);
        assertFalse(q1.equals(q2));
    }

    @Test
    public void testEquality_NullComparison() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        assertFalse(q1.equals(null));
    }

    @Test
    public void testEquality_SameReference() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        assertTrue(q1.equals(q1));
    }

    @Test
    public void testEquality_NullUnit() {
        assertThrows(NullPointerException.class, () -> new QuantityLength(1.0, null));
    }

    @Test
    public void testEquality_DifferentClass() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        assertFalse(q1.equals("1.0"));
    }

    // ===== UC4: Yards Tests =====

    @Test
    public void testEquality_YardToYard_SameValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.YARDS);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_YardToYard_DifferentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q2 = new QuantityLength(2.0, LengthUnit.YARDS);
        assertFalse(q1.equals(q2));
    }

    @Test
    public void testEquality_YardToFeet_EquivalentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q2 = new QuantityLength(3.0, LengthUnit.FEET);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_FeetToYard_EquivalentValue() {
        QuantityLength q1 = new QuantityLength(3.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.YARDS);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_YardToInches_EquivalentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q2 = new QuantityLength(36.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_InchesToYard_EquivalentValue() {
        QuantityLength q1 = new QuantityLength(36.0, LengthUnit.INCHES);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.YARDS);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_YardToFeet_NonEquivalentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q2 = new QuantityLength(2.0, LengthUnit.FEET);
        assertFalse(q1.equals(q2));
    }

    @Test
    public void testEquality_YardSameReference() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        assertTrue(q1.equals(q1));
    }

    @Test
    public void testEquality_YardNullComparison() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        assertFalse(q1.equals(null));
    }

    @Test
    public void testEquality_YardWithNullUnit() {
        assertThrows(NullPointerException.class, () -> new QuantityLength(1.0, null));
    }

    // ===== UC4: Centimeters Tests =====

    @Test
    public void testEquality_CentimetersToCentimeters_SameValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_centimetersToInches_EquivalentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        QuantityLength q2 = new QuantityLength(0.393701, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testEquality_centimetersToFeet_NonEquivalentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.FEET);
        assertFalse(q1.equals(q2));
    }

    @Test
    public void testEquality_CentimetersSameReference() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        assertTrue(q1.equals(q1));
    }

    @Test
    public void testEquality_CentimetersNullComparison() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        assertFalse(q1.equals(null));
    }

    @Test
    public void testEquality_CentimetersWithNullUnit() {
        assertThrows(NullPointerException.class, () -> new QuantityLength(1.0, null));
    }

    @Test
    public void testEquality_MultiUnit_TransitiveProperty() {
        QuantityLength yards = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength feet = new QuantityLength(3.0, LengthUnit.FEET);
        QuantityLength inches = new QuantityLength(36.0, LengthUnit.INCHES);
        assertTrue(yards.equals(feet));
        assertTrue(feet.equals(inches));
        assertTrue(yards.equals(inches));
    }

    @Test
    public void testEquality_AllUnits_ComplexScenario() {
        QuantityLength q1 = new QuantityLength(2.0, LengthUnit.YARDS);
        QuantityLength q2 = new QuantityLength(6.0, LengthUnit.FEET);
        QuantityLength q3 = new QuantityLength(72.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
        assertTrue(q2.equals(q3));
        assertTrue(q1.equals(q3));
    }
}