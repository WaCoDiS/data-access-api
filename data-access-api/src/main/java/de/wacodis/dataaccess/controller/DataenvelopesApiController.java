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
package de.wacodis.dataaccess.controller;

import de.wacodis.data.access.datawrapper.DataEnvelopeExplorer;
import de.wacodis.data.access.datawrapper.DataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.DataEnvelopeSearcher;
import de.wacodis.data.access.datawrapper.RequestResponse;
import de.wacodis.data.access.datawrapper.RequestResult;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticSearchDataEnvelopeExplorer;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchDataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchDataEnvelopeSearcher;
import de.wacodis.dataaccess.configuration.ElasticsearchDataEnvelopesAPIConfiguration;
import de.wacodis.dataaccess.elasticsearch.ElasticsearchClientFactory;
import de.wacodis.dataaccess.messaging.DataEnvelopeAcknowledgmentPublisherChannel;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.DataEnvelopeQuery;
import java.io.IOException;
import java.util.Optional;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import de.wacodis.dataaccess.model.Error;
import de.wacodis.dataaccess.util.ErrorFactory;
import java.util.List;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-11-27T14:57:01.610+01:00[Europe/Berlin]")
@Controller
@RequestMapping("${openapi.waCoDiSDataAccess.base-path:/dataAccess}")
public class DataenvelopesApiController implements DataenvelopesApi {

