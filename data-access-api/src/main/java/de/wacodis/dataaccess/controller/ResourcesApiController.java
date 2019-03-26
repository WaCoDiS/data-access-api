package de.wacodis.dataaccess.controller;

import de.wacodis.data.access.datawrapper.ResourceSearchResponseToResourceConverter;
import de.wacodis.data.access.datawrapper.ResourceSearcher;
import de.wacodis.data.access.datawrapper.SimpleResourceSearchResponseToResourceConverter;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchResourceSearcher;
import de.wacodis.dataaccess.configuration.ElasticsearchResourcesAPIConfiguration;
import de.wacodis.dataaccess.elasticsearch.ElasticsearchClientFactory;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.DataAccessResourceSearchBody;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
public class ResourcesApiController implements ResourcesApi {

    private final NativeWebRequest request;
    
    @Autowired
    ElasticsearchResourcesAPIConfiguration elasticsearchConfig;
    
    @Autowired
    ElasticsearchClientFactory elasticsearchClientFactory;
    
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ResourcesApiController.class);

    @org.springframework.beans.factory.annotation.Autowired
    public ResourcesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity searchResources(DataAccessResourceSearchBody dataAccessResourceSearchBody) {
        String elasticsearchUri = this.elasticsearchConfig.getUri();
        RestHighLevelClient elasticsearchClient = this.elasticsearchClientFactory.buildElasticsearchClient(elasticsearchUri);
        LOGGER.debug("built elasticsearch client for uri " + elasticsearchUri);
        
        try {
            LOGGER.info("initiate metadata search" + System.lineSeparator() + dataAccessResourceSearchBody.toString() + System.lineSeparator() + "connect to " + elasticsearchUri);
            ResourceSearcher searcher = createResourceSearcherInstance(elasticsearchClient);
            Map<String, List<AbstractResource>> hits = searcher.query(dataAccessResourceSearchBody);
            LOGGER.info("metadata search succeded, with result:" + System.lineSeparator() + hits.toString());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(hits);
        } catch (IOException ex) {
            LOGGER.error("error while querying metadata for search body " + System.lineSeparator() + dataAccessResourceSearchBody.toString(), ex);     
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }finally{
            try {
                elasticsearchClient.close();
                LOGGER.debug("closed elasticcsearch client for uri " + elasticsearchUri);
            } catch (IOException ex) {
                LOGGER.error("closing elasticsearch client for uri " + elasticsearchUri + " raised exception, could not close client", ex);
            }
        }
    }
    
    private ResourceSearcher createResourceSearcherInstance(RestHighLevelClient elasticsearchClient){
        String elasticsearchIndex = this.elasticsearchConfig.getIndexName();
        long requestTimeout = this.elasticsearchConfig.getRequestTimeout_Millis();
        ResourceSearchResponseToResourceConverter responseConverter = new SimpleResourceSearchResponseToResourceConverter();
        
        ElasticsearchResourceSearcher searcher = new ElasticsearchResourceSearcher(elasticsearchClient, elasticsearchIndex, responseConverter, requestTimeout);
        return searcher;
    }
   
}
