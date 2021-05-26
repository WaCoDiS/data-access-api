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
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class AreaOfInterestConverter {

    public static GeoShapeCompatibilityAreaOfInterest getGeoshapeAreaOfInterest(AbstractDataEnvelopeAreaOfInterest defaultAreaOfInterest) {
        GeoShapeCompatibilityAreaOfInterest geoshapeAreaOfInterest = new GeoShapeCompatibilityAreaOfInterest();

        // TODO: is this a good idea on handling envelopes without geom?
        if (defaultAreaOfInterest == null) {
            return null;
        }
        
        Float[] bbox = defaultAreaOfInterest.getExtent().toArray(new Float[0]); //geojson bbox format [minLon, minLat, maxLon, maxLat]
        List<Float> topLeft = Arrays.asList(new Float[]{bbox[0], bbox[3]});
        List<Float> bottomRight = Arrays.asList(new Float[]{bbox[2], bbox[1]});

        List<List<Float>> geoshapeEnvelope = new ArrayList<>();
        geoshapeEnvelope.add(topLeft);
        geoshapeEnvelope.add(bottomRight);

        geoshapeAreaOfInterest.setCoordinates(geoshapeEnvelope);
        geoshapeAreaOfInterest.setType(GeoShapeCompatibilityAreaOfInterest.GeoShapeType.ENVELOPE);

        return geoshapeAreaOfInterest;
    }

    public static AbstractDataEnvelopeAreaOfInterest getDefaultAreaOfInterest(GeoShapeCompatibilityAreaOfInterest areaOfInterest) {
        GeoShapeCompatibilityAreaOfInterest geoShapeAreaOfInterest = (GeoShapeCompatibilityAreaOfInterest) areaOfInterest;

        // TODO: is this a good idea on handling envelopes without geom?
        if (geoShapeAreaOfInterest == null) {
            return null;
        }
        
        if (!geoShapeAreaOfInterest.getType().equals(GeoShapeCompatibilityAreaOfInterest.GeoShapeType.ENVELOPE)) {
            throw new IllegalArgumentException("could not convert AreaOfInterest, expected input of type: " + GeoShapeCompatibilityAreaOfInterest.GeoShapeType.ENVELOPE.toString() + " got: " + areaOfInterest.getType().toString());
        }

        AbstractDataEnvelopeAreaOfInterest defaultAreaOfInterest = new AbstractDataEnvelopeAreaOfInterest();
        Float[] extent = new Float[4]; //[minLon, minLat, maxLon, maxLat]
        List<Float> coord1 = geoShapeAreaOfInterest.getCoordinates().get(0); //topLeft (lon, lat)
        List<Float> coord2 = geoShapeAreaOfInterest.getCoordinates().get(1); //bottomRight (lon, lat)

        extent[0] = coord1.get(0); //minLon
        extent[1] = coord2.get(1); //minLat
        extent[2] = coord2.get(0); //maxLon
        extent[3] = coord1.get(1); //maxLat

        defaultAreaOfInterest.setExtent(Arrays.asList(extent));
        return defaultAreaOfInterest;

    }

}
