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
package de.wacodis.data.access.datawrapper.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.data.access.datawrapper.DataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.RequestResponse;
import de.wacodis.data.access.datawrapper.RequestResult;
import de.wacodis.data.access.datawrapper.elasticsearch.util.AreaOfInterestConverter;
import de.wacodis.data.access.datawrapper.elasticsearch.util.DataEnvelopeJsonDeserializerFactory;
import de.wacodis.data.access.datawrapper.elasticsearch.util.ElasticsearchCompatibilityDataEnvelopeSerializer;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;

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
    public RequestResponse<AbstractDataEnvelope> createDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException {
        try {
            IndexRequest request = buildIndexRequest(dataEnvelope);
            IndexResponse response = this.elasticsearchClient.index(request, RequestOptions.DEFAULT);
            String dataEnvelopeIdentifier = getIDFromIndexResponse(response);
            dataEnvelope.setIdentifier(dataEnvelopeIdentifier);

            if (dataEnvelope.getAreaOfInterest() instanceof GeoShapeCompatibilityAreaOfInterest) {
                dataEnvelope.setAreaOfInterest(AreaOfInterestConverter.getDefaultAreaOfInterest((GeoShapeCompatibilityAreaOfInterest)dataEnvelope.getAreaOfInterest()));
            }

            return new RequestResponse(RequestResult.CREATED, Optional.of(dataEnvelope));
        } catch (Exception ex) {
            throw new IOException("could not create AbstractDataEnvelope,  raised unexpected exception", ex);
        }
    }

    @Override
    public RequestResponse<AbstractDataEnvelope> updateDataEnvelope(String identifier, AbstractDataEnvelope dataEnvelope) throws IOException {
        try {
            UpdateRequest request = buildUpdateRequest(identifier, dataEnvelope);
            UpdateResponse response = this.elasticsearchClient.update(request, RequestOptions.DEFAULT);

            switch (response.status()) {
                case OK:
                    if (response.getGetResult().isExists() && !response.getGetResult().isSourceEmpty()) {
                        String updatedDataEnvelopeJson = response.getGetResult().sourceAsString();
                        ObjectMapper deserializer = this.jsonDeserializerFactory.getObjectMapper(updatedDataEnvelopeJson);
                        AbstractDataEnvelope updatedDataEnvelope = deserializer.readValue(updatedDataEnvelopeJson, AbstractDataEnvelope.class);
                        //set areaOfInterest conforming to Wacodis Data Models
                        GeoShapeCompatibilityAreaOfInterest compatibilityAreaOfInterest = (GeoShapeCompatibilityAreaOfInterest) updatedDataEnvelope.getAreaOfInterest();
                        AbstractDataEnvelopeAreaOfInterest defaultAreaOfInterest = AreaOfInterestConverter.getDefaultAreaOfInterest(compatibilityAreaOfInterest);
                        updatedDataEnvelope.setAreaOfInterest(defaultAreaOfInterest);
                        updatedDataEnvelope.setIdentifier(response.getId());

                        return new RequestResponse<>(RequestResult.MODIFIED, Optional.of(updatedDataEnvelope));

                    } else {
                        throw new IOException("AbstractDataEnvelope with identifier " + identifier + " updated in index " + this.indexName + " but response did not contain updated resource");
                    }
                case NOT_FOUND:
                    return new RequestResponse<>(RequestResult.MODIFIED, Optional.empty());
                default:
                    throw new IOException("could not update AbstractDataEnvelope with indentifier" + identifier + "  ,request got response" + response.status().toString());
            }

        } catch (Exception ex) {
            throw new IOException("error while updating AbstractDataEnvelope with identifier " + identifier + ",  raised unexpected exception", ex);
        }
    }

    @Override
    public RequestResult deleteDataEnvelope(String identifier) throws IOException {
        try {
            DeleteRequest request = buildDeleteRequest(identifier);
            DeleteResponse response = this.elasticsearchClient.delete(request, RequestOptions.DEFAULT);
            DocWriteResponse.Result result = response.getResult();

            switch (result) {
                case DELETED:
                    LOGGER.info("successfully deleted document " + identifier + " from index" + this.indexName);
                    return RequestResult.DELETED;
                case NOT_FOUND:
                    LOGGER.info("unable to deleted document " + identifier + " from index " + this.indexName + ", document " + identifier + "was not found");
                    return RequestResult.NOTFOUND;
                default:
                    LOGGER.info("unable to deleted document " + identifier + " from index " + this.indexName + ", request got response " + result.toString());
                    return RequestResult.ERROR;
            }
        } catch (Exception ex) {
            throw new IOException("could not delete AbstractDataEnvelope with identifier " + identifier + ",  raised unexpected exception", ex);
        }
    }

    private IndexRequest buildIndexRequest(AbstractDataEnvelope dataEnvelope) {
        Map<String, Object> serializedDataEnvelope = serializeDataEnvelope(dataEnvelope);

        IndexRequest request = new IndexRequest();
        request.index(this.indexName);
        request.type(this.type);
        request.source(serializedDataEnvelope);
        request.timeout(this.requestTimeout);

        return request;
    }

    private IndexRequest buildIndexRequest(AbstractDataEnvelope dataEnvelope, String identifier) {
        IndexRequest request = buildIndexRequest(dataEnvelope);
        request.id(identifier);
        request.timeout(this.requestTimeout);

        return request;
    }

    private DeleteRequest buildDeleteRequest(String identifier) {
        DeleteRequest request = new DeleteRequest();
        request.id(identifier).index(this.indexName).type(this.type).timeout(this.requestTimeout);

        return request;
    }

    private UpdateRequest buildUpdateRequest(String identifier, AbstractDataEnvelope dataEnvelope) {
        Map<String, Object> serializedDataEnvelope = serializeDataEnvelope(dataEnvelope);

        UpdateRequest request = new UpdateRequest();
        request.index(this.indexName);
        request.type(this.type);
        request.id(identifier);
        request.doc(serializedDataEnvelope);
        request.timeout(this.requestTimeout);
        request.fetchSource(true); //append updated resource to response

        return request;
    }

    /**
     * @param response
     * @return identifier of indexed AbstractDataEnvelope
     */
    private String getIDFromIndexResponse(IndexResponse response) {
        String id = response.getId();

        return id;
    }

    private Map<String, Object> serializeDataEnvelope(AbstractDataEnvelope dataEnvelope) {
        GeoShapeCompatibilityAreaOfInterest geoshapeAreaOfInterest = AreaOfInterestConverter.getGeoshapeAreaOfInterest(dataEnvelope.getAreaOfInterest()); //make AreaOfInterest compatible with elasticsearch
        dataEnvelope.setAreaOfInterest(geoshapeAreaOfInterest);

        try {
            Map<String, Object> serializedDataEnvelope = this.dataEnvelopeSerializer.serialize(dataEnvelope);
            return serializedDataEnvelope;
        } catch (IOException ex) {
            throw new IllegalArgumentException("cannot serialize AbstractDataEnvelope as JSON" + System.lineSeparator() + dataEnvelope.toString(), ex);
        }
    }

}
