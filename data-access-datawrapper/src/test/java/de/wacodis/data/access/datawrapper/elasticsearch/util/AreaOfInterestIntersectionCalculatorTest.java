/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

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
    public void testCalculateIntersection_Extent() {
        List<Float> intersection = AreaOfInterestIntersectionCalculator.calculateIntersection(aoi1, aoi2).getExtent();

        assertEquals(5.0f, intersection.get(0), delta);
        assertEquals(5.0f, intersection.get(1), delta);
        assertEquals(10.0f, intersection.get(2), delta);
        assertEquals(10.0f, intersection.get(3), delta);
    }

    @Test
    public void testCalculateIntersection_Footprint() throws ParseException {
        //0 0 , 0 10, 10 10, 10 0, 0 0
        String footprint = "{\r\n    \"type\": \"Polygon\",\r\n    \"coordinates\": [\r\n        [\r\n            [\r\n                0,\r\n                0\r\n            ],\r\n            [\r\n                0,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                0\r\n            ],\r\n            [\r\n                0,\r\n                0\r\n            ]\r\n        ]\r\n    ]\r\n}";
        GeoJsonReader jsonReader = new GeoJsonReader();
        Geometry footprintGeom = jsonReader.read(footprint);

        Geometry intersection = AreaOfInterestIntersectionCalculator.calculateIntersection(aoi1, footprintGeom);

        Coordinate[] coords = intersection.getCoordinates();

        assertEquals(0, coords[0].x, delta);
        assertEquals(0, coords[0].y, delta);

        assertEquals(0, coords[1].x, delta);
        assertEquals(10, coords[1].y, delta);

        assertEquals(10, coords[2].x, delta);
        assertEquals(10, coords[2].y, delta);

        assertEquals(10, coords[3].x, delta);
        assertEquals(0, coords[3].y, delta);

        assertEquals(0, coords[4].x, delta);
        assertEquals(0, coords[4].y, delta);
    }

    /**
     * Test of intersects method, of class AreaOfInterestIntersectionCalculator.
     */
    @Test
    public void testIntersects_Extent() {
        assertTrue(AreaOfInterestIntersectionCalculator.intersects(aoi1, aoi2));
        assertFalse(AreaOfInterestIntersectionCalculator.intersects(aoi2, aoi3));

        //touches (not disjoint)
        List<Float> extent3 = new ArrayList<>();
        extent3.add(15.0f);
        extent3.add(15.0f);
        extent3.add(20.0f);
        extent3.add(20.0f);
        aoi3.setExtent(extent3);
        assertTrue(AreaOfInterestIntersectionCalculator.intersects(aoi2, aoi3));
    }

    @Test
    public void testCalculateIntersects_Footprint() throws ParseException {
        //10 10, 10 20, 20 20 , 20 10, 10 10
        String footprint = "{\r\n    \"type\": \"Polygon\",\r\n    \"coordinates\": [\r\n        [\r\n            [\r\n                10,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                20\r\n            ],\r\n            [\r\n                20,\r\n                20\r\n            ],\r\n            [\r\n                20,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                10\r\n            ]\r\n        ]\r\n    ]\r\n}";
        GeoJsonReader jsonReader = new GeoJsonReader();
        Geometry footprintGeom = jsonReader.read(footprint);

        assertTrue(AreaOfInterestIntersectionCalculator.intersects(aoi1, footprintGeom));
    }

    /**
     * Test of calculateArea method, of class
     * AreaOfInterestIntersectionCalculator.
     */
    @Test
    public void testCalculateArea_Extent() {
        assertEquals(100.0f, AreaOfInterestIntersectionCalculator.calculateArea(aoi1), delta);
    }

    /**
     * Test of calculateOverlapPercentage method, of class
     * AreaOfInterestIntersectionCalculator.
     */
    @Test
    public void testCalculateOverlapPercentage_Extent() {
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

    @Test
    public void testCalculateOverlapPercentage_Footprint_NoOverlap() throws ParseException {
        //10 10, 10 20, 20 20 , 20 10, 10 10
        String footprint = "{\r\n    \"type\": \"Polygon\",\r\n    \"coordinates\": [\r\n        [\r\n            [\r\n                10,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                20\r\n            ],\r\n            [\r\n                20,\r\n                20\r\n            ],\r\n            [\r\n                20,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                10\r\n            ]\r\n        ]\r\n    ]\r\n}";
        GeoJsonReader jsonReader = new GeoJsonReader();
        Geometry footprintGeom = jsonReader.read(footprint);

        assertEquals(0.0f, AreaOfInterestIntersectionCalculator.calculateOverlapPercentage(aoi1, footprintGeom), delta);
    }

    @Test
    public void testCalculateOverlapPercentage_Footprint_FullOverlap() throws ParseException {
        //0 0 , 0 10, 10 10, 10 0, 0 0
        String footprint = "{\r\n    \"type\": \"Polygon\",\r\n    \"coordinates\": [\r\n        [\r\n            [\r\n                0,\r\n                0\r\n            ],\r\n            [\r\n                0,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                0\r\n            ],\r\n            [\r\n                0,\r\n                0\r\n            ]\r\n        ]\r\n    ]\r\n}";
        GeoJsonReader jsonReader = new GeoJsonReader();
        Geometry footprintGeom = jsonReader.read(footprint);

        assertEquals(100.0f, AreaOfInterestIntersectionCalculator.calculateOverlapPercentage(aoi1, footprintGeom), delta);
    }
    
        @Test
    public void testCalculateOverlapPercentage_Footprint_PartialOverlap() throws ParseException {
        //0 5 , 0 10, 10 10, 10 5, 0 5
        String footprint = "{\r\n    \"type\": \"Polygon\",\r\n    \"coordinates\": [\r\n        [\r\n            [\r\n                0,\r\n                5\r\n            ],\r\n            [\r\n                0,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                5\r\n            ],\r\n            [\r\n                0,\r\n                5\r\n            ]\r\n        ]\r\n    ]\r\n}";
        GeoJsonReader jsonReader = new GeoJsonReader();
        Geometry footprintGeom = jsonReader.read(footprint);

        assertEquals(50.0f, AreaOfInterestIntersectionCalculator.calculateOverlapPercentage(aoi1, footprintGeom), delta);
    }

}
