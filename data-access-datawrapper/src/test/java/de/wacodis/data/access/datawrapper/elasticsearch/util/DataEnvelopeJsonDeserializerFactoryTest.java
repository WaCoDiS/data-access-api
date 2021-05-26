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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataEnvelopeJsonDeserializerFactoryTest {

    public DataEnvelopeJsonDeserializerFactoryTest() {
    }

    @Test
    public void testDeserializationRoundtrip() throws IOException {
        String dataEnvelopeJSON = "{\n"
                + "  \"sourceType\": \"CopernicusDataEnvelope\",\n"
                + "  \"areaOfInterest\": {\n"
                + "			\"type\": \"envelope\",\n"
                + "			\"coordinates\": [[-180.0, 90.0], [180.0, -90.0]]\n"
                + "   },\n"
                + "  \"timeFrame\": {\n"
                + "		\"startTime\": \"2012-04-23T18:25:43.511\",\n"
                + "		\"endTime\":  \"2018-04-23T18:25:43.511\"\n"
                + "	},\n"
                + "  \"created\": \"2012-04-23T18:25:43.511\",\n"
                + "  \"modified\": \"2012-04-23T18:25:43.511\",\n"
                + "  \"datasetId\": \"abcdefgh\",\n"
                + "  \"satellite\": \"sentinel-2\",\n"
                + "  \"cloudCoverage\": 25.5,\n"
                + "  \"portal\": \"Sentinel-Hub\"\n"
                + "}";

        DataEnvelopeJsonDeserializerFactory factory = new DataEnvelopeJsonDeserializerFactory();
        ObjectMapper mapper = factory.getObjectMapper(dataEnvelopeJSON);

        AbstractDataEnvelope dataEnvelope = mapper.readValue(dataEnvelopeJSON, AbstractDataEnvelope.class);
        String serializedDataEnvelopeJSON = mapper.writeValueAsString(dataEnvelope);

        assertEquals(dataEnvelope, mapper.readValue(serializedDataEnvelopeJSON, AbstractDataEnvelope.class));
    }

}
