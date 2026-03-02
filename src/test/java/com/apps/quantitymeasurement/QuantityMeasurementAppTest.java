package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.apps.quantitymeasurement.QuantityMeasurementApp.Feet;
import com.apps.quantitymeasurement.QuantityMeasurementApp.Inches;

public class QuantityMeasurementAppTest {

    // ===== UC1: Feet Tests =====

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
    public void testFeetEquality_NonNumericInput() {
        Feet feet1 = new Feet(1.0);
        assertFalse(feet1.equals("1.0"), "Feet should NOT equal a String");
    }

    @Test
    public void testFeetEquality_SameReference() {
        Feet feet1 = new Feet(1.0);
        assertTrue(feet1.equals(feet1), "Feet should equal itself");
    }

    // ===== UC2: Inches Tests =====

    @Test
    public void testInchesEquality_SameValue() {
        Inches inch1 = new Inches(1.0);
        Inches inch2 = new Inches(1.0);
        assertTrue(inch1.equals(inch2), "1.0 inch should equal 1.0 inch");
    }

    @Test
    public void testInchesEquality_DifferentValue() {
        Inches inch1 = new Inches(1.0);
        Inches inch2 = new Inches(2.0);
        assertFalse(inch1.equals(inch2), "1.0 inch should NOT equal 2.0 inch");
    }

    @Test
    public void testInchesEquality_NullComparison() {
        Inches inch1 = new Inches(1.0);
        assertFalse(inch1.equals(null), "Inches should NOT equal null");
    }

    @Test
    public void testInchesEquality_NonNumericInput() {
        Inches inch1 = new Inches(1.0);
        assertFalse(inch1.equals("1.0"), "Inches should NOT equal a String");
    }

    @Test
    public void testInchesEquality_SameReference() {
        Inches inch1 = new Inches(1.0);
        assertTrue(inch1.equals(inch1), "Inches should equal itself");
    }
}