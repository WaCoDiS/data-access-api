/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch;

import de.wacodis.data.access.datawrapper.ResponseHelper;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.DataAccessSearchBodyElasticsearchBoolQueryProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.ElasticsearchQueryProvider;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.DataAccessResourceSearchBody;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchDataWrapper implements DataWrapper {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDataWrapper.class);

    private final ElasticsearchQueryProvider queryProvider;

    private String indexName;
    private TimeValue timeOut;
    private RestHighLevelClient elasticsearchClient;

    public ElasticsearchDataWrapper() {
        this.queryProvider = new DataAccessSearchBodyElasticsearchBoolQueryProvider();
        this.elasticsearchClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public TimeValue getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(TimeValue timeOut) {
        this.timeOut = timeOut;
    }

    public Map<String, List<AbstractResource>> query(DataAccessResourceSearchBody searchBody) throws IOException {

        MultiSearchRequest multiSearch = buildSearchRequest(searchBody);
        MultiSearchResponse multiResponse = this.elasticsearchClient.msearch(multiSearch, RequestOptions.DEFAULT); //synchronous request to elasicsearch
        
        //raise exception if not all requests succeeded
        List<Exception> failedRequests = getFailures(multiResponse);
        if(!failedRequests.isEmpty()){
            throw new IOException("at least one request of elasticsearch multisearch request failed" + System.lineSeparator() + getExceptionMessageForFailures(failedRequests));
        }
        
        
        //close client
        return null;
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
        SearchSourceBuilder source = new SearchSourceBuilder();
        source.timeout(this.timeOut);
        source.from(0); //retrieve all hits
        source.size(Integer.MAX_VALUE);

        SearchRequest request = new SearchRequest();
        request.indices(this.indexName);
        request.source(source);

        return request;
    }
    
        private void processResponse(DataAccessResourceSearchBody searchBody, MultiSearchResponse searchResponse){
        List<AbstractSubsetDefinition> inputs = searchBody.getInputs();
            
        for(int i = 0; i < inputs.size(); i++){
            AbstractSubsetDefinition input = inputs.get(i);
            SearchResponse response = searchResponse.getResponses()[i].getResponse();
            List<AbstractDataEnvelope> responseDataEnvelopes = processHits(response.getHits());
            
            ResponseHelper reponseHelper = new ResponseHelper(searchBody.getAreaOfInterest(), searchBody.getTimeFrame(), input, responseDataEnvelopes);
            
            //ToDO convert to AbstractResource
        }
        
    }
        
    private List<AbstractDataEnvelope> processHits(SearchHits hits){
        List<AbstractDataEnvelope> responseDataEnvelopes = new ArrayList<>();
        
        hits.forEach( hit -> {
            String jsonResponse = hit.getSourceAsString();
            //TODO convert json to object
                //add to list
        });
        
        return responseDataEnvelopes;
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
    
    private String getExceptionMessageForFailures(List<Exception> failures){
        StringBuilder sb = new StringBuilder();
 
        for(int i = 0; i < failures.size(); i++){
            if(i != 0){
                sb.append(System.lineSeparator());
            }
            
            sb.append("Exception ").append(i).append(":").append(System.lineSeparator()).append(failures.get(i).getMessage());
        }
        
        return sb.toString();
    }
    

    
    

}
