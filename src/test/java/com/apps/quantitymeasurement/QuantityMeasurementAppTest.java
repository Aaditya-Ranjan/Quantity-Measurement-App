package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.apps.quantitymeasurement.QuantityMeasurementApp.Feet;

public class QuantityMeasurementAppTest {

    @Test
    public void testFeetEquality_SameValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);
        assertTrue(feet1.equals(feet2), "1.0 ft should equal 1.0 ft");
    }

    @Test
    public void testFeetEquality_DifferentValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(2.0);
        assertFalse(feet1.equals(feet2), "1.0 ft should NOT equal 2.0 ft");
    }

    @Test
    public void testFeetEquality_NullComparison() {
        Feet feet1 = new Feet(1.0);
        assertFalse(feet1.equals(null), "Feet should NOT equal null");
    }

    @Test
    public void testFeetEquality_DifferentClass() {
        Feet feet1 = new Feet(1.0);
        assertFalse(feet1.equals("1.0"), "Feet should NOT equal a String");
    }

    @Test
    public void testFeetEquality_SameReference() {
        Feet feet1 = new Feet(1.0);
        assertTrue(feet1.equals(feet1), "Feet should equal itself");
    }
}