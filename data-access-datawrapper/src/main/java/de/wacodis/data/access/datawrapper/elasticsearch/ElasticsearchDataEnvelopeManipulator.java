/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.wacodis.data.access.datawrapper.DataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.elasticsearch.util.DataEnvelopeJsonDeserializerFactory;
import de.wacodis.data.access.datawrapper.elasticsearch.util.ElasticsearchCompatibilityDataEnvelopeSerializer;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchDataEnvelopeManipulator implements DataEnvelopeManipulator {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDataEnvelopeSearcher.class);

    private static final int DEFAULTTIMEOUT_MILLIS = 10000;

    private RestHighLevelClient elasticsearchClient;
    private String type;
    private String indexName;
    private TimeValue requestTimeout;
    private final DataEnvelopeJsonDeserializerFactory jsonDeserializerFactory;
    private final ElasticsearchCompatibilityDataEnvelopeSerializer dataEnvelopeSerializer;

    public ElasticsearchDataEnvelopeManipulator(RestHighLevelClient elasticsearchClient, String indexName, String type, long requestTimeout) {
        this.elasticsearchClient = elasticsearchClient;
        this.type = type;
        this.indexName = indexName;
        this.requestTimeout = new TimeValue(requestTimeout, TimeUnit.MILLISECONDS);
        this.jsonDeserializerFactory = new DataEnvelopeJsonDeserializerFactory();
        this.dataEnvelopeSerializer = new ElasticsearchCompatibilityDataEnvelopeSerializer();
    }

    public ElasticsearchDataEnvelopeManipulator(RestHighLevelClient elasticsearchClient, String indexName, String type) {
        this(elasticsearchClient, indexName, type, DEFAULTTIMEOUT_MILLIS);
    }

    public RestHighLevelClient getElasticsearchClient() {
        return elasticsearchClient;
    }

    public void setElasticsearchClient(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public TimeValue getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(TimeValue requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    @Override
    public String createDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException {
        try {
            IndexRequest request = buildIndexRequest(dataEnvelope);
            IndexResponse response = this.elasticsearchClient.index(request, RequestOptions.DEFAULT);
            String dataEnvelopeIdentifier = processIndexResponse(response);
            return dataEnvelopeIdentifier;
        } catch (Exception ex) {
            throw new IOException("could not create AbstractDataEnvelope,  raised unexpected exception", ex);
        }
    }

    @Override
    public AbstractDataEnvelope updateDataEnvelope(String identifier, AbstractDataEnvelope dataEnvelope) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteDataEnvelope(String identifier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private IndexRequest buildIndexRequest(AbstractDataEnvelope dataEnvelope) {
        Map<String, Object> serializedDataEnvelope = serializeDataEnvelope(dataEnvelope);

        IndexRequest request = new IndexRequest();
        request.index(this.indexName);
        request.type(this.type);
        request.source(serializedDataEnvelope);
        
        return request;
    }
    
    /**
     * @param response
     * @return id of indexed AbstractDataEnvelope
     */
    private String processIndexResponse(IndexResponse response){
        return response.getId();
    }

    private Map<String, Object> serializeDataEnvelope(AbstractDataEnvelope dataEnvelope) {
        GeoShapeCompatibilityAreaOfInterest geoshapeAreaOfInterest = getGeoshapeAreaOfInterst(dataEnvelope.getAreaOfInterest()); //make AreaOfInterest compatible with elasticsearch
        dataEnvelope.setAreaOfInterest(geoshapeAreaOfInterest);

        try {
            Map<String, Object> serializedDataEnvelope = this.dataEnvelopeSerializer.serialize(dataEnvelope);
            return serializedDataEnvelope;
        } catch (IOException ex) {
            throw new IllegalArgumentException("cannot serialize AbstractDataEnvelope as JSON" + System.lineSeparator() + dataEnvelope.toString(), ex);
        }
    }

    private GeoShapeCompatibilityAreaOfInterest getGeoshapeAreaOfInterst(AbstractDataEnvelopeAreaOfInterest defaultAreaOfInterest) {
        GeoShapeCompatibilityAreaOfInterest geoshapeAreaOfInterest = new GeoShapeCompatibilityAreaOfInterest();

        Float[] bbox = defaultAreaOfInterest.getExtent().toArray(new Float[0]); //geojson bbox format [minLon, minLat, maxLon, maxLat]
        List<Float> topLeft = Arrays.asList(new Float[]{bbox[0], bbox[3]});
        List<Float> bottomRight = Arrays.asList(new Float[]{bbox[2], bbox[1]});

        List<List<Float>> geoshapeEnvelope = new ArrayList<>();
        geoshapeEnvelope.add(topLeft);
        geoshapeEnvelope.add(bottomRight);

        geoshapeAreaOfInterest.setCoordinates(geoshapeEnvelope);
        geoshapeAreaOfInterest.setType(GeoShapeCompatibilityAreaOfInterest.GeoShapeType.ENVELOPE);

        return geoshapeAreaOfInterest;
    }

}
