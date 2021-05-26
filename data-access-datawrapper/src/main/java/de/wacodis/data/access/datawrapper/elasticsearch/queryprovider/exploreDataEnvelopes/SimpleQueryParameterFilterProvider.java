/*
 * Copyright 2018-2021 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.exploreDataEnvelopes;

import de.wacodis.dataaccess.model.DataEnvelopeQueryQueryParams;
import static de.wacodis.dataaccess.model.DataEnvelopeQueryQueryParams.ComparatorEnum.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
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
        Object value = queryParam.getValue();
        DataEnvelopeQueryQueryParams.ComparatorEnum comparator = queryParam.getComparator();

        //null value only allowed combined with comparator 'EQUALS' or 'NOT'
        if (value == null && !(comparator.equals(EQUALS) || comparator.equals(NOT))) {
            LOGGER.error("illegal null value for field '{}'", fieldName);
            throw new IllegalArgumentException("null value of query param '" + fieldName + "' is not allowed combined with comparator '" + comparator.toString() + "', cannot build query for query parameter '" + fieldName + "'");
        }

        switch (comparator) {

            case EQUALS:
                if (value != null) {
                    filter = new TermQueryBuilder(fieldName, queryParam.getValue());
                } else { //field must not exist if source value is null
                    filter = new BoolQueryBuilder().mustNot(new ExistsQueryBuilder(fieldName));
                }
                break;
            case GREATER:
                filter = new RangeQueryBuilder(fieldName).gt(value);
                break;
            case LESSER:
                filter = new RangeQueryBuilder(fieldName).lt(value);
                break;
            case GREATEROREQUALS:
                filter = new RangeQueryBuilder(fieldName).gte(value);
                break;
            case LESSEROREQUALS:
                filter = new RangeQueryBuilder(fieldName).lte(value);
                break;
            case NOT:
                if (value != null) {
                    filter = new BoolQueryBuilder().mustNot(new TermQueryBuilder(fieldName, value));
                } else { //field must exist if source value is not null
                    filter = new ExistsQueryBuilder(fieldName);
                }
                break;
            default:
                LOGGER.error("handling of comparator '{}' is not implemented", queryParam.getComparator().toString());
                throw new IllegalArgumentException("unknown comparator '" + queryParam.getComparator().toString() + "', cannot build query for query parameter '" + fieldName + "'");
        }

        return filter;
    }

}
