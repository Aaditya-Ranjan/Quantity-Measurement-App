package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.apps.quantitymeasurement.entity.QuantityDTO;
import com.apps.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.entity.QuantityModel;
import com.apps.quantitymeasurement.exception.QuantityMeasurementException;
import com.apps.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;
import com.apps.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.apps.quantitymeasurement.controller.QuantityMeasurementController;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;

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

    // ===== UC11: Volume Tests =====

    @Test
    public void testEquality_LitreToLitre_SameValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1.0, VolumeUnit.LITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_LitreToLitre_DifferentValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(2.0, VolumeUnit.LITRE);
        assertFalse(v1.equals(v2));
    }

    @Test
    public void testEquality_LitreToMillilitre_EquivalentValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_MillilitreToLitre_EquivalentValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1.0, VolumeUnit.LITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_LitreToGallon_EquivalentValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(3.78541, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1.0, VolumeUnit.GALLON);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_GallonToLitre_EquivalentValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.GALLON);
        Quantity<VolumeUnit> v2 = new Quantity<>(3.78541, VolumeUnit.LITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_VolumeVsLength_Incompatible() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        assertFalse(v1.equals(l1));
    }

    @Test
    public void testEquality_VolumeVsWeight_Incompatible() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertFalse(v1.equals(w1));
    }

    @Test
    public void testEquality_NullComparison_Volume() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        assertFalse(v1.equals(null));
    }

    @Test
    public void testEquality_SameReference_Volume() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        assertTrue(v1.equals(v1));
    }

    @Test
    public void testEquality_NullUnit_Volume() {
        assertThrows(NullPointerException.class,
                () -> new Quantity<>(1.0, (VolumeUnit) null));
    }

    @Test
    public void testEquality_TransitiveProperty_Volume() {
        Quantity<VolumeUnit> a = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> b = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> c = new Quantity<>(1.0, VolumeUnit.LITRE);
        assertTrue(a.equals(b));
        assertTrue(b.equals(c));
        assertTrue(a.equals(c));
    }

    @Test
    public void testEquality_ZeroValue_Volume() {
        Quantity<VolumeUnit> v1 = new Quantity<>(0.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(0.0, VolumeUnit.MILLILITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_NegativeVolume() {
        Quantity<VolumeUnit> v1 = new Quantity<>(-1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(-1000.0, VolumeUnit.MILLILITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_LargeVolumeValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1000000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.LITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testEquality_SmallVolumeValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(0.001, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1.0, VolumeUnit.MILLILITRE);
        assertTrue(v1.equals(v2));
    }

    @Test
    public void testConversion_LitreToMillilitre() {
        Quantity<VolumeUnit> result = new Quantity<>(1.0, VolumeUnit.LITRE)
                .convertTo(VolumeUnit.MILLILITRE);
        assertEquals(new Quantity<>(1000.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testConversion_MillilitreToLitre() {
        Quantity<VolumeUnit> result = new Quantity<>(1000.0, VolumeUnit.MILLILITRE)
                .convertTo(VolumeUnit.LITRE);
        assertEquals(new Quantity<>(1.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testConversion_GallonToLitre() {
        Quantity<VolumeUnit> result = new Quantity<>(1.0, VolumeUnit.GALLON)
                .convertTo(VolumeUnit.LITRE);
        assertEquals(3.78541, result.getValue(), 1e-4);
    }

    @Test
    public void testConversion_LitreToGallon() {
        Quantity<VolumeUnit> result = new Quantity<>(3.78541, VolumeUnit.LITRE)
                .convertTo(VolumeUnit.GALLON);
        assertEquals(1.0, result.getValue(), 1e-4);
    }

    @Test
    public void testConversion_MillilitreToGallon() {
        Quantity<VolumeUnit> result = new Quantity<>(1000.0, VolumeUnit.MILLILITRE)
                .convertTo(VolumeUnit.GALLON);
        assertEquals(0.264172, result.getValue(), 1e-4);
    }

    @Test
    public void testConversion_SameUnit_Volume() {
        Quantity<VolumeUnit> result = new Quantity<>(5.0, VolumeUnit.LITRE)
                .convertTo(VolumeUnit.LITRE);
        assertEquals(new Quantity<>(5.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testConversion_ZeroValue_Volume() {
        Quantity<VolumeUnit> result = new Quantity<>(0.0, VolumeUnit.LITRE)
                .convertTo(VolumeUnit.MILLILITRE);
        assertEquals(new Quantity<>(0.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testConversion_NegativeValue_Volume() {
        Quantity<VolumeUnit> result = new Quantity<>(-1.0, VolumeUnit.LITRE)
                .convertTo(VolumeUnit.MILLILITRE);
        assertEquals(new Quantity<>(-1000.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testConversion_RoundTrip_Volume() {
        Quantity<VolumeUnit> original = new Quantity<>(1.5, VolumeUnit.LITRE);
        Quantity<VolumeUnit> result = original
                .convertTo(VolumeUnit.MILLILITRE)
                .convertTo(VolumeUnit.LITRE);
        assertEquals(original.getValue(), result.getValue(), EPSILON);
    }

    @Test
    public void testAddition_SameUnit_LitrePlusLitre() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(1.0, VolumeUnit.LITRE),
                new Quantity<>(2.0, VolumeUnit.LITRE));
        assertEquals(new Quantity<>(3.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_SameUnit_MillilitrePlusMillilitre() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(500.0, VolumeUnit.MILLILITRE),
                new Quantity<>(500.0, VolumeUnit.MILLILITRE));
        assertEquals(new Quantity<>(1000.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testAddition_CrossUnit_LitrePlusMillilitre() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(1.0, VolumeUnit.LITRE),
                new Quantity<>(1000.0, VolumeUnit.MILLILITRE));
        assertEquals(new Quantity<>(2.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_CrossUnit_MillilitrePlusLitre() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(1000.0, VolumeUnit.MILLILITRE),
                new Quantity<>(1.0, VolumeUnit.LITRE));
        assertEquals(new Quantity<>(2000.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testAddition_CrossUnit_GallonPlusLitre() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(1.0, VolumeUnit.GALLON),
                new Quantity<>(3.78541, VolumeUnit.LITRE));
        assertEquals(2.0, result.getValue(), 1e-4);
    }

    @Test
    public void testAddition_ExplicitTargetUnit_Litre() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(1.0, VolumeUnit.LITRE),
                new Quantity<>(1000.0, VolumeUnit.MILLILITRE),
                VolumeUnit.LITRE);
        assertEquals(new Quantity<>(2.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_ExplicitTargetUnit_Millilitre() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(1.0, VolumeUnit.LITRE),
                new Quantity<>(1000.0, VolumeUnit.MILLILITRE),
                VolumeUnit.MILLILITRE);
        assertEquals(new Quantity<>(2000.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testAddition_ExplicitTargetUnit_Gallon() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(3.78541, VolumeUnit.LITRE),
                new Quantity<>(3.78541, VolumeUnit.LITRE),
                VolumeUnit.GALLON);
        assertEquals(2.0, result.getValue(), 1e-4);
    }

    @Test
    public void testAddition_WithZero_Volume() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(5.0, VolumeUnit.LITRE),
                new Quantity<>(0.0, VolumeUnit.MILLILITRE));
        assertEquals(new Quantity<>(5.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_NegativeValues_Volume() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(5.0, VolumeUnit.LITRE),
                new Quantity<>(-2000.0, VolumeUnit.MILLILITRE));
        assertEquals(new Quantity<>(3.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_LargeValues_Volume() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(1e6, VolumeUnit.LITRE),
                new Quantity<>(1e6, VolumeUnit.LITRE));
        assertEquals(new Quantity<>(2e6, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_SmallValues_Volume() {
        Quantity<VolumeUnit> result = Quantity.add(
                new Quantity<>(0.001, VolumeUnit.LITRE),
                new Quantity<>(0.002, VolumeUnit.LITRE));
        assertEquals(0.003, result.getValue(), EPSILON);
    }

    @Test
    public void testVolumeUnitEnum_LitreConstant() {
        assertEquals(1.0, VolumeUnit.LITRE.getConversionFactor(), EPSILON);
    }

    @Test
    public void testVolumeUnitEnum_MillilitreConstant() {
        assertEquals(0.001, VolumeUnit.MILLILITRE.getConversionFactor(), EPSILON);
    }

    @Test
    public void testVolumeUnitEnum_GallonConstant() {
        assertEquals(3.78541, VolumeUnit.GALLON.getConversionFactor(), EPSILON);
    }

    @Test
    public void testConvertToBaseUnit_LitreToLitre() {
        assertEquals(5.0, VolumeUnit.LITRE.convertToBaseUnit(5.0), EPSILON);
    }

    @Test
    public void testConvertToBaseUnit_MillilitreToLitre() {
        assertEquals(1.0, VolumeUnit.MILLILITRE.convertToBaseUnit(1000.0), EPSILON);
    }

    @Test
    public void testConvertToBaseUnit_GallonToLitre() {
        assertEquals(3.78541, VolumeUnit.GALLON.convertToBaseUnit(1.0), EPSILON);
    }

    @Test
    public void testConvertFromBaseUnit_LitreToLitre() {
        assertEquals(2.0, VolumeUnit.LITRE.convertFromBaseUnit(2.0), EPSILON);
    }

    @Test
    public void testConvertFromBaseUnit_LitreToMillilitre() {
        assertEquals(1000.0, VolumeUnit.MILLILITRE.convertFromBaseUnit(1.0), EPSILON);
    }

    @Test
    public void testConvertFromBaseUnit_LitreToGallon() {
        assertEquals(1.0, VolumeUnit.GALLON.convertFromBaseUnit(3.78541), 1e-4);
    }

    @Test
    public void testAddition_Commutativity_Volume() {
        Quantity<VolumeUnit> ab = Quantity.add(
                new Quantity<>(1.0, VolumeUnit.LITRE),
                new Quantity<>(1000.0, VolumeUnit.MILLILITRE),
                VolumeUnit.LITRE);
        Quantity<VolumeUnit> ba = Quantity.add(
                new Quantity<>(1000.0, VolumeUnit.MILLILITRE),
                new Quantity<>(1.0, VolumeUnit.LITRE),
                VolumeUnit.LITRE);
        assertTrue(ab.equals(ba));
    }

    @Test
    public void testHashCode_Volume_EqualObjects() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    // ===== UC12: Subtraction Tests =====

    @Test
    public void testSubtraction_SameUnit_FeetMinusFeet() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(5.0, LengthUnit.FEET));
        assertEquals(new Quantity<>(5.0, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_SameUnit_LitreMinusLitre() {
        Quantity<VolumeUnit> result = new Quantity<>(10.0, VolumeUnit.LITRE)
                .subtract(new Quantity<>(3.0, VolumeUnit.LITRE));
        assertEquals(new Quantity<>(7.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testSubtraction_CrossUnit_FeetMinusInches() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES));
        assertEquals(9.5, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    public void testSubtraction_CrossUnit_InchesMinusFeet() {
        Quantity<LengthUnit> result = new Quantity<>(120.0, LengthUnit.INCHES)
                .subtract(new Quantity<>(5.0, LengthUnit.FEET));
        assertEquals(60.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.INCHES, result.getUnit());
    }

    @Test
    public void testSubtraction_ExplicitTargetUnit_Feet() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES), LengthUnit.FEET);
        assertEquals(9.5, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_ExplicitTargetUnit_Inches() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES), LengthUnit.INCHES);
        assertEquals(114.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_ExplicitTargetUnit_Millilitre() {
        Quantity<VolumeUnit> result = new Quantity<>(5.0, VolumeUnit.LITRE)
                .subtract(new Quantity<>(2.0, VolumeUnit.LITRE), VolumeUnit.MILLILITRE);
        assertEquals(3000.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_ResultingInNegative() {
        Quantity<LengthUnit> result = new Quantity<>(5.0, LengthUnit.FEET)
                .subtract(new Quantity<>(10.0, LengthUnit.FEET));
        assertEquals(-5.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_ResultingInZero() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(120.0, LengthUnit.INCHES));
        assertEquals(0.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_WithZeroOperand() {
        Quantity<LengthUnit> result = new Quantity<>(5.0, LengthUnit.FEET)
                .subtract(new Quantity<>(0.0, LengthUnit.INCHES));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_WithNegativeValues() {
        Quantity<LengthUnit> result = new Quantity<>(5.0, LengthUnit.FEET)
                .subtract(new Quantity<>(-2.0, LengthUnit.FEET));
        assertEquals(7.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_NonCommutative() {
        Quantity<LengthUnit> ab = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(5.0, LengthUnit.FEET));
        Quantity<LengthUnit> ba = new Quantity<>(5.0, LengthUnit.FEET)
                .subtract(new Quantity<>(10.0, LengthUnit.FEET));
        assertEquals(5.0, ab.getValue(), EPSILON);
        assertEquals(-5.0, ba.getValue(), EPSILON);
        assertFalse(ab.equals(ba));
    }

    @Test
    public void testSubtraction_WithLargeValues() {
        Quantity<WeightUnit> result = new Quantity<>(1e6, WeightUnit.KILOGRAM)
                .subtract(new Quantity<>(5e5, WeightUnit.KILOGRAM));
        assertEquals(5e5, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_WithSmallValues() {
        Quantity<LengthUnit> result = new Quantity<>(0.001, LengthUnit.FEET)
                .subtract(new Quantity<>(0.0005, LengthUnit.FEET));
        assertEquals(0.0005, result.getValue(), 1e-6);
    }

    @Test
    public void testSubtraction_NullOperand() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(10.0, LengthUnit.FEET).subtract(null));
    }

    @Test
    public void testSubtraction_NullTargetUnit() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(5.0, LengthUnit.FEET), null));
    }

    // REPLACE WITH:
    @Test
    public void testSubtraction_CrossCategory() {
        Quantity<LengthUnit> length = new Quantity<>(10.0, LengthUnit.FEET);
        @SuppressWarnings("unchecked")
        Quantity<LengthUnit> weight = (Quantity<LengthUnit>)(Quantity<?>) new Quantity<>(5.0, WeightUnit.KILOGRAM);
        assertThrows(IllegalArgumentException.class,
                () -> length.subtract(weight));
    }

    @Test
    public void testSubtraction_AllMeasurementCategories() {
        // Length
        assertEquals(5.0, new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(5.0, LengthUnit.FEET)).getValue(), EPSILON);
        // Weight
        assertEquals(5.0, new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .subtract(new Quantity<>(5.0, WeightUnit.KILOGRAM)).getValue(), EPSILON);
        // Volume
        assertEquals(5.0, new Quantity<>(10.0, VolumeUnit.LITRE)
                .subtract(new Quantity<>(5.0, VolumeUnit.LITRE)).getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_ChainedOperations() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(2.0, LengthUnit.FEET))
                .subtract(new Quantity<>(1.0, LengthUnit.FEET));
        assertEquals(7.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_Immutability() {
        Quantity<LengthUnit> original = new Quantity<>(10.0, LengthUnit.FEET);
        original.subtract(new Quantity<>(3.0, LengthUnit.FEET));
        assertEquals(10.0, original.getValue(), EPSILON);
    }

    @Test
    public void testSubtraction_PrecisionAndRounding() {
        Quantity<WeightUnit> result = new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .subtract(new Quantity<>(5000.0, WeightUnit.GRAM));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtractionAddition_Inverse() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(3.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = Quantity.add(a, b).subtract(b);
        assertEquals(a.getValue(), result.getValue(), EPSILON);
    }

// ===== UC12: Division Tests =====

    @Test
    public void testDivision_SameUnit_FeetDividedByFeet() {
        double result = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(5.0, result, EPSILON);
    }

    @Test
    public void testDivision_SameUnit_LitreDividedByLitre() {
        double result = new Quantity<>(10.0, VolumeUnit.LITRE)
                .divide(new Quantity<>(5.0, VolumeUnit.LITRE));
        assertEquals(2.0, result, EPSILON);
    }

    @Test
    public void testDivision_CrossUnit_InchesDividedByFeet() {
        double result = new Quantity<>(24.0, LengthUnit.INCHES)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(1.0, result, EPSILON);
    }

    @Test
    public void testDivision_CrossUnit_KilogramDividedByGram() {
        double result = new Quantity<>(2.0, WeightUnit.KILOGRAM)
                .divide(new Quantity<>(2000.0, WeightUnit.GRAM));
        assertEquals(1.0, result, EPSILON);
    }

    // REPLACE WITH:
    @Test
    public void testDivision_CrossCategory() {
        Quantity<LengthUnit> length = new Quantity<>(10.0, LengthUnit.FEET);
        @SuppressWarnings("unchecked")
        Quantity<LengthUnit> weight = (Quantity<LengthUnit>)(Quantity<?>) new Quantity<>(5.0, WeightUnit.KILOGRAM);
        assertThrows(IllegalArgumentException.class,
                () -> length.divide(weight));
    }

    @Test
    public void testDivision_RatioLessThanOne() {
        double result = new Quantity<>(5.0, LengthUnit.FEET)
                .divide(new Quantity<>(10.0, LengthUnit.FEET));
        assertTrue(result < 1.0);
        assertEquals(0.5, result, EPSILON);
    }

    @Test
    public void testDivision_RatioEqualToOne() {
        double result = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(10.0, LengthUnit.FEET));
        assertEquals(1.0, result, EPSILON);
    }

    @Test
    public void testDivision_NonCommutative() {
        double ab = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(5.0, LengthUnit.FEET));
        double ba = new Quantity<>(5.0, LengthUnit.FEET)
                .divide(new Quantity<>(10.0, LengthUnit.FEET));
        assertEquals(2.0, ab, EPSILON);
        assertEquals(0.5, ba, EPSILON);
        assertNotEquals(ab, ba, EPSILON);
    }

    @Test
    public void testDivision_ByZero() {
        assertThrows(ArithmeticException.class,
                () -> new Quantity<>(10.0, LengthUnit.FEET)
                        .divide(new Quantity<>(0.0, LengthUnit.FEET)));
    }

    @Test
    public void testDivision_WithLargeRatio() {
        double result = new Quantity<>(1e6, WeightUnit.KILOGRAM)
                .divide(new Quantity<>(1.0, WeightUnit.KILOGRAM));
        assertEquals(1e6, result, EPSILON);
    }

    @Test
    public void testDivision_WithSmallRatio() {
        double result = new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .divide(new Quantity<>(1e6, WeightUnit.KILOGRAM));
        assertEquals(1e-6, result, 1e-12);
    }

    @Test
    public void testDivision_NullOperand() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(10.0, LengthUnit.FEET).divide(null));
    }


    @Test
    public void testDivision_AllMeasurementCategories() {
        assertEquals(2.0, new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(5.0, LengthUnit.FEET)), EPSILON);
        assertEquals(2.0, new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .divide(new Quantity<>(5.0, WeightUnit.KILOGRAM)), EPSILON);
        assertEquals(2.0, new Quantity<>(10.0, VolumeUnit.LITRE)
                .divide(new Quantity<>(5.0, VolumeUnit.LITRE)), EPSILON);
    }

    @Test
    public void testDivision_Immutability() {
        Quantity<LengthUnit> original = new Quantity<>(10.0, LengthUnit.FEET);
        original.divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(10.0, original.getValue(), EPSILON);
    }

    @Test
    public void testSubtractionAndDivision_Integration() {
        Quantity<LengthUnit> diff = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(4.0, LengthUnit.FEET));
        double ratio = diff.divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(3.0, ratio, EPSILON);
    }

    @Test
    public void testDivision_Associativity() {
        // (A ÷ B) ÷ C ≠ A ÷ (B ÷ C) → non-associative
        // (60 ÷ 6) ÷ 2 = 10 ÷ 2 = 5
        // 60 ÷ (6 ÷ 2) = 60 ÷ 3 = 20  → different
        double abc = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(5.0, LengthUnit.FEET));   // 2.0
        double bca = new Quantity<>(5.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));   // 2.5
        assertNotEquals(abc, bca, EPSILON);
    }

    // ===== UC13: Centralized Arithmetic Tests =====

    @Test
    public void testArithmeticOperation_Add_EnumComputation() {
        // Access via reflection not needed — test behavior through public API
        Quantity<LengthUnit> result = Quantity.add(
                new Quantity<>(7.0, LengthUnit.FEET),
                new Quantity<>(3.0, LengthUnit.FEET));
        assertEquals(10.0, result.getValue(), EPSILON);
    }

    @Test
    public void testArithmeticOperation_Subtract_EnumComputation() {
        Quantity<LengthUnit> result = new Quantity<>(7.0, LengthUnit.FEET)
                .subtract(new Quantity<>(3.0, LengthUnit.FEET));
        assertEquals(4.0, result.getValue(), EPSILON);
    }

    @Test
    public void testArithmeticOperation_Divide_EnumComputation() {
        double result = new Quantity<>(7.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(3.5, result, EPSILON);
    }

    @Test
    public void testValidation_NullOperand_ConsistentAcrossAllOperations() {
        Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
        // add
        assertThrows(IllegalArgumentException.class, () -> Quantity.add(q, null));
        // subtract
        assertThrows(IllegalArgumentException.class, () -> q.subtract(null));
        // divide
        assertThrows(IllegalArgumentException.class, () -> q.divide(null));
    }

    @Test
    public void testValidation_CrossCategory_ConsistentAcrossAllOperations() {
        Quantity<LengthUnit> length = new Quantity<>(10.0, LengthUnit.FEET);
        @SuppressWarnings("unchecked")
        Quantity<LengthUnit> weight = (Quantity<LengthUnit>)(Quantity<?>)
                new Quantity<>(5.0, WeightUnit.KILOGRAM);
        assertThrows(IllegalArgumentException.class, () -> Quantity.add(length, weight));
        assertThrows(IllegalArgumentException.class, () -> length.subtract(weight));
        assertThrows(IllegalArgumentException.class, () -> length.divide(weight));
    }

    @Test
    public void testValidation_NullTargetUnit_AddSubtractReject() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(5.0, LengthUnit.FEET);
        assertThrows(IllegalArgumentException.class,
                () -> Quantity.add(q1, q2, null));
        assertThrows(IllegalArgumentException.class,
                () -> q1.subtract(q2, null));
    }

    @Test
    public void testArithmeticOperation_DivideByZero_Throws() {
        assertThrows(ArithmeticException.class,
                () -> new Quantity<>(10.0, LengthUnit.FEET)
                        .divide(new Quantity<>(0.0, LengthUnit.FEET)));
    }

    @Test
    public void testRounding_AddSubtract_Consistent() {
        Quantity<WeightUnit> addResult = Quantity.add(
                new Quantity<>(10.0, WeightUnit.KILOGRAM),
                new Quantity<>(5000.0, WeightUnit.GRAM));
        assertEquals(15.0, addResult.getValue(), EPSILON);

        Quantity<WeightUnit> subResult = new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .subtract(new Quantity<>(5000.0, WeightUnit.GRAM));
        assertEquals(5.0, subResult.getValue(), EPSILON);
    }

    @Test
    public void testImmutability_AfterAllOperations_ViaCentralizedHelper() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(3.0, LengthUnit.FEET);
        Quantity.add(q1, q2);
        q1.subtract(q2);
        q1.divide(q2);
        assertEquals(10.0, q1.getValue(), EPSILON);
        assertEquals(3.0, q2.getValue(), EPSILON);
    }

    @Test
    public void testAllOperations_AcrossAllCategories_ViaHelper() {
        // Length
        assertEquals(15.0, Quantity.add(
                new Quantity<>(10.0, LengthUnit.FEET),
                new Quantity<>(5.0, LengthUnit.FEET)).getValue(), EPSILON);
        assertEquals(5.0, new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(5.0, LengthUnit.FEET)).getValue(), EPSILON);
        assertEquals(2.0, new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(5.0, LengthUnit.FEET)), EPSILON);
        // Weight
        assertEquals(15.0, Quantity.add(
                new Quantity<>(10.0, WeightUnit.KILOGRAM),
                new Quantity<>(5.0, WeightUnit.KILOGRAM)).getValue(), EPSILON);
        assertEquals(5.0, new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .subtract(new Quantity<>(5.0, WeightUnit.KILOGRAM)).getValue(), EPSILON);
        assertEquals(2.0, new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .divide(new Quantity<>(5.0, WeightUnit.KILOGRAM)), EPSILON);
        // Volume
        assertEquals(15.0, Quantity.add(
                new Quantity<>(10.0, VolumeUnit.LITRE),
                new Quantity<>(5.0, VolumeUnit.LITRE)).getValue(), EPSILON);
        assertEquals(5.0, new Quantity<>(10.0, VolumeUnit.LITRE)
                .subtract(new Quantity<>(5.0, VolumeUnit.LITRE)).getValue(), EPSILON);
        assertEquals(2.0, new Quantity<>(10.0, VolumeUnit.LITRE)
                .divide(new Quantity<>(5.0, VolumeUnit.LITRE)), EPSILON);
    }

    @Test
    public void testArithmetic_ChainedOperations_ViaHelper() {
        // (10ft + 2ft - 3ft) / 3ft = 3.0
        Quantity<LengthUnit> result = Quantity.add(
                        new Quantity<>(10.0, LengthUnit.FEET),
                        new Quantity<>(2.0, LengthUnit.FEET))
                .subtract(new Quantity<>(3.0, LengthUnit.FEET));
        double ratio = result.divide(new Quantity<>(3.0, LengthUnit.FEET));
        assertEquals(3.0, ratio, EPSILON);
    }

    @Test
    public void testImplicitTargetUnit_AddSubtract_UsesFirstOperandUnit() {
        Quantity<LengthUnit> addResult = Quantity.add(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(12.0, LengthUnit.INCHES));
        assertEquals(LengthUnit.FEET, addResult.getUnit());

        Quantity<LengthUnit> subResult = new Quantity<>(1.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES));
        assertEquals(LengthUnit.FEET, subResult.getUnit());
    }

    @Test
    public void testExplicitTargetUnit_OverridesImplicit() {
        Quantity<LengthUnit> result = Quantity.add(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(12.0, LengthUnit.INCHES),
                LengthUnit.INCHES);
        assertEquals(LengthUnit.INCHES, result.getUnit());
        assertEquals(24.0, result.getValue(), EPSILON);
    }

    @Test
    public void testSubtractionAddition_Inverse_ViaHelper() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(3.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = Quantity.add(a, b).subtract(b);
        assertEquals(a.getValue(), result.getValue(), EPSILON);
    }

    // ===== UC14: Temperature Tests =====

    @Test
    public void testTemperatureEquality_CelsiusToCelsius_SameValue() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testTemperatureEquality_FahrenheitToFahrenheit_SameValue() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        Quantity<TemperatureUnit> t2 = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testTemperatureEquality_CelsiusToFahrenheit_0C_32F() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testTemperatureEquality_CelsiusToFahrenheit_100C_212F() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(212.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testTemperatureEquality_EqualPoint_Minus40() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(-40.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(-40.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testTemperatureEquality_SymmetricProperty() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(212.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));
    }

    @Test
    public void testTemperatureEquality_ReflexiveProperty() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(37.0, TemperatureUnit.CELSIUS);
        assertTrue(t1.equals(t1));
    }

    @Test
    public void testTemperatureEquality_DifferentValues() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        assertFalse(t1.equals(t2));
    }

    @Test
    public void testTemperatureEquality_NullComparison() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        assertFalse(t1.equals(null));
    }

    @Test
    public void testTemperatureConversion_CelsiusToFahrenheit_0C() {
        Quantity<TemperatureUnit> result = new Quantity<>(0.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(32.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_CelsiusToFahrenheit_100C() {
        Quantity<TemperatureUnit> result = new Quantity<>(100.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_FahrenheitToCelsius_32F() {
        Quantity<TemperatureUnit> result = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(0.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_FahrenheitToCelsius_212F() {
        Quantity<TemperatureUnit> result = new Quantity<>(212.0, TemperatureUnit.FAHRENHEIT)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(100.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_CelsiusToFahrenheit_Negative40() {
        Quantity<TemperatureUnit> result = new Quantity<>(-40.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(-40.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_CelsiusToFahrenheit_50C() {
        Quantity<TemperatureUnit> result = new Quantity<>(50.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(122.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_SameUnit() {
        Quantity<TemperatureUnit> result = new Quantity<>(37.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(37.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_ZeroValue() {
        Quantity<TemperatureUnit> result = new Quantity<>(0.0, TemperatureUnit.FAHRENHEIT)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(-17.78, result.getValue(), 1e-2);
    }

    @Test
    public void testTemperatureConversion_NegativeValues() {
        Quantity<TemperatureUnit> result = new Quantity<>(-20.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(-4.0, result.getValue(), EPSILON);
    }

    @Test
    public void testTemperatureConversion_RoundTrip() {
        double original = 37.5;
        Quantity<TemperatureUnit> result = new Quantity<>(original, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(original, result.getValue(), 1e-4);
    }

    @Test
    public void testTemperatureConversion_LargeValue() {
        Quantity<TemperatureUnit> result = new Quantity<>(1000.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(1832.0, result.getValue(), EPSILON);
    }

// ===== UC14: Unsupported Operations =====

    @Test
    public void testTemperatureUnsupportedOperation_Add() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class,
                () -> Quantity.add(t1, t2));
    }

    @Test
    public void testTemperatureUnsupportedOperation_Subtract() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class,
                () -> t1.subtract(t2));
    }

    @Test
    public void testTemperatureUnsupportedOperation_Divide() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class,
                () -> t1.divide(t2));
    }

    @Test
    public void testTemperatureUnsupportedOperation_ErrorMessage() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class, () -> t1.subtract(t2));
        assertTrue(ex.getMessage().contains("Temperature"));
    }

// ===== UC14: Cross-Category Type Safety =====

    @Test
    public void testTemperatureVsLength_Incompatible() {
        Quantity<TemperatureUnit> temp = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<LengthUnit> length = new Quantity<>(100.0, LengthUnit.FEET);
        assertFalse(temp.equals(length));
    }

    @Test
    public void testTemperatureVsWeight_Incompatible() {
        Quantity<TemperatureUnit> temp = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        Quantity<WeightUnit> weight = new Quantity<>(50.0, WeightUnit.KILOGRAM);
        assertFalse(temp.equals(weight));
    }

    @Test
    public void testTemperatureVsVolume_Incompatible() {
        Quantity<TemperatureUnit> temp = new Quantity<>(25.0, TemperatureUnit.CELSIUS);
        Quantity<VolumeUnit> volume = new Quantity<>(25.0, VolumeUnit.LITRE);
        assertFalse(temp.equals(volume));
    }

// ===== UC14: IMeasurable Operation Support Methods =====

    @Test
    public void testOperationSupport_TemperatureUnit_ReturnsFalse() {
        assertFalse(TemperatureUnit.CELSIUS.supportsArithmetic());
        assertFalse(TemperatureUnit.FAHRENHEIT.supportsArithmetic());
    }

    @Test
    public void testOperationSupport_LengthUnit_ReturnsTrue() {
        assertTrue(LengthUnit.FEET.supportsArithmetic());
        assertTrue(LengthUnit.INCHES.supportsArithmetic());
    }

    @Test
    public void testOperationSupport_WeightUnit_ReturnsTrue() {
        assertTrue(WeightUnit.KILOGRAM.supportsArithmetic());
    }

    @Test
    public void testOperationSupport_VolumeUnit_ReturnsTrue() {
        assertTrue(VolumeUnit.LITRE.supportsArithmetic());
    }

    @Test
    public void testTemperatureValidateOperationSupport_ThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> TemperatureUnit.CELSIUS.validateOperationSupport("ADD"));
    }

    @Test
    public void testLengthValidateOperationSupport_NoException() {
        // Default no-op — should not throw
        assertDoesNotThrow(() -> LengthUnit.FEET.validateOperationSupport("ADD"));
    }

    @Test
    public void testTemperatureUnit_NullUnitValidation() {
        assertThrows(NullPointerException.class,
                () -> new Quantity<>(100.0, (TemperatureUnit) null));
    }

    @Test
    public void testTemperatureUnit_UnitNames() {
        assertEquals("CELSIUS", TemperatureUnit.CELSIUS.getUnitName());
        assertEquals("FAHRENHEIT", TemperatureUnit.FAHRENHEIT.getUnitName());
    }

    @Test
    public void testTemperatureUnit_ImplementsIMeasurable() {
        IMeasurable unit = TemperatureUnit.CELSIUS;
        assertNotNull(unit);
        assertEquals("CELSIUS", unit.getUnitName());
    }

    @Test
    public void testBackwardCompatibility_AllPreviousOperationsUnchanged() {
        // Length still fully functional
        assertTrue(new Quantity<>(1.0, LengthUnit.FEET)
                .equals(new Quantity<>(12.0, LengthUnit.INCHES)));
        assertEquals(3.0, Quantity.add(
                new Quantity<>(1.0, LengthUnit.FEET),
                new Quantity<>(2.0, LengthUnit.FEET)).getValue(), EPSILON);
        // Weight still fully functional
        assertTrue(new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .equals(new Quantity<>(1000.0, WeightUnit.GRAM)));
        // Volume still fully functional
        assertTrue(new Quantity<>(1.0, VolumeUnit.LITRE)
                .equals(new Quantity<>(1000.0, VolumeUnit.MILLILITRE)));
    }

    // ===== UC15: N-Tier Architecture Tests =====

    private IQuantityMeasurementService service;
    private List<QuantityMeasurementEntity> testStore;

    @BeforeEach
    void setUpService() {
        testStore = new ArrayList<>();
        IQuantityMeasurementRepository testRepo = new IQuantityMeasurementRepository() {
            @Override public void save(QuantityMeasurementEntity e) { testStore.add(e); }
            @Override public List<QuantityMeasurementEntity> getAllMeasurements() { return testStore; }
            @Override public void clearAll() { testStore.clear(); }
        };
        service = new QuantityMeasurementServiceImpl(testRepo);
    }

// --- QuantityDTO Tests ---

    @Test
    public void testQuantityDTO_LengthUnit_Creation() {
        QuantityDTO dto = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        assertEquals(1.0, dto.getValue(), EPSILON);
        assertEquals("FEET", dto.getUnit().getUnitName());
        assertEquals("LENGTH", dto.getUnit().getMeasurementType());
    }

    @Test
    public void testQuantityDTO_WeightUnit_Creation() {
        QuantityDTO dto = new QuantityDTO(500.0, QuantityDTO.WeightUnit.GRAM);
        assertEquals(500.0, dto.getValue(), EPSILON);
        assertEquals("GRAM", dto.getUnit().getUnitName());
        assertEquals("WEIGHT", dto.getUnit().getMeasurementType());
    }

    @Test
    public void testQuantityDTO_TemperatureUnit_Creation() {
        QuantityDTO dto = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        assertEquals("TEMPERATURE", dto.getUnit().getMeasurementType());
        assertEquals("CELSIUS", dto.getUnit().getUnitName());
    }

    @Test
    public void testQuantityDTO_ToString() {
        QuantityDTO dto = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        assertEquals("1.0 FEET", dto.toString());
    }

// --- QuantityModel Tests ---

    @Test
    public void testQuantityModel_Creation() {
        QuantityModel<LengthUnit> model = new QuantityModel<>(1.0, LengthUnit.FEET);
        assertEquals(1.0, model.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, model.getUnit());
    }

    @Test
    public void testQuantityModel_Weight_Creation() {
        QuantityModel<WeightUnit> model = new QuantityModel<>(1000.0, WeightUnit.GRAM);
        assertEquals(1000.0, model.getValue(), EPSILON);
        assertEquals(WeightUnit.GRAM, model.getUnit());
    }

// --- QuantityMeasurementEntity Tests ---

    @Test
    public void testQuantityMeasurementEntity_BinaryConstruction() {
        QuantityDTO op1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO op2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                op1, op2, "COMPARE", "true");
        assertEquals("COMPARE", entity.getOperation());
        assertEquals("true", entity.getResult());
        assertFalse(entity.hasError());
    }

    @Test
    public void testQuantityMeasurementEntity_ErrorConstruction() {
        QuantityDTO op1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                op1, null, "DIVIDE", "Division by zero", true);
        assertTrue(entity.hasError());
        assertEquals("Division by zero", entity.getErrorMessage());
    }

    @Test
    public void testQuantityMeasurementEntity_SingleOperandConstruction() {
        QuantityDTO op1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                op1, "CONVERT", "12.0 INCHES");
        assertEquals("CONVERT", entity.getOperation());
        assertNull(entity.getOperand2());
        assertFalse(entity.hasError());
    }

// --- Service Layer Tests ---

    @Test
    public void testService_CompareEquality_SameUnit() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        assertTrue(service.compareEquality(q1, q2));
    }

    @Test
    public void testService_CompareEquality_CrossUnit_True() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        assertTrue(service.compareEquality(q1, q2));
    }

    @Test
    public void testService_CompareEquality_CrossUnit_False() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(5.0, QuantityDTO.LengthUnit.INCHES);
        assertFalse(service.compareEquality(q1, q2));
    }

    @Test
    public void testService_CompareEquality_CrossCategory_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        assertThrows(QuantityMeasurementException.class,
                () -> service.compareEquality(q1, q2));
    }

    @Test
    public void testService_CompareEquality_NullOperand_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        assertThrows(QuantityMeasurementException.class,
                () -> service.compareEquality(q1, null));
    }

    @Test
    public void testService_Convert_FeetToInches() {
        QuantityDTO input = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO result = service.convert(input, QuantityDTO.LengthUnit.INCHES);
        assertEquals(12.0, result.getValue(), EPSILON);
        assertEquals("INCHES", result.getUnit().getUnitName());
    }

    @Test
    public void testService_Convert_KilogramToGram() {
        QuantityDTO input = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        QuantityDTO result = service.convert(input, QuantityDTO.WeightUnit.GRAM);
        assertEquals(1000.0, result.getValue(), EPSILON);
    }

    @Test
    public void testService_Convert_CelsiusToFahrenheit() {
        QuantityDTO input = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO result = service.convert(input, QuantityDTO.TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, result.getValue(), EPSILON);
    }

    @Test
    public void testService_Convert_CrossUnitType_ThrowsException() {
        QuantityDTO input = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        assertThrows(QuantityMeasurementException.class,
                () -> service.convert(input, QuantityDTO.WeightUnit.KILOGRAM));
    }

    @Test
    public void testService_Add_SameUnit_Length() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO result = service.add(q1, q2, QuantityDTO.LengthUnit.FEET);
        assertEquals(3.0, result.getValue(), EPSILON);
    }

    @Test
    public void testService_Add_CrossUnit_Length() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO result = service.add(q1, q2, QuantityDTO.LengthUnit.FEET);
        assertEquals(2.0, result.getValue(), EPSILON);
    }

    @Test
    public void testService_Add_Weight() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        QuantityDTO q2 = new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM);
        QuantityDTO result = service.add(q1, q2, QuantityDTO.WeightUnit.KILOGRAM);
        assertEquals(2.0, result.getValue(), EPSILON);
    }

    @Test
    public void testService_Add_Temperature_ThrowsUnsupported() {
        QuantityDTO q1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO q2 = new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS);
        assertThrows(QuantityMeasurementException.class,
                () -> service.add(q1, q2, QuantityDTO.TemperatureUnit.CELSIUS));
    }

    @Test
    public void testService_Subtract_Length() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(6.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO result = service.subtract(q1, q2, QuantityDTO.LengthUnit.FEET);
        assertEquals(9.5, result.getValue(), EPSILON);
    }

    @Test
    public void testService_Subtract_Weight() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM);
        QuantityDTO q2 = new QuantityDTO(5000.0, QuantityDTO.WeightUnit.GRAM);
        QuantityDTO result = service.subtract(q1, q2, QuantityDTO.WeightUnit.KILOGRAM);
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    public void testService_Divide_Length() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        assertEquals(5.0, service.divide(q1, q2), EPSILON);
    }

    @Test
    public void testService_Divide_ByZero_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(0.0, QuantityDTO.LengthUnit.FEET);
        assertThrows(QuantityMeasurementException.class,
                () -> service.divide(q1, q2));
    }

    @Test
    public void testService_OperationSaved_ToRepository() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        service.compareEquality(q1, q2);
        assertEquals(1, testStore.size());
        assertEquals("COMPARE", testStore.get(0).getOperation());
    }

// --- Controller Tests ---

    @Test
    public void testController_NullService_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuantityMeasurementController(null));
    }

    @Test
    public void testController_PerformEquality_DoesNotThrow() {
        QuantityMeasurementController controller = new QuantityMeasurementController(service);
        assertDoesNotThrow(() -> controller.performEquality(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test
    public void testController_PerformConversion_DoesNotThrow() {
        QuantityMeasurementController controller = new QuantityMeasurementController(service);
        assertDoesNotThrow(() -> controller.performConversion(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                QuantityDTO.LengthUnit.INCHES));
    }

    @Test
    public void testController_PerformAddition_ErrorHandled_DoesNotThrow() {
        QuantityMeasurementController controller = new QuantityMeasurementController(service);
        // Temperature addition is unsupported — controller should handle gracefully
        assertDoesNotThrow(() -> controller.performAddition(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS),
                QuantityDTO.TemperatureUnit.CELSIUS));
    }

    @Test
    public void testController_PerformDivision_DoesNotThrow() {
        QuantityMeasurementController controller = new QuantityMeasurementController(service);
        assertDoesNotThrow(() -> controller.performDivision(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET)));
    }

// --- IMeasurable getMeasurementType Tests ---

    @Test
    public void testIMeasurable_LengthUnit_MeasurementType() {
        assertEquals("LENGTH", LengthUnit.FEET.getMeasurementType());
        assertEquals("LENGTH", LengthUnit.INCHES.getMeasurementType());
    }

    @Test
    public void testIMeasurable_WeightUnit_MeasurementType() {
        assertEquals("WEIGHT", WeightUnit.KILOGRAM.getMeasurementType());
    }

    @Test
    public void testIMeasurable_VolumeUnit_MeasurementType() {
        assertEquals("VOLUME", VolumeUnit.LITRE.getMeasurementType());
    }

    @Test
    public void testIMeasurable_TemperatureUnit_MeasurementType() {
        assertEquals("TEMPERATURE", TemperatureUnit.CELSIUS.getMeasurementType());
    }

    @Test
    public void testBackwardCompatibility_UC1ToUC14_AllCategoriesUnchanged() {
        // Length
        assertTrue(new Quantity<>(1.0, LengthUnit.FEET)
                .equals(new Quantity<>(12.0, LengthUnit.INCHES)));
        // Weight
        assertTrue(new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .equals(new Quantity<>(1000.0, WeightUnit.GRAM)));
        // Volume
        assertTrue(new Quantity<>(1.0, VolumeUnit.LITRE)
                .equals(new Quantity<>(1000.0, VolumeUnit.MILLILITRE)));
        // Temperature
        assertTrue(new Quantity<>(0.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT)));
    }
}