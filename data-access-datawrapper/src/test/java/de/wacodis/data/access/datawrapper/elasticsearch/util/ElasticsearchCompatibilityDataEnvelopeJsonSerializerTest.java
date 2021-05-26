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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import java.io.IOException;
import org.joda.time.DateTime;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchCompatibilityDataEnvelopeJsonSerializerTest {

    public ElasticsearchCompatibilityDataEnvelopeJsonSerializerTest() {
    }


    @Test
    public void testSerialize() throws IOException {
        CopernicusDataEnvelope envelope = new CopernicusDataEnvelope();
        envelope.setCloudCoverage(20.0f);
        envelope.setDatasetId("XYZ");
        envelope.setPortal(CopernicusDataEnvelope.PortalEnum.CODE_DE);
        envelope.setSatellite(CopernicusDataEnvelope.SatelliteEnum._1);
        envelope.setCreated(DateTime.now());
        envelope.setModified(DateTime.now());
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);

        AbstractDataEnvelopeTimeFrame timeFrame = new AbstractDataEnvelopeTimeFrame();
        timeFrame.setEndTime(DateTime.now().plusHours(2));
        timeFrame.setStartTime(DateTime.now());
        envelope.setTimeFrame(timeFrame);

        GeoShapeCompatibilityAreaOfInterest aoi = new GeoShapeCompatibilityAreaOfInterest();
        aoi.setType(GeoShapeCompatibilityAreaOfInterest.GeoShapeType.ENVELOPE);
        List<List<Float>> coordinates = new ArrayList<>();
        List<Float> coord1 = new ArrayList<>();
        coord1.add(0.0f);
        coord1.add(1.0f);
        List<Float> coord2 = new ArrayList<>();
        coord2.add(2.0f);
        coord2.add(0.0f);
        coordinates.add(coord1);
        coordinates.add(coord2);
        aoi.setCoordinates(coordinates);
        envelope.setAreaOfInterest(aoi);

        ElasticsearchCompatibilityDataEnvelopeSerializer serializer = new ElasticsearchCompatibilityDataEnvelopeSerializer();
        Map<String, Object> result = serializer.serialize(envelope);
        
        assertEquals(envelope.getCloudCoverage().doubleValue(), result.get("cloudCoverage"));
    }

}
