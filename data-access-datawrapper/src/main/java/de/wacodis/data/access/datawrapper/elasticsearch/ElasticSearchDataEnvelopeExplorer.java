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
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class ElasticSearchDataEnvelopeExplorer implements DataEnvelopeExplorer {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDataEnvelopeExplorer.class);

    private static final String IDENTIFIER_ATTRIBUTE = "identifier";
    private static final String INDEX_DOCUMENT_ID_FIELD = "_id";

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
        LOGGER.debug("handle DataEnvelopeQuery:" + System.lineSeparator() + query.toString());

        RequestResponse<List<AbstractDataEnvelope>> requestResponse;

        try {
            List<QueryBuilder> filters = buildFilters(query);
            SearchRequest request = buildSearchRequest(filters);
            LOGGER.debug("created {} filters for DataEnvelope exploration", filters.size());
            //query index
            SearchResponse searchResponse = this.elasticsearchClient.search(request, RequestOptions.DEFAULT);
            List<AbstractDataEnvelope> responseDataEnvelopes = processSearchResponse(searchResponse);

            requestResponse = new RequestResponse(RequestResult.OK, Optional.of(responseDataEnvelopes));

            LOGGER.info("DataEnvelope exploration finished succesfully, {} DataEnvelope(s) found", responseDataEnvelopes.size());
        } catch (Exception e) {
            requestResponse = new RequestResponse(RequestResult.ERROR, Optional.empty());
            requestResponse.addException(e);
            LOGGER.error("DataEnvelope exploration caused error", e);
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
        if (query.getQueryParams() != null) {
            query.getQueryParams().entrySet().forEach((param) -> {
                //if query for 'identifier' match document id (_id) instead of identifier attribute of DataEnvelope
                if (!isIdentifierQuery(param.getKey())) {
                    filters.add(this.paramFilterProvider.buildFilterForQueryParam(param.getKey(), param.getValue()));
                } else {
                    filters.add(this.paramFilterProvider.buildFilterForQueryParam(INDEX_DOCUMENT_ID_FIELD, param.getValue()));
                }
            });
        }

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
    
    private boolean isIdentifierQuery(String paramName){
        return paramName.equals(IDENTIFIER_ATTRIBUTE);
    }
}
