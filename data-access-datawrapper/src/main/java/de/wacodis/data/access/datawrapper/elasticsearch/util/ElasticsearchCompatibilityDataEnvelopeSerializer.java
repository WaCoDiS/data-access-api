/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.io.IOException;
import java.util.Map;


/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchCompatibilityDataEnvelopeSerializer {

    private final ObjectMapper serializer;
    
    public ElasticsearchCompatibilityDataEnvelopeSerializer() {
        this.serializer = new ObjectMapper();
        this.serializer.registerModule(new JodaModule());
        this.serializer.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * serializes AbstractDataEnvelope as Map<String, Object>, ensures no duplicate fields
     * @param dataEnvelope
     * @return
     * @throws IOException 
     */
    public Map<String, Object> serialize(AbstractDataEnvelope dataEnvelope) throws IOException{
        //parsing json as map removes duplicate fields in json string (which can occur with jackson type inheritance)
        String dataEnvelopeJson = serializer.writeValueAsString(dataEnvelope);
        Map<String, Object> result = serializer.readValue(dataEnvelopeJson, new TypeReference<Map<String, Object>>(){}); //
        
        return result;
    }
    
}
