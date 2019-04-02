package de.wacodis.dataaccess.controller;

import de.wacodis.data.access.datawrapper.DataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.DataEnvelopeSearcher;
import de.wacodis.data.access.datawrapper.RequestResponse;
import de.wacodis.data.access.datawrapper.RequestResult;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchDataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchDataEnvelopeSearcher;
import de.wacodis.dataaccess.configuration.ElasticsearchDataEnvelopesAPIConfiguration;
import de.wacodis.dataaccess.elasticsearch.ElasticsearchClientFactory;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
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

    @org.springframework.beans.factory.annotation.Autowired
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
                return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(responseDataEnvelope.get());
            } else {
                return ResponseEntity.status(404).contentType(MediaType.TEXT_PLAIN).body("no DataEnvelope available for the given id " + id);
            }

        } catch (Exception ex) {
            LOGGER.error("error while retrieving AbstractDataEnvelope for id " + id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
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
    public ResponseEntity<String> createResource(AbstractDataEnvelope abstractDataEnvelope) {
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);

        try {
            DataEnvelopeManipulator manipulator = createDataEnvelopeManipulatorInstance(elasticsearchClient);
            String dataEnvelopeIdentifier = manipulator.createDataEnvelope(abstractDataEnvelope);

            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.TEXT_PLAIN).body(dataEnvelopeIdentifier);
        } catch (Exception ex) {
            LOGGER.error("error while creating AbstractDataEnvelope", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
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
                    return ResponseEntity.status(204).build();
                case NOTFOUND:
                    return ResponseEntity.status(404).contentType(MediaType.TEXT_PLAIN).body("document " + id + " not found");
                default:
                    return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("unexpected result: " + deleteResult.toString());
            }

        } catch (Exception ex) {
            LOGGER.error("error while deleting document " + id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
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
                    Optional<AbstractDataEnvelope> updatedDataEnvelope =  response.getResponseObject();
                    if(updatedDataEnvelope.isPresent()){
                        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(updatedDataEnvelope.get());
                    }else{
                        LOGGER.warn("update request succeded with status code 200 but no response object was submitted, returning empty body");
                        return ResponseEntity.status(HttpStatus.OK).build();
                    }
                case NOTFOUND:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("no document with identifier " + id + "found");
                default:
                    Error error =  ErrorFactory.getErrorObject("update request for resource " + id + " responded with unexpected result: " + response.getStatus().toString());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error);
            }

        } catch (Exception ex) {
            String errorMessage = "unexpected error while updating resource " + id + " in index " + this.elasticsearchConfig.getIndexName();
            LOGGER.error(errorMessage, ex);
            Error error =  ErrorFactory.getErrorObject(errorMessage);
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

}
