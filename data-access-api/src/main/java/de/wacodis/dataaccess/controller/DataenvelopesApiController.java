package de.wacodis.dataaccess.controller;

import de.wacodis.data.access.datawrapper.DataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.DataEnvelopeSearcher;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchDataEnvelopeManipulator;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchDataEnvelopeSearcher;
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
        String elasticsearchUri = "http://localhost:9200";
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
        String elasticsearchUri = "http://localhost:9200";
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

    private DataEnvelopeSearcher createDataEnvelopeSearcherInstance(RestHighLevelClient elasticsearchClient) {
        String elasticsearchIndex = "dataenvelope";
        long requestTimeout = 5000;

        return new ElasticsearchDataEnvelopeSearcher(elasticsearchClient, elasticsearchIndex, requestTimeout);
    }

    private DataEnvelopeManipulator createDataEnvelopeManipulatorInstance(RestHighLevelClient elasticsearchClient) {
        String elasticsearchIndex = "dataenvelope";
        String type = "dataenvelope";
        long requestTimeout = 5000;

        return new ElasticsearchDataEnvelopeManipulator(elasticsearchClient, type, type, requestTimeout);
    }

}
