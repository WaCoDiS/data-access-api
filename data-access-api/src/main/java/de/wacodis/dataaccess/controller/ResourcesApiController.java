package de.wacodis.dataaccess.controller;

import de.wacodis.data.access.datawrapper.ResourceSearchResponseToResourceConverter;
import de.wacodis.data.access.datawrapper.ResourceSearcher;
import de.wacodis.data.access.datawrapper.SimpleResourceSearchResponseToResourceConverter;
import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchResourceSearcher;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.DataAccessResourceSearchBody;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.LoggerFactory;
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
public class ResourcesApiController implements ResourcesApi {

    private final NativeWebRequest request;
    
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
        try {
            ResourceSearcher searcher = createResourceSearcherInstance();
            Map<String, List<AbstractResource>> hits = searcher.query(dataAccessResourceSearchBody);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(hits);
        } catch (IOException ex) {
            LOGGER.error("error while querying metadata", ex);     
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
    
    private ResourceSearcher createResourceSearcherInstance(){
        ResourceSearchResponseToResourceConverter responseConverter = new SimpleResourceSearchResponseToResourceConverter();
        ElasticsearchResourceSearcher searcher = new ElasticsearchResourceSearcher(responseConverter);
        searcher.setIndexName("dataenvelope");
        searcher.setTimeOut(new TimeValue(10, TimeUnit.SECONDS));
        
        return searcher;
    }
   
}
