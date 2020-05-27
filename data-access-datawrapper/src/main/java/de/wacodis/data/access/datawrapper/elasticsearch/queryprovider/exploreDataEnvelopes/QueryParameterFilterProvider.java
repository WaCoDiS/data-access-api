/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.exploreDataEnvelopes;

import de.wacodis.dataaccess.model.DataEnvelopeQueryQueryParams;
import org.elasticsearch.index.query.QueryBuilder;

/**
 *
 * @author Arne
 */
public interface QueryParameterFilterProvider {
    
    QueryBuilder buildFilterForQueryParam(String fieldName, DataEnvelopeQueryQueryParams queryParam);
    
}

