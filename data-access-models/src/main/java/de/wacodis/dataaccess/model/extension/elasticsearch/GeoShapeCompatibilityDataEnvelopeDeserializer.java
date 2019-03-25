/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dataaccess.model.extension.elasticsearch;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <T>
 */
public class GeoShapeCompatibilityDataEnvelopeDeserializer<T extends AbstractDataEnvelope> extends StdDeserializer<T> {
    
    private final static String AREAOFINTEREST_NODE = "areaOfInterest";

    public GeoShapeCompatibilityDataEnvelopeDeserializer() {
        this(null);
    }

    public GeoShapeCompatibilityDataEnvelopeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
   
        //convert elasticsearch geoshape aoi to object
        JsonNode aoiNode = node.get(AREAOFINTEREST_NODE); //convert elasticsearch geoshape aoi to object
        GeoShapeCompatibilityAreaOfInterest aoi = mapper.treeToValue(aoiNode, GeoShapeCompatibilityAreaOfInterest.class);
        //set (json) aoi temporarily to null
        ((ObjectNode)node).replace(AREAOFINTEREST_NODE, NullNode.getInstance());
        //convert json to DataEnvelope
        AbstractDataEnvelope envelope = mapper.treeToValue(node, AbstractDataEnvelope.class);
        //replace null aoi by elasticsearch geoshape aoi
        envelope.setAreaOfInterest(aoi);
        
        return ((T)envelope);
    }

}
