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

        DataEnvelopeJsonDeserializerFactory factory = new DataEnvelopeJsonDeserializerFactory();
        ObjectMapper mapper = factory.getObjectMapper(dataEnvelopeJSON);
        
        AbstractDataEnvelope dataEnvelope = mapper.readValue(dataEnvelopeJSON, AbstractDataEnvelope.class);
        String serializedDataEnvelopeJSON = mapper.writeValueAsString(dataEnvelope);
        
        assertEquals(dataEnvelope, mapper.readValue(serializedDataEnvelopeJSON, AbstractDataEnvelope.class));
    }

}
