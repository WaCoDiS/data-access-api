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
