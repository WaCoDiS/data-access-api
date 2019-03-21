/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import org.elasticsearch.index.query.QueryBuilder;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface ElasticsearchQueryProvider {
    
    QueryBuilder buildQueryForSubsetDefinition(AbstractSubsetDefinition subsetDefinition, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame);
    
}
