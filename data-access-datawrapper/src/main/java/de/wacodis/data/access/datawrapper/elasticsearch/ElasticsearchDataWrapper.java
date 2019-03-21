/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch;

import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.DataAccessSearchBodyElasticsearchBoolQueryProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.ElasticsearchQueryProvider;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.DataAccessResourceSearchBody;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchDataWrapper {

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
        
        
        for(AbstractSubsetDefinition subset: searchBody.getInputs()){
            QueryBuilder query = this.queryProvider.buildQueryForSubsetDefinition(subset, searchBody.getAreaOfInterest(), searchBody.getTimeFrame());
            
            SearchSourceBuilder source = new SearchSourceBuilder();
            source.timeout(this.timeOut);
            source.from(0);
            source.size(Integer.MAX_VALUE);
          
            SearchRequest request = new SearchRequest();
            request.indices(this.indexName);
            request.source(source);
            
            SearchResponse response = this.elasticsearchClient.search(request, RequestOptions.DEFAULT);
            
            for(SearchHit hit : response.getHits()){
                Map<String, Object> hitSource = hit.getSourceAsMap();
                System.out.println(hitSource.toString());
            }
            
        }
        
        //close client
        return null;
    }

    
}
