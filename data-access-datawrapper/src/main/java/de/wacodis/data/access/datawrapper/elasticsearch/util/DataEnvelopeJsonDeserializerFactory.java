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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.DwdDataEnvelope;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;
import de.wacodis.dataaccess.model.SensorWebDataEnvelope;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityDataEnvelopeDeserializer;
import java.io.IOException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataEnvelopeJsonDeserializerFactory {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DataEnvelopeJsonDeserializerFactory.class);
    
    private final String SOURCETYPE_NODE = "sourceType";

    private final ObjectMapper mapper;

    public DataEnvelopeJsonDeserializerFactory() {
        this.mapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper(String jsonDataEnvelope) {
        ObjectMapper deserializer = new ObjectMapper();
        AbstractDataEnvelope typeReference = new AbstractDataEnvelope();
        SimpleModule deserializeModule = new SimpleModule();

        try {
            AbstractDataEnvelope.SourceTypeEnum srcType = getSourceType(jsonDataEnvelope);

            switch (srcType) {
                case COPERNICUSDATAENVELOPE:
                    typeReference = new CopernicusDataEnvelope();
                    break;
                case GDIDEDATAENVELOPE:
                    typeReference = new GdiDeDataEnvelope();
                    break;
                case SENSORWEBDATAENVELOPE:
                    typeReference = new SensorWebDataEnvelope();
                    break;
                case WACODISPRODUCTDATAENVELOPE:
                    typeReference = new WacodisProductDataEnvelope();
                    break;
                case DWDDATAENVELOPE:
                    typeReference = new DwdDataEnvelope();
                    break;
                default:
                    throw new IllegalArgumentException("cannot create DataEnvelope json deserializer for unknown source type: " + srcType.toString());
            }

            
        } catch (IOException ex) {
            LOGGER.error("could not determine value for " + AbstractDataEnvelope.SourceTypeEnum.class.getSimpleName() + " from json , create deserializer for " + typeReference.getClass().getSimpleName() + ", deserialization might fail!" + System.lineSeparator() +"Json:" + System.lineSeparator() + jsonDataEnvelope);
        }
        
       deserializeModule.addDeserializer(typeReference.getClass(), new GeoShapeCompatibilityDataEnvelopeDeserializer<>());
       deserializer.registerModule(deserializeModule);
       deserializer.registerModule(new JodaModule());
       
       LOGGER.debug("create DataEnvelope json deserializer for type " + typeReference.getClass().getSimpleName());
       
       return deserializer;
    }

    private AbstractDataEnvelope.SourceTypeEnum getSourceType(String jsonDataEnvelope) throws IOException {
        JsonNode node = mapper.readTree(jsonDataEnvelope);
        JsonNode srcTypeNode = node.get(SOURCETYPE_NODE);
        String srcTypeText = srcTypeNode.asText();

        return AbstractDataEnvelope.SourceTypeEnum.fromValue(srcTypeText);
    }

}