    private final NativeWebRequest request;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DataenvelopesApiController.class);

    @Autowired
    ElasticsearchClientFactory elasticsearchClientFactory;

    @Autowired
    ElasticsearchDataEnvelopesAPIConfiguration elasticsearchConfig;

    @Autowired
    DataEnvelopeAcknowledgmentPublisherChannel dataEnvelopeAcknowledger;

    @Autowired
    public DataenvelopesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity retrieveDataEnvelope(String id) {
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);

        try {
            DataEnvelopeSearcher searcher = createDataEnvelopeSearcherInstance(elasticsearchClient);
            Optional<AbstractDataEnvelope> responseDataEnvelope = searcher.retrieveDataEnvelopeById(id);

            if (responseDataEnvelope.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseDataEnvelope.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception ex) {
            LOGGER.error("error while retrieving AbstractDataEnvelope for id " + id, ex);
            Error error = ErrorFactory.getErrorObject("unexpected error while retrieving resource " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
        } finally {
            try {
                elasticsearchClient.close();
                LOGGER.debug("closed elasticcsearch client for uri " + elasticsearchUri);
            } catch (IOException ex) {
                LOGGER.error("closing elasticsearch client for uri " + elasticsearchUri + " raised exception, could not close client", ex);
            }
        }
    }

    @Override
    public ResponseEntity createResource(AbstractDataEnvelope abstractDataEnvelope) {
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);

        try {
            DataEnvelopeManipulator manipulator = createDataEnvelopeManipulatorInstance(elasticsearchClient);
            RequestResponse<AbstractDataEnvelope> createResponse = manipulator.createDataEnvelope(abstractDataEnvelope);
            if (createResponse.getStatus().equals(RequestResult.CREATED) && createResponse.getResponseObject().isPresent()) {
                AbstractDataEnvelope indexedDataEnvelope = createResponse.getResponseObject().get();
                //acknowledge created DataEnvelope
                publishDataEnvelopeAcknowledgement(indexedDataEnvelope);

                return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(indexedDataEnvelope);
            } else {
                String message = "unable to create resources" + System.lineSeparator() + abstractDataEnvelope.toString();
                Error error = ErrorFactory.getErrorObject(message);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
            }
        } catch (Exception ex) {
            LOGGER.error("error while creating AbstractDataEnvelope", ex);
            Error error = ErrorFactory.getErrorObject("unexpected error while creating resource" + System.lineSeparator() + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
        } finally {
            try {
                elasticsearchClient.close();
                LOGGER.debug("closed elasticcsearch client for uri " + elasticsearchUri);
            } catch (IOException ex) {
                LOGGER.error("closing elasticsearch client for uri " + elasticsearchUri + " raised exception, could not close client", ex);
            }
        }
    }

    @Override
    public ResponseEntity deleteDataEnvelope(String id) {
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);

        try {
            DataEnvelopeManipulator manipulator = createDataEnvelopeManipulatorInstance(elasticsearchClient);
            RequestResult deleteResult = manipulator.deleteDataEnvelope(id);

            switch (deleteResult) {
                case DELETED:
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                case NOTFOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                default:
                    Error error = ErrorFactory.getErrorObject("unexpected result: " + deleteResult.toString());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
            }

        } catch (Exception ex) {
            LOGGER.error("error while deleting document " + id, ex);
            Error error = ErrorFactory.getErrorObject("unexpected error while deleting resource " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
        } finally {
            try {
                elasticsearchClient.close();
                LOGGER.debug("closed elasticcsearch client for uri " + elasticsearchUri);
            } catch (IOException ex) {
                LOGGER.error("closing elasticsearch client for uri " + elasticsearchUri + " raised exception, could not close client", ex);
            }
        }
    }

    @Override
    public ResponseEntity modifyDataEnvelope(String id, AbstractDataEnvelope abstractDataEnvelope) {
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);

        try {
            DataEnvelopeManipulator manipulator = createDataEnvelopeManipulatorInstance(elasticsearchClient);
            RequestResponse response = manipulator.updateDataEnvelope(id, abstractDataEnvelope);

            switch (response.getStatus()) {
                case MODIFIED:
                    Optional<AbstractDataEnvelope> updatedDataEnvelope = response.getResponseObject();
                    if (updatedDataEnvelope.isPresent()) {
                        //acknowledge updated DataEnvelope
                        publishDataEnvelopeAcknowledgement(updatedDataEnvelope.get());

                        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(updatedDataEnvelope.get());
                    } else {
                        LOGGER.warn("update request succeded with status code 200 but no response object was submitted, returning empty body");
                        return ResponseEntity.status(HttpStatus.OK).build();
                    }

                case NOTFOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                default:
                    Error error = ErrorFactory.getErrorObject("update request for resource " + id + " responded with unexpected result: " + response.getStatus().toString());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
            }

        } catch (Exception ex) {
            String errorMessage = "unexpected error while updating resource " + id + " in index " + this.elasticsearchConfig.getIndexName();
            LOGGER.error(errorMessage, ex);
            Error error = ErrorFactory.getErrorObject(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorMessage);
        } finally {
            try {
                elasticsearchClient.close();
                LOGGER.debug("closed elasticcsearch client for uri " + elasticsearchUri);
            } catch (IOException ex) {
                LOGGER.error("closing elasticsearch client for uri " + elasticsearchUri + " raised exception, could not close client", ex);
            }
        }
    }

    @Override
    public ResponseEntity retrieveDataEnvelopeID(AbstractDataEnvelope abstractDataEnvelope) {
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);

        try {
            DataEnvelopeSearcher searcher = createDataEnvelopeSearcherInstance(elasticsearchClient);
            RequestResponse<AbstractDataEnvelope> response = searcher.retrieveIdForDataEnvelope(abstractDataEnvelope); //id

            if (response.getResponseObject().isPresent()) { //match found
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(response.getResponseObject().get());
            } else { //no match found
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            String errorMessage = "unexpected error while searching existing DataEnvelopes in index " + this.elasticsearchConfig.getIndexName();
            LOGGER.error(errorMessage + System.lineSeparator() + abstractDataEnvelope.toString(), ex);
            Error error = ErrorFactory.getErrorObject(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorMessage);

        } finally {
            try {
                elasticsearchClient.close();
                LOGGER.debug("closed elasticcsearch client for uri " + elasticsearchUri);
            } catch (IOException ex) {
                LOGGER.error("closing elasticsearch client for uri " + elasticsearchUri + " raised exception, could not close client", ex);
            }

        }
    }

    @Override
    public ResponseEntity queryExistingDataEnvelopes(DataEnvelopeQuery dataEnvelopeQuery) {
        LOGGER.info("initiate DataEnvelope exploration");
                
        ResponseEntity requestResponse;
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);

        DataEnvelopeExplorer explorer = createDataEnvelopeExplorerInstance(elasticsearchClient);
        RequestResponse queryResponse = explorer.queryDataEnvelopes(dataEnvelopeQuery);

        if (queryResponse.getResponseObject().isPresent()) {
            requestResponse = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(queryResponse.getResponseObject().get());
        } else {
            String errorMsg = createErrorMessage(queryResponse.getExcetions());
            Error error = ErrorFactory.getErrorObject(errorMsg);
            requestResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
        }

        try {
            elasticsearchClient.close();
            LOGGER.debug("closed elasticcsearch client for uri {}", elasticsearchUri);
        } catch (IOException ex) {
            LOGGER.error("closing elasticsearch client for uri " + elasticsearchUri + " raised exception, could not close client", ex);
        }
        
        LOGGER.info("finished DataEnvelope exploration");
        
        return requestResponse;
    }

    private DataEnvelopeSearcher createDataEnvelopeSearcherInstance(RestHighLevelClient elasticsearchClient) {
        String elasticsearchIndex = this.elasticsearchConfig.getIndexName();
        long requestTimeout = this.elasticsearchConfig.getRequestTimeout_Millis();

        return new ElasticsearchDataEnvelopeSearcher(elasticsearchClient, elasticsearchIndex, requestTimeout);
    }

    private DataEnvelopeManipulator createDataEnvelopeManipulatorInstance(RestHighLevelClient elasticsearchClient) {
        String elasticsearchIndex = this.elasticsearchConfig.getIndexName();
        String documentType = this.elasticsearchConfig.getType();
        long requestTimeout = this.elasticsearchConfig.getRequestTimeout_Millis();

        return new ElasticsearchDataEnvelopeManipulator(elasticsearchClient, elasticsearchIndex, documentType, requestTimeout);
    }

    private DataEnvelopeExplorer createDataEnvelopeExplorerInstance(RestHighLevelClient elasticsearchClient) {
        String elasticsearchIndex = this.elasticsearchConfig.getIndexName();
        long requestTimeout = this.elasticsearchConfig.getRequestTimeout_Millis();

        return new ElasticSearchDataEnvelopeExplorer(elasticsearchClient, elasticsearchIndex, requestTimeout);
    }

    private String createErrorMessage(List<Exception> exceptions) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("exception report: ").append(System.lineSeparator());

        if (exceptions.isEmpty()) {
            errorMessage.append("\t").append("no exceptions provided");
        } else {
            for (int i = 0; i < exceptions.size(); i++) {
                errorMessage.append("\t").append("exception ").append((i+1)).append(": ").append(exceptions.get(i).getMessage());
            }
        }

        return errorMessage.toString();
    }

    private boolean publishDataEnvelopeAcknowledgement(AbstractDataEnvelope dataEnvelope) {
        LOGGER.info("publish acknowledgement of DataEnvelope:" + System.lineSeparator() + dataEnvelope.toString());

        Message acknowledgement = MessageBuilder.withPayload(dataEnvelope).build();
        return this.dataEnvelopeAcknowledger.acknowledgeDataEnvelope().send(acknowledgement);
    }

}
