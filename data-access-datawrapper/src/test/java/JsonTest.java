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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityDataEnvelopeDeserializer;
import java.io.IOException;
import org.joda.time.DateTime;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class JsonTest {

    /**
     * @param args the command line arguments
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    public static void main(String[] args) throws JsonProcessingException, IOException {
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

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(envelope);

        System.out.println(json);

        String envelopeJSON = "{\n"
                + "	\"sourceType\": \"CopernicusDataEnvelope\",\n"
                + "	\"areaOfInterest\": {\n"
                + "		\"type\": \"envelope\",\n"
                + "		\"coordinates\": [[0, 1], [2, 0]]\n"
                + "	},\n"
                + "	\"datasetId\": \"XYZ\",\n"
                + "	\"satellite\": \"sentinel-1\",\n"
                + "	\"cloudCoverage\": 20.0,\n"
                + "	\"portal\": \"Code-DE\"\n"
                + "}";

        ObjectMapper mapper2 = new ObjectMapper();
        SimpleModule geoshapeModule = new SimpleModule();
        geoshapeModule.addDeserializer(CopernicusDataEnvelope.class, new GeoShapeCompatibilityDataEnvelopeDeserializer(CopernicusDataEnvelope.class));
        mapper2.registerModule(geoshapeModule);
//        mapper2.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
//        mapper2.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        mapper2.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        AbstractDataEnvelope geoshapeEnvelope = mapper2.readValue(envelopeJSON, CopernicusDataEnvelope.class);
        System.out.println(geoshapeEnvelope);
    }

}
