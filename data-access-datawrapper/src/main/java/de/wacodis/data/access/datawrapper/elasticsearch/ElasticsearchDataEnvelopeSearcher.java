/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.data.access.datawrapper.DataEnvelopeSearcher;
import de.wacodis.data.access.datawrapper.elasticsearch.util.DataEnvelopeJsonDeserializerFactory;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchDataEnvelopeSearcher implements DataEnvelopeSearcher {
    
     private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDataEnvelopeSearcher.class);

    private static final int DEFAULTTIMEOUT_MILLIS = 10000;

    private RestHighLevelClient elasticsearchClient;
    private String type;
    private String indexName;
    private TimeValue requestTimeout;
    private final DataEnvelopeJsonDeserializerFactory jsonDeserializerFactory;

    public ElasticsearchDataEnvelopeSearcher(RestHighLevelClient elasticsearchClient, String indexName, long requestTimeout_millis) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
        this.requestTimeout = new TimeValue(requestTimeout_millis, TimeUnit.MILLISECONDS);
        this.jsonDeserializerFactory = new DataEnvelopeJsonDeserializerFactory();
    }

    public ElasticsearchDataEnvelopeSearcher(RestHighLevelClient elasticsearchClient, String indexName) {
        this(elasticsearchClient, indexName, DEFAULTTIMEOUT_MILLIS);
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

    public RestHighLevelClient getElasticsearchClient() {
        return elasticsearchClient;
    }

    public void setElasticsearchClient(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }
    
    

    @Override
    public Optional<AbstractDataEnvelope> retrieveDataEnvelopeById(String identifier) throws IOException {
        GetRequest request = buildGetRequest(identifier);
        GetResponse response = this.elasticsearchClient.get(request, RequestOptions.DEFAULT);
        Optional<AbstractDataEnvelope> responseDataEnvelope = processGetResponse(response);

        return responseDataEnvelope;
    }

    @Override
    public Optional<String> retrieveIdForDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private GetRequest buildGetRequest(String identifier) {
        GetRequest request = new GetRequest();
        request.id(identifier);
        request.index(this.indexName);

        return request;
    }

    private Optional<AbstractDataEnvelope> processGetResponse(GetResponse response) throws IOException{
        if(response.isExists() && !response.isSourceEmpty()){
            String dataEnvelopeJson = response.getSourceAsString();
            ObjectMapper mapper = this.jsonDeserializerFactory.getObjectMapper(dataEnvelopeJson);
            AbstractDataEnvelope responseDataEnvelope = mapper.readValue(dataEnvelopeJson, AbstractDataEnvelope.class);
            return Optional.of(responseDataEnvelope);
        }else{
           LOGGER.debug("response of get request did not contain items");
           return Optional.empty();
        }
    }
    
}
