/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.exploreDataEnvelopes;

import de.wacodis.dataaccess.model.DataEnvelopeQueryQueryParams;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.slf4j.LoggerFactory;

/**
 * converts query parameter to elasticsearch filter
 *
 * @author Arne
 */
public class SimpleQueryParameterFilterProvider implements QueryParameterFilterProvider {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SimpleQueryParameterFilterProvider.class);

    @Override
    public QueryBuilder buildFilterForQueryParam(String fieldName, DataEnvelopeQueryQueryParams queryParam) {
        QueryBuilder filter;

        switch (queryParam.getComparator()) {

            case EQUALS:
                filter = new TermQueryBuilder(fieldName, queryParam.getValue());
                break;
            case GREATER:
                filter = new RangeQueryBuilder(fieldName).gt(queryParam.getValue());
                break;
            case LESSER:
                filter = new RangeQueryBuilder(fieldName).lt(queryParam.getValue());
                break;
            case GREATEROREQUALS:
                filter = new RangeQueryBuilder(fieldName).gte(queryParam.getValue());
                break;
            case LESSEROREQUALS:
                filter = new RangeQueryBuilder(fieldName).lte(queryParam.getValue());
                break;
            case NOT:
                filter = new BoolQueryBuilder().mustNot(new TermQueryBuilder(fieldName, queryParam.getValue()));
                break;
            default:
                LOGGER.error("handling of comparator {} is not implemented", queryParam.getComparator().toString());
                throw new IllegalArgumentException("unknown comparator " + queryParam.getComparator().toString() + ", cannot build query for query parameter " + fieldName);
        }

        return filter;
    }

}
