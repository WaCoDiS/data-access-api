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
import de.wacodis.data.access.datawrapper.DataEnvelopeSearcher;
import de.wacodis.data.access.datawrapper.RequestResponse;
import de.wacodis.data.access.datawrapper.RequestResult;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.DataEnvelopeElasticsearchFilterProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.FilterProviderFactory;
import de.wacodis.data.access.datawrapper.elasticsearch.util.AreaOfInterestConverter;
import de.wacodis.data.access.datawrapper.elasticsearch.util.DataEnvelopeJsonDeserializerFactory;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ElasticsearchDataEnvelopeSearcher implements DataEnvelopeSearcher {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDataEnvelopeSearcher.class);

    private static final int DEFAULTTIMEOUT_MILLIS = 10000;
    private static final String DATAENVELOPE_SOURCETYPE_ATTRIBUTE = "sourceType";

    private RestHighLevelClient elasticsearchClient;
    private String type;
    private String indexName;
    private TimeValue requestTimeout;
    private final DataEnvelopeJsonDeserializerFactory jsonDeserializerFactory;
    private final FilterProviderFactory filterProviderFactory;

    public ElasticsearchDataEnvelopeSearcher(RestHighLevelClient elasticsearchClient, String indexName, long requestTimeout_millis) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
        this.requestTimeout = new TimeValue(requestTimeout_millis, TimeUnit.MILLISECONDS);
        this.jsonDeserializerFactory = new DataEnvelopeJsonDeserializerFactory();
        this.filterProviderFactory = new FilterProviderFactory();
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
    public RequestResponse<AbstractDataEnvelope> retrieveIdForDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException {
        SearchRequest request = buildSearchRequest(dataEnvelope);
        SearchResponse response = this.elasticsearchClient.search(request, RequestOptions.DEFAULT);
        Optional<AbstractDataEnvelope> matchedDataEnvelope = processSearchResponse(response);

        return new RequestResponse(RequestResult.OK, matchedDataEnvelope);
    }

    private SearchRequest buildSearchRequest(AbstractDataEnvelope dataEnvelope) {
        DataEnvelopeElasticsearchFilterProvider filterProvider = this.filterProviderFactory.getFilterProviderForDataEnvelope(dataEnvelope);
        List<QueryBuilder> filters = filterProvider.buildFiltersForDataEnvelope(dataEnvelope);
        QueryBuilder query = appendFiltersToQuery(dataEnvelope, filters);
        LOGGER.debug("dataenvelope search: prepare elasticsearch query for data envelope of type {}, query:\n{}", dataEnvelope.getSourceType(), query);

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.timeout(this.requestTimeout);
        source.from(0); //retrieve all hits
        source.size(1); //only get first hit
        source.fetchSource(true); //IDs only
        source.query(query);

        SearchRequest request = new SearchRequest();
        request.indices(this.indexName);
        request.source(source);

        return request;
    }

    private GetRequest buildGetRequest(String identifier) {
        GetRequest request = new GetRequest();
        request.id(identifier);
        request.index(this.indexName);

        return request;
    }

    private Optional<AbstractDataEnvelope> processGetResponse(GetResponse response) throws IOException {
        if (response.isExists() && !response.isSourceEmpty()) {
            String dataEnvelopeJson = response.getSourceAsString();
            AbstractDataEnvelope responseDataEnvelope = deserializeDataEnvelope(dataEnvelopeJson);
            responseDataEnvelope.setIdentifier(response.getId());

            return Optional.of(responseDataEnvelope);
        } else {
            LOGGER.info("response of get request did not contain items");
            return Optional.empty();
        }
    }

    private QueryBuilder appendFiltersToQuery(AbstractDataEnvelope dataEnvelope, List<QueryBuilder> filters) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        filters.forEach(filter -> boolQuery.filter(filter));
        //match source type 
        QueryBuilder srcTypeFilter = getSourceTypeFilter(dataEnvelope);
        boolQuery.filter(srcTypeFilter); 
        
        return boolQuery;
    }

    private Optional<AbstractDataEnvelope> processSearchResponse(SearchResponse response) throws IOException {
        SearchHits hits = response.getHits();

        if (hits.getTotalHits() > 0) {
            if (hits.getTotalHits() > 1) {
                LOGGER.warn("only expected 1 hit, but got " + hits.getTotalHits() + "hits, only first hit is considered");
            }

            SearchHit hit = hits.getAt(0); //first hit
            String hitIdentifier = hit.getId();
            String matchJson = hit.getSourceAsString();
            AbstractDataEnvelope matchedDataEnvelope = deserializeDataEnvelope(matchJson);
            matchedDataEnvelope.setIdentifier(hitIdentifier);
            return Optional.of(matchedDataEnvelope);
        }else{
            return Optional.empty();
        }
    }

    private AbstractDataEnvelope deserializeDataEnvelope(String dataEnvelopeJson) throws IOException {
        ObjectMapper mapper = this.jsonDeserializerFactory.getObjectMapper(dataEnvelopeJson);
        AbstractDataEnvelope dataEnvelope = mapper.readValue(dataEnvelopeJson, AbstractDataEnvelope.class);
        
        //format Extent
        GeoShapeCompatibilityAreaOfInterest compatibilityAOI = (GeoShapeCompatibilityAreaOfInterest)dataEnvelope.getAreaOfInterest();
        AbstractDataEnvelopeAreaOfInterest defaultAOI = AreaOfInterestConverter.getDefaultAreaOfInterest(compatibilityAOI);
        dataEnvelope.setAreaOfInterest(defaultAOI);
        

        return dataEnvelope;
    }
    
    
    /**
     * match sourceType
     * @param dataEnvelope
     * @return 
     */
    private QueryBuilder getSourceTypeFilter(AbstractDataEnvelope dataEnvelope){
        return QueryBuilders.termQuery(DATAENVELOPE_SOURCETYPE_ATTRIBUTE, dataEnvelope.getSourceType().toString());
    }
}
