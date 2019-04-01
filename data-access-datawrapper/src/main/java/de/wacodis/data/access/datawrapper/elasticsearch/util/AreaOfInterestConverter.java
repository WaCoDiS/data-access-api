/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
