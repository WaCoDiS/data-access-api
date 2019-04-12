/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.data.access.datawrapper.DataEnvelopeSearcher;
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

        //convert to indexed AreaOfInterest to default AreaOfInterest (Wacodis Models)
        if (responseDataEnvelope.isPresent()) {
            GeoShapeCompatibilityAreaOfInterest geoshapeAreaOfInterest = (GeoShapeCompatibilityAreaOfInterest) responseDataEnvelope.get().getAreaOfInterest();
            AbstractDataEnvelopeAreaOfInterest defaultAreaOfInterest = AreaOfInterestConverter.getDefaultAreaOfInterest(geoshapeAreaOfInterest);
            responseDataEnvelope.get().setAreaOfInterest(defaultAreaOfInterest);
        }

        return responseDataEnvelope;
    }

    @Override
    public Optional<String> retrieveIdForDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException {
        SearchRequest request = buildSearchRequest(dataEnvelope);
        SearchResponse response = this.elasticsearchClient.search(request, RequestOptions.DEFAULT);
        Optional<String> dataEnvelopeId = processSearchResponse(response);

        return dataEnvelopeId;
    }

    private SearchRequest buildSearchRequest(AbstractDataEnvelope dataEnvelope) {
        DataEnvelopeElasticsearchFilterProvider filterProvider = this.filterProviderFactory.getFilterProviderForDataEnvelope(dataEnvelope);
        List<QueryBuilder> filters = filterProvider.buildFiltersForDataEnvelope(dataEnvelope);
        QueryBuilder query = appendFiltersToBoolQuery(filters);

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.timeout(this.requestTimeout);
        source.from(0); //retrieve all hits
        source.size(1); //only retrieve first hit
        source.fetchSource(false); //IDs only
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
            ObjectMapper mapper = this.jsonDeserializerFactory.getObjectMapper(dataEnvelopeJson);
            AbstractDataEnvelope responseDataEnvelope = mapper.readValue(dataEnvelopeJson, AbstractDataEnvelope.class);
            return Optional.of(responseDataEnvelope);
        } else {
            LOGGER.debug("response of get request did not contain items");
            return Optional.empty();
        }
    }

    private QueryBuilder appendFiltersToBoolQuery(List<QueryBuilder> filters) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        filters.forEach(filter -> boolQuery.filter(filter));

        return boolQuery;
    }

    private Optional<String> processSearchResponse(SearchResponse response) throws IOException {
        SearchHits hits = response.getHits();

        if (hits.totalHits > 0) {
            SearchHit firstHit = hits.getHits()[0]; //only first hit
            String hitId = firstHit.getId();
            return Optional.of(hitId);

        } else {
            return Optional.empty(); //no hit, no id
        }
    }

}
