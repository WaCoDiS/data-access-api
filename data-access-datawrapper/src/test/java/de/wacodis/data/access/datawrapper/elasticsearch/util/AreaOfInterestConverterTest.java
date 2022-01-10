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
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class AreaOfInterestConverterTest {

    @Test
    public void testTestGeoshapeAreaOfInterest() {
        AbstractDataEnvelopeAreaOfInterest defaultAOI = new AbstractDataEnvelopeAreaOfInterest();
        List<Float> coordinates = new ArrayList<>();
        coordinates.addAll(Arrays.asList(-180.0f, -90.0f, 180.0f, 90.0f));
        defaultAOI.setExtent(coordinates);
        
        GeoShapeCompatibilityAreaOfInterest geoshapeAOI = AreaOfInterestConverter.getGeoshapeAreaOfInterest(defaultAOI);
     
        assertAll(
                () -> assertEquals(geoshapeAOI.getType(), GeoShapeCompatibilityAreaOfInterest.GeoShapeType.ENVELOPE),
                () ->  assertEquals(2, geoshapeAOI.getCoordinates().size()),
                () -> assertArrayEquals(new Float[]{-180.0f, 90.0f}, geoshapeAOI.getCoordinates().get(0).toArray(new Float[]{})),
                () -> assertArrayEquals(new Float[]{180.0f, -90.0f}, geoshapeAOI.getCoordinates().get(1).toArray(new Float[]{}))
        );
    }

    
    @Test
    public void testGetDefaultAreaOfInterest(){
        GeoShapeCompatibilityAreaOfInterest geoshapeAOI = new GeoShapeCompatibilityAreaOfInterest();
        geoshapeAOI.setType(GeoShapeCompatibilityAreaOfInterest.GeoShapeType.ENVELOPE);    
        List<List<Float>> coordinates = new ArrayList<>();
        coordinates.add(Arrays.asList(-180.0f, 90.0f));
        coordinates.add(Arrays.asList(180.0f, -90.0f));
        geoshapeAOI.setCoordinates(coordinates);

        AbstractDataEnvelopeAreaOfInterest defaultAOI = AreaOfInterestConverter.getDefaultAreaOfInterest(geoshapeAOI);
        
        assertArrayEquals(new Float[]{-180.0f, -90.0f, 180.0f, 90.0f}, defaultAOI.getExtent().toArray(new Float[]{}));
    }
    
    
}
