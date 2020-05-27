/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.data.access.datawrapper.DataEnvelopeExplorer;
import de.wacodis.data.access.datawrapper.RequestResponse;
import de.wacodis.data.access.datawrapper.RequestResult;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.CommonAttributeFilterUtil;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.exploreDataEnvelopes.QueryParameterFilterProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.exploreDataEnvelopes.SimpleQueryParameterFilterProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.DataEnvelopeElasticsearchFilterProvider;
import de.wacodis.data.access.datawrapper.elasticsearch.util.AreaOfInterestConverter;
import de.wacodis.data.access.datawrapper.elasticsearch.util.DataEnvelopeJsonDeserializerFactory;
import de.wacodis.data.access.datawrapper.elasticsearch.util.ElasticsearchCompatibilityDataEnvelopeSerializer;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.DataEnvelopeQuery;
import de.wacodis.dataaccess.model.extension.elasticsearch.GeoShapeCompatibilityAreaOfInterest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

/**
 *
 * @author Arne
 */
public class ElasticSearchDataEnvelopeExplorer implements DataEnvelopeExplorer {

    private static final int DEFAULTTIMEOUT_MILLIS = 10000;

    private RestHighLevelClient elasticsearchClient;
    private String indexName;
    private TimeValue requestTimeout;
    private final DataEnvelopeJsonDeserializerFactory jsonDeserializerFactory;
    private final ElasticsearchCompatibilityDataEnvelopeSerializer dataEnvelopeSerializer;
    private String dateFormat;
    private QueryParameterFilterProvider paramFilterProvider;

    public ElasticSearchDataEnvelopeExplorer(RestHighLevelClient elasticsearchClient, String indexName, long requestTimeout_Millies) {
        this(elasticsearchClient, indexName, requestTimeout_Millies, CommonAttributeFilterUtil.getDefaultDateFormat());
    }

    public ElasticSearchDataEnvelopeExplorer(RestHighLevelClient elasticsearchClient, String indexName) {
        this(elasticsearchClient, indexName, DEFAULTTIMEOUT_MILLIS, CommonAttributeFilterUtil.getDefaultDateFormat());
    }

    public ElasticSearchDataEnvelopeExplorer(RestHighLevelClient elasticsearchClient, String indexName, long requestTimeout_Millies, String dateFormat) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
        this.dateFormat = dateFormat;
        this.paramFilterProvider = new SimpleQueryParameterFilterProvider();
        this.jsonDeserializerFactory = new DataEnvelopeJsonDeserializerFactory();
        this.dataEnvelopeSerializer = new ElasticsearchCompatibilityDataEnvelopeSerializer();
        this.requestTimeout = TimeValue.timeValueMillis(requestTimeout_Millies);
    }

    @Override
    public RequestResponse<List<AbstractDataEnvelope>> queryDataEnvelopes(DataEnvelopeQuery query) {
        RequestResponse<List<AbstractDataEnvelope>> requestResponse;

        try {
            List<QueryBuilder> filters = buildFilters(query);
            SearchRequest request = buildSearchRequest(filters);
            //query index
            SearchResponse searchResponse = this.elasticsearchClient.search(request, RequestOptions.DEFAULT);
            List<AbstractDataEnvelope> responseDataEnvelopes = processSearchResponse(searchResponse);

            requestResponse = new RequestResponse(RequestResult.OK, Optional.of(responseDataEnvelopes));
        } catch (Exception e) {
            requestResponse = new RequestResponse(RequestResult.ERROR, Optional.empty());
            requestResponse.addException(e);
        }

        return requestResponse;
    }

    private List<QueryBuilder> buildFilters(DataEnvelopeQuery query) {
        List<QueryBuilder> filters = new ArrayList<>();

        //bbox filter
        if (query.getAreaOfInterest() != null) {
            filters.add(CommonAttributeFilterUtil.getSpatialBBOXFilter(query.getAreaOfInterest()));
        }
        //time frame filters
        if (query.getTimeFrame() != null) {
            if (query.getTimeFrame().getStartTime() != null) {
                filters.add(CommonAttributeFilterUtil.getTimeFrameStartTimeFilter(query.getTimeFrame(), this.dateFormat));
            }
            if (query.getTimeFrame().getEndTime() != null) {
                filters.add(CommonAttributeFilterUtil.getTimeFrameEndTimeFilter(query.getTimeFrame(), this.dateFormat));
            }
        }

        //filter for query params
        query.getQueryParams().entrySet().forEach((param) -> {
            filters.add(this.paramFilterProvider.buildFilterForQueryParam(param.getKey(), param.getValue()));
        });

        return filters;
    }

    private SearchRequest buildSearchRequest(List<QueryBuilder> filters) {
        QueryBuilder query = assembleQuery(filters);

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.timeout(this.requestTimeout);
        source.from(0); //retrieve all hits
        source.size(10000); //10000 = max allowed value
        source.fetchSource(true);
        source.query(query);

        SearchRequest request = new SearchRequest();
        request.indices(this.indexName);
        request.source(source);

        return request;
    }

    private QueryBuilder assembleQuery(List<QueryBuilder> filters) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        filters.forEach(filter -> boolQuery.filter(filter));

        return boolQuery;
    }

    private List<AbstractDataEnvelope> processSearchResponse(SearchResponse response) throws IOException {
        List<AbstractDataEnvelope> responseEnvelopes = new ArrayList<>();
        SearchHits hits = response.getHits();

        for (SearchHit hit : hits) {
            String hitIdentifier = hit.getId();
            String hitJson = hit.getSourceAsString();
            AbstractDataEnvelope hitDataEnvelope = deserializeDataEnvelope(hitJson);
            hitDataEnvelope.setIdentifier(hitIdentifier);
            responseEnvelopes.add(hitDataEnvelope);
        }

        return responseEnvelopes;
    }

    private AbstractDataEnvelope deserializeDataEnvelope(String dataEnvelopeJson) throws IOException {
        ObjectMapper mapper = this.jsonDeserializerFactory.getObjectMapper(dataEnvelopeJson);
        AbstractDataEnvelope dataEnvelope = mapper.readValue(dataEnvelopeJson, AbstractDataEnvelope.class);

        //format Extent
        GeoShapeCompatibilityAreaOfInterest compatibilityAOI = (GeoShapeCompatibilityAreaOfInterest) dataEnvelope.getAreaOfInterest();
        AbstractDataEnvelopeAreaOfInterest defaultAOI = AreaOfInterestConverter.getDefaultAreaOfInterest(compatibilityAOI);
        dataEnvelope.setAreaOfInterest(defaultAOI);

        return dataEnvelope;
    }
}
