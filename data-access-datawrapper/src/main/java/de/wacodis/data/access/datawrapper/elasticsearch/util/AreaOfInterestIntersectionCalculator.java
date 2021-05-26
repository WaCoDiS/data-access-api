/*
 * Copyright 2018-2021 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import java.util.Arrays;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 *
 * @author Arne
 */
public class AreaOfInterestIntersectionCalculator {

    public static AbstractDataEnvelopeAreaOfInterest calculateIntersection(AbstractDataEnvelopeAreaOfInterest aoi1, AbstractDataEnvelopeAreaOfInterest aoi2) {
        Float[] extent1 = aoi1.getExtent().toArray(new Float[0]);
        Float[] extent2 = aoi2.getExtent().toArray(new Float[0]);
        Float[] intersection = new Float[4];
        Arrays.fill(intersection, 0.0f);

        if (intersects(aoi1, aoi2)) {
            if (extent1[0] > extent2[0]) {
                intersection[0] = extent1[0];
            } else {
                intersection[0] = extent2[0];
            }
            if (extent1[1] > extent2[1]) {
                intersection[1] = extent1[1];
            } else {
                intersection[1] = extent2[1];
            }
            if (extent1[2] < extent2[2]) {
                intersection[2] = extent1[2];
            } else {
                intersection[2] = extent2[2];
            }
            if (extent1[3] < extent2[3]) {
                intersection[3] = extent1[3];
            } else {
                intersection[3] = extent2[3];
            }
        }

        AbstractDataEnvelopeAreaOfInterest aoiIntersect = new AbstractDataEnvelopeAreaOfInterest();
        aoiIntersect.extent(Arrays.asList(intersection));
        return aoiIntersect;
    }

    public static Geometry calculateIntersection(AbstractDataEnvelopeAreaOfInterest aoi, Geometry geom) {
        Geometry aoiGeom = getAOIasGeometry(aoi);

        //calculate intersection
        Geometry intersection = geom.intersection(aoiGeom);

        return intersection;
    }

    public static boolean intersects(AbstractDataEnvelopeAreaOfInterest aoi1, AbstractDataEnvelopeAreaOfInterest aoi2) {
        Float[] extent1 = aoi1.getExtent().toArray(new Float[0]);
        Float[] extent2 = aoi2.getExtent().toArray(new Float[0]);

        return extent1[0] <= extent2[2]
                && extent1[2] >= extent2[0]
                && extent1[1] <= extent2[3]
                && extent1[3] >= extent2[1];
    }
    
    public static boolean intersects(AbstractDataEnvelopeAreaOfInterest aoi, Geometry geom){
        Geometry aoiGeom = getAOIasGeometry(aoi);
        
        return geom.intersects(aoiGeom);
    }

    /**
     * calculates the area of an area of interest by a*b, does not respect
     * curvature of earth
     *
     * @param aoi
     * @return
     */
    public static double calculateArea(AbstractDataEnvelopeAreaOfInterest aoi) {
        Float[] extent = aoi.getExtent().toArray(new Float[0]);

        double a = Math.abs(extent[0] - extent[2]);
        double b = Math.abs(extent[1] - extent[3]);
        return a * b;
    }

    /**
     * calculates the area of intersection relative to the area of refernece
     *
     * @param aoiReference
     * @param aoiOther
     * @return value between 0.0 and 100.0 (percentage)
     */
    public static float calculateOverlapPercentage(AbstractDataEnvelopeAreaOfInterest aoiReference, AbstractDataEnvelopeAreaOfInterest aoiOther) {
        if (intersects(aoiReference, aoiOther)) {
            AbstractDataEnvelopeAreaOfInterest intersection = calculateIntersection(aoiReference, aoiOther);
            double areaIntersection = calculateArea(intersection);
            double areaReference = calculateArea(aoiReference);
            float overlapPercentage = (float) ((areaIntersection/ areaReference) * 100);

            assert (overlapPercentage <= 100.0f && overlapPercentage >= 0.0f);

            return overlapPercentage;
        } else {
            return 0.0f;
        }
    }

    /**
     * calculates the area of intersection relative to the area of refernece
     *
     * @param aoiReference
     * @param geomOther
     * @return value between 0.0 and 100.0 (percentage)
     */
    public static float calculateOverlapPercentage(AbstractDataEnvelopeAreaOfInterest aoiReference, Geometry geomOther) {
        Geometry intersection = calculateIntersection(aoiReference, geomOther);
        double areaIntersection = intersection.getArea();
        double areaReference = calculateArea(aoiReference);

        float overlapPercentage = (float) ((areaIntersection/ areaReference) * 100);

        assert (overlapPercentage <= 100.0f && overlapPercentage >= 0.0f);

        return overlapPercentage;
    }

    private static Geometry getAOIasGeometry(AbstractDataEnvelopeAreaOfInterest aoi) {
        //from geojson bbox: minLon, minLat, maxLon, maxLat
        //to Envelope: x1,x2,y1,y2
        Envelope aoiEnv = new Envelope(aoi.getExtent().get(0), aoi.getExtent().get(2), aoi.getExtent().get(1), aoi.getExtent().get(3));
        Geometry aoiGeom = new GeometryFactory().toGeometry(aoiEnv);
        
        return aoiGeom;
    }

}
