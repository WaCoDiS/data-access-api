/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.data.access.datawrapper.ResourceSearchResponseContainer;
import de.wacodis.data.access.datawrapper.ResourceSearcher;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.DataAccessSearchBodyElasticsearchBoolQueryProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.ElasticsearchQueryProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.util.AreaOfInterestConverter;
import de.wacodis.data.access.datawrapper.elasticsearch.util.CopernicusDataEnvelopeSorter;
import de.wacodis.data.access.datawrapper.elasticsearch.util.DataEnvelopeJsonDeserializerFactory;
import de.wacodis.data.access.datawrapper.elasticsearch.util.DataEnvelopePrioritizer;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.DataAccessResourceSearchBody;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.LoggerFactory;
import de.wacodis.data.access.datawrapper.resourceconverter.DataEnvelopeToResourceConversionHelper;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchResourceSearcher implements ResourceSearcher {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchResourceSearcher.class);

    private static final int DEFAULTTIMEOUT_MILLIS = 10000;

    private String indexName;
    private TimeValue requestTimeout;
    private RestHighLevelClient elasticsearchClient;
    private final DataEnvelopeJsonDeserializerFactory jsonDeserializerFactory;
    private final ElasticsearchQueryProvider queryProvider;

    /**
     *
     * @param elasticsearchClient elasticsearch high level REST client
     * @param indexName elasticsearch index
     * @param requestTimeout_millis timeout per request
     */
    public ElasticsearchResourceSearcher(RestHighLevelClient elasticsearchClient, String indexName, long requestTimeout_millis) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
        this.requestTimeout = new TimeValue(requestTimeout_millis, TimeUnit.MILLISECONDS);
        this.jsonDeserializerFactory = new DataEnvelopeJsonDeserializerFactory();
        this.queryProvider = new DataAccessSearchBodyElasticsearchBoolQueryProvider();
    }

    /**
     * use default timeout 10000 milliseconds
     *
     * @param elasticsearchClient
     * @param indexName
     */
    public ElasticsearchResourceSearcher(RestHighLevelClient elasticsearchClient, String indexName) {
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

    @Override
    public Map<String, List<AbstractResource>> query(DataAccessResourceSearchBody searchBody) throws IOException {
        try {
            MultiSearchRequest multiSearch = buildSearchRequest(searchBody);
            MultiSearchResponse multiResponse = this.elasticsearchClient.msearch(multiSearch, RequestOptions.DEFAULT); //synchronous request to elasicsearch

            //raise exception if not all requests succeeded
            List<Exception> failedRequests = getFailures(multiResponse);
            if (!failedRequests.isEmpty()) {
                throw new IOException("at least one request of elasticsearch multisearch request failed" + System.lineSeparator() + getExceptionMessageForFailures(failedRequests));
            }

            Map<String, List<AbstractResource>> responseResources = processResponse(searchBody, multiResponse);
            //close client
            return responseResources;
        } catch (Exception e) {
            throw new IOException("querying resources raised unexpected exception", e);
        }
    }

    /**
     * build elasticsearch multisearch api request containing a request for each
     * input (SubsetDefinition)
     *
     * @param searchBody
     * @return
     */
    private MultiSearchRequest buildSearchRequest(DataAccessResourceSearchBody searchBody) {
        List<AbstractSubsetDefinition> inputs = searchBody.getInputs();
        AbstractDataEnvelopeAreaOfInterest areaOfInterest = searchBody.getAreaOfInterest();
        AbstractDataEnvelopeTimeFrame timeFrame = searchBody.getTimeFrame();

        MultiSearchRequest multiSearch = new MultiSearchRequest(); //init elasticsearch multisearch api request

        inputs.forEach(input -> { //add individual request for each input to multisearch request
            SearchRequest singleRequest = buildSearchRequestForSubsetDefintion(input, areaOfInterest, timeFrame); //request for input
            multiSearch.add(singleRequest);      
        });

        return multiSearch;
    }

    /**
     * build elasicsearch search request for an input (SubsetDefinition)
     *
     * @param subset
     * @param areaOfInterest
     * @param timeFrame
     * @return
     */
    private SearchRequest buildSearchRequestForSubsetDefintion(AbstractSubsetDefinition subset, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame) {
        QueryBuilder query = this.queryProvider.buildQueryForSubsetDefinition(subset, areaOfInterest, timeFrame);
        LOGGER.debug("resource search: add elasticsearch query for input {} to multisearch, query:\n{}", subset.getIdentifier(), query);
        
        SearchSourceBuilder source = new SearchSourceBuilder();
        source.timeout(this.requestTimeout);
        source.from(0); //retrieve all hits
        source.size(10000); //10000 = max allowed value
        source.query(query);

        SearchRequest request = new SearchRequest();
        request.indices(this.indexName);
        request.source(source);

        return request;
    }

    private Map<String, List<AbstractResource>> processResponse(DataAccessResourceSearchBody searchBody, MultiSearchResponse searchResponse) throws IOException {
        Map<String, List<AbstractResource>> resources = new HashMap<>();
        List<AbstractSubsetDefinition> inputs = searchBody.getInputs();

        for (int i = 0; i < inputs.size(); i++) { //response are in the same order as inputs (requests)
            AbstractSubsetDefinition input = inputs.get(i);
            SearchResponse response = searchResponse.getResponses()[i].getResponse();
            List<AbstractDataEnvelope> responseDataEnvelopes = processHits(response.getHits());
            //prioritize results
            //responseDataEnvelopes = orderDataEnvelopes(responseDataEnvelopes, searchBody);
            
            ResourceSearchContext searchContext = new ResourceSearchContext(searchBody.getAreaOfInterest(), searchBody.getTimeFrame(), input);
            ResourceSearchResponseContainer responseHelper = new ResourceSearchResponseContainer(responseDataEnvelopes, searchContext);
            //create resource for DataEnvelope and put to response container
            resources.put(input.getIdentifier(), getResourcesForResponse(responseHelper));
        }

        return resources;
    }

    private List<AbstractDataEnvelope> processHits(SearchHits hits) throws IOException {
        List<AbstractDataEnvelope> responseDataEnvelopes = new ArrayList<>();

        for (SearchHit hit : hits.getHits()) {
            String jsonResponse = hit.getSourceAsString();
            ObjectMapper responseDeserializer = this.jsonDeserializerFactory.getObjectMapper(jsonResponse);
            AbstractDataEnvelope responseDataEnvelope = responseDeserializer.readValue(jsonResponse, AbstractDataEnvelope.class);
            responseDataEnvelope.setAreaOfInterest(getDefaultAreaOfInterest(responseDataEnvelope.getAreaOfInterest()));
            responseDataEnvelope.setIdentifier(hit.getId());
            responseDataEnvelopes.add(responseDataEnvelope);
        }

        return responseDataEnvelopes;
    }

    private List<AbstractResource> getResourcesForResponse(ResourceSearchResponseContainer response) {
        List<AbstractResource> resources = new ArrayList<>();
        List<AbstractDataEnvelope> responseDataEnvelopes = response.getResponseDataEnvelopes();

        for (AbstractDataEnvelope dataEnvelope : responseDataEnvelopes) {
            AbstractResource resource = DataEnvelopeToResourceConversionHelper.convertToResource(dataEnvelope, response.getSearchContext());
            resources.add(resource);
        }

        return resources;
    }

    private List<Exception> getFailures(MultiSearchResponse multiResponse) {
        List<Exception> failures = new ArrayList<>();

        for (MultiSearchResponse.Item responseItem : multiResponse.getResponses()) {
            if (responseItem.isFailure()) {
                failures.add(responseItem.getFailure());
            }
        }

        return failures;
    }

    private String getExceptionMessageForFailures(List<Exception> failures) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < failures.size(); i++) {
            if (i != 0) {
                sb.append(System.lineSeparator());
            }

            sb.append("Exception ").append(i).append(":").append(System.lineSeparator()).append(failures.get(i).getMessage());
        }

        return sb.toString();
    }

    private AbstractDataEnvelopeAreaOfInterest getDefaultAreaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
        if (areaOfInterest instanceof GeoShapeCompatibilityAreaOfInterest) {
            GeoShapeCompatibilityAreaOfInterest geoShapeAreaOfInterest = (GeoShapeCompatibilityAreaOfInterest) areaOfInterest;
            return AreaOfInterestConverter.getDefaultAreaOfInterest(geoShapeAreaOfInterest);
        } else if (areaOfInterest instanceof AbstractDataEnvelopeAreaOfInterest) { //areaOfInteres already is of default type
            return areaOfInterest;
        } else {
            LOGGER.warn("unknown type " + areaOfInterest.getClass().getSimpleName() + ", return unchanged areaOfInterest");
            return areaOfInterest;
        }
    }
    
    private List<AbstractDataEnvelope> orderDataEnvelopes(List<AbstractDataEnvelope> dataEnvelopes, DataAccessResourceSearchBody searchRequest){
        DataEnvelopePrioritizer sorter = new CopernicusDataEnvelopeSorter(searchRequest);
        List<AbstractDataEnvelope> sortedEnvs = sorter.orderDataEnvelopes(dataEnvelopes);
        
        return sortedEnvs;
    }

}
