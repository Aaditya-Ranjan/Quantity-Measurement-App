package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuantityMeasurementAppTest {

    private static final double EPSILON = 1e-6;

    // ===== UC1-UC8: Length Backward Compatibility =====

    @Test
    public void testGenericQuantity_LengthEquality_SameValue() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(1.0, LengthUnit.FEET);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testGenericQuantity_LengthEquality_CrossUnit() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testGenericQuantity_LengthEquality_DifferentValue() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(2.0, LengthUnit.FEET);
        assertFalse(q1.equals(q2));
    }

    @Test
    public void testGenericQuantity_LengthEquality_YardToFeet() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.YARDS);
        Quantity<LengthUnit> q2 = new Quantity<>(3.0, LengthUnit.FEET);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testGenericQuantity_LengthEquality_YardToInches() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.YARDS);
        Quantity<LengthUnit> q2 = new Quantity<>(36.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
    }

    @Test
    public void testGenericQuantity_LengthConversion_FeetToInches() {
        Quantity<LengthUnit> result = new Quantity<>(1.0, LengthUnit.FEET)
                .convertTo(LengthUnit.INCHES);
        assertEquals(new Quantity<>(12.0, LengthUnit.INCHES), result);
    }

    @Test
    public void testGenericQuantity_LengthConversion_YardsToInches() {
        Quantity<LengthUnit> result = new Quantity<>(1.0, LengthUnit.YARDS)
                .convertTo(LengthUnit.INCHES);
        assertEquals(36.0, result.getValue(), EPSILON);
    }

    @Test
    public void testGenericQuantity_LengthAddition_SameUnit() {
        Quantity<LengthUnit> result = Quantity.add(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(new Quantity<>(3.0, LengthUnit.FEET), result);
    }

    @Test
    public void testGenericQuantity_LengthAddition_CrossUnit() {
        Quantity<LengthUnit> result = Quantity.add(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(12.0, LengthUnit.INCHES));
        assertEquals(new Quantity<>(2.0, LengthUnit.FEET), result);
    }

    @Test
    public void testGenericQuantity_LengthAddition_ExplicitTargetUnit() {
        Quantity<LengthUnit> result = Quantity.add(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(12.0, LengthUnit.INCHES),
                LengthUnit.YARDS);
        assertEquals(2.0 / 3.0, result.getValue(), EPSILON);
    }

    // ===== UC9: Weight Backward Compatibility =====

    @Test
    public void testGenericQuantity_WeightEquality_SameValue() {
        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertTrue(w1.equals(w2));
    }

    @Test
    public void testGenericQuantity_WeightEquality_KgToGram() {
        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);
        assertTrue(w1.equals(w2));
    }

    @Test
    public void testGenericQuantity_WeightEquality_GramToKg() {
        Quantity<WeightUnit> w1 = new Quantity<>(1000.0, WeightUnit.GRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertTrue(w1.equals(w2));
    }

    @Test
    public void testGenericQuantity_WeightConversion_KgToGram() {
        Quantity<WeightUnit> result = new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .convertTo(WeightUnit.GRAM);
        assertEquals(new Quantity<>(1000.0, WeightUnit.GRAM), result);
    }

    @Test
    public void testGenericQuantity_WeightConversion_PoundToKg() {
        Quantity<WeightUnit> result = new Quantity<>(2.20462, WeightUnit.POUND)
                .convertTo(WeightUnit.KILOGRAM);
        assertEquals(1.0, result.getValue(), 1e-4);
    }

    @Test
    public void testGenericQuantity_WeightAddition_SameUnit() {
        Quantity<WeightUnit> result = Quantity.add(
                new Quantity<>(1.0, WeightUnit.KILOGRAM),
                new Quantity<>(2.0, WeightUnit.KILOGRAM));
        assertEquals(new Quantity<>(3.0, WeightUnit.KILOGRAM), result);
    }

    @Test
    public void testGenericQuantity_WeightAddition_CrossUnit() {
        Quantity<WeightUnit> result = Quantity.add(
                new Quantity<>(1.0, WeightUnit.KILOGRAM),
                new Quantity<>(1000.0, WeightUnit.GRAM));
        assertEquals(new Quantity<>(2.0, WeightUnit.KILOGRAM), result);
    }

    @Test
    public void testGenericQuantity_WeightAddition_ExplicitTargetUnit() {
        Quantity<WeightUnit> result = Quantity.add(
                new Quantity<>(1.0, WeightUnit.KILOGRAM),
                new Quantity<>(1000.0, WeightUnit.GRAM),
                WeightUnit.GRAM);
        assertEquals(new Quantity<>(2000.0, WeightUnit.GRAM), result);
    }

    // ===== UC10: Cross-Category Prevention =====

    @Test
    public void testCrossCategoryPrevention_LengthVsWeight() {
        Quantity<LengthUnit> length = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertFalse(length.equals(weight));
    }

    // ===== UC10: IMeasurable Interface =====

    @Test
    public void testIMeasurableInterface_LengthUnit() {
        assertEquals(1.0, LengthUnit.FEET.getConversionFactor(), EPSILON);
        assertEquals(5.0, LengthUnit.FEET.convertToBaseUnit(5.0), EPSILON);
        assertEquals(5.0, LengthUnit.FEET.convertFromBaseUnit(5.0), EPSILON);
        assertEquals("FEET", LengthUnit.FEET.getUnitName());
    }

    @Test
    public void testIMeasurableInterface_WeightUnit() {
        assertEquals(1.0, WeightUnit.KILOGRAM.getConversionFactor(), EPSILON);
        assertEquals(5.0, WeightUnit.KILOGRAM.convertToBaseUnit(5.0), EPSILON);
        assertEquals(5.0, WeightUnit.KILOGRAM.convertFromBaseUnit(5.0), EPSILON);
        assertEquals("KILOGRAM", WeightUnit.KILOGRAM.getUnitName());
    }

    // ===== UC10: Constructor Validation =====

    @Test
    public void testGenericQuantity_NullUnit() {
        assertThrows(NullPointerException.class,
                () -> new Quantity<>(1.0, (LengthUnit) null));
    }

    @Test
    public void testGenericQuantity_InvalidValue_NaN() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(Double.NaN, LengthUnit.FEET));
    }

    @Test
    public void testGenericQuantity_InvalidValue_Infinite() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(Double.POSITIVE_INFINITY, WeightUnit.KILOGRAM));
    }

    // ===== UC10: Equality Contract =====

    @Test
    public void testGenericQuantity_Reflexive() {
        Quantity<LengthUnit> q = new Quantity<>(1.0, LengthUnit.FEET);
        assertTrue(q.equals(q));
    }

    @Test
    public void testGenericQuantity_Symmetric() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
        assertTrue(q2.equals(q1));
    }

    @Test
    public void testGenericQuantity_Transitive() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.YARDS);
        Quantity<LengthUnit> q2 = new Quantity<>(3.0, LengthUnit.FEET);
        Quantity<LengthUnit> q3 = new Quantity<>(36.0, LengthUnit.INCHES);
        assertTrue(q1.equals(q2));
        assertTrue(q2.equals(q3));
        assertTrue(q1.equals(q3));
    }

    @Test
    public void testGenericQuantity_NullComparison() {
        Quantity<LengthUnit> q = new Quantity<>(1.0, LengthUnit.FEET);
        assertFalse(q.equals(null));
    }

    // ===== UC10: HashCode =====

    @Test
    public void testGenericQuantity_HashCode_EqualObjects() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void testGenericQuantity_HashCode_WeightEqualObjects() {
        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);
        assertEquals(w1.hashCode(), w2.hashCode());
    }

    // ===== UC10: Immutability =====

    @Test
    public void testGenericQuantity_Immutability_ConvertTo() {
        Quantity<LengthUnit> original = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> converted = original.convertTo(LengthUnit.INCHES);
        assertEquals(1.0, original.getValue(), EPSILON);
        assertEquals(12.0, converted.getValue(), EPSILON);
    }

    @Test
    public void testGenericQuantity_Immutability_Add() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(2.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = Quantity.add(q1, q2);
        assertEquals(1.0, q1.getValue(), EPSILON);
        assertEquals(2.0, q2.getValue(), EPSILON);
        assertEquals(3.0, result.getValue(), EPSILON);
    }

    // ===== UC10: Scalability - VolumeUnit =====

    @Test
    public void testScalability_NewUnitEnumIntegration() {
        // Verifies new unit enums integrate without changing Quantity<U>
        // VolumeUnit would implement IMeasurable and work seamlessly
        // For now we verify the pattern with existing units
        IMeasurable unit = LengthUnit.FEET;
        assertEquals(1.0, unit.getConversionFactor(), EPSILON);
        assertEquals("FEET", unit.getUnitName());
    }
}