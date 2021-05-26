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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityDataEnvelopeDeserializer;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class JsonDeserializerTest {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
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

        ObjectMapper mapper = new ObjectMapper();
        
        AbstractDataEnvelope reference = new CopernicusDataEnvelope();
        
        SimpleModule geoshapeModule = new SimpleModule();
        //geoshapeModule.addDeserializer(AbstractDataEnvelope.class, new GeoShapeCompatibilityDataEnvelopeDeserializer());
        geoshapeModule.addDeserializer(reference.getClass(), new GeoShapeCompatibilityDataEnvelopeDeserializer<>());
        mapper.registerModule(geoshapeModule);
//        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
//        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        AbstractDataEnvelope geoshapeEnvelope = mapper.readValue(envelopeJSON, reference.getClass());
        System.out.println(geoshapeEnvelope);
    }

}
