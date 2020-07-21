/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Arne
 */
public class AreaOfInterestIntersectionCalculatorTest {

    private AbstractDataEnvelopeAreaOfInterest aoi1;
    private AbstractDataEnvelopeAreaOfInterest aoi2;
    private AbstractDataEnvelopeAreaOfInterest aoi3;

    float delta = 0.001f;

    @Before
    public void setUp() {
        aoi1 = new AbstractDataEnvelopeAreaOfInterest();
        aoi2 = new AbstractDataEnvelopeAreaOfInterest();
        aoi3 = new AbstractDataEnvelopeAreaOfInterest();

        List<Float> extent1 = new ArrayList<>();
        extent1.add(0.0f);
        extent1.add(0.0f);
        extent1.add(10.0f);
        extent1.add(10.0f);
        aoi1.setExtent(extent1);

        List<Float> extent2 = new ArrayList<>();
        extent2.add(5.0f);
        extent2.add(5.0f);
        extent2.add(15.0f);
        extent2.add(15.0f);
        aoi2.setExtent(extent2);

        List<Float> extent3 = new ArrayList<>();
        extent3.add(20.0f);
        extent3.add(20.0f);
        extent3.add(25.0f);
        extent3.add(25.0f);
        aoi3.setExtent(extent3);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of calculateIntersection method, of class
     * AreaOfInterestIntersectionCalculator.
     */
    @Test
    public void testCalculateIntersection() {
        List<Float> intersection = AreaOfInterestIntersectionCalculator.calculateIntersection(aoi1, aoi2).getExtent();
        
        assertEquals(5.0f, intersection.get(0), delta);
        assertEquals(5.0f, intersection.get(1), delta);
        assertEquals(10.0f, intersection.get(2), delta);
        assertEquals(10.0f, intersection.get(3), delta);
    }

    /**
     * Test of intersects method, of class AreaOfInterestIntersectionCalculator.
     */
    @Test
    public void testIntersects() {
        assertTrue(AreaOfInterestIntersectionCalculator.intersects(aoi1, aoi2));
        assertFalse(AreaOfInterestIntersectionCalculator.intersects(aoi2, aoi3));
    }

    /**
     * Test of calculateArea method, of class
     * AreaOfInterestIntersectionCalculator.
     */
    @Test
    public void testCalculateArea() {
        assertEquals(100.0f, AreaOfInterestIntersectionCalculator.calculateArea(aoi1), delta);
    }

    /**
     * Test of calculateOverlapPercentage method, of class
     * AreaOfInterestIntersectionCalculator.
     */
    @Test
    public void testCalculateOverlapPercentage() {
        assertEquals(100.0f, AreaOfInterestIntersectionCalculator.calculateOverlapPercentage(aoi1, aoi1), delta);
        assertEquals(0.0f, AreaOfInterestIntersectionCalculator.calculateOverlapPercentage(aoi2, aoi3), delta);
       
        List<Float> extent2 = new ArrayList<>();
        extent2.add(5.0f);
        extent2.add(0.0f);
        extent2.add(10.0f);
        extent2.add(10.0f);
        aoi2.setExtent(extent2);
        assertEquals(50.0f, AreaOfInterestIntersectionCalculator.calculateOverlapPercentage(aoi1, aoi2), delta);
    }

}
