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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.LoggerFactory;
import static de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.CommonAttributeFilterUtil.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SubsetDefinitionCommonAttributesElasticsearchBoolQueryProvider implements ElasticsearchQueryProvider {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SubsetDefinitionCommonAttributesElasticsearchBoolQueryProvider.class);

    @Override
    public BoolQueryBuilder buildQueryForSubsetDefinition(AbstractSubsetDefinition subsetDefinition, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        QueryBuilder spatialFilter = getSpatialBBOXFilter(areaOfInterest);
        QueryBuilder startTimeFilter = getTimeFrameStartTimeFilter(timeFrame);
        QueryBuilder endTimeFilter = getTimeFrameEndTimeFilter(timeFrame);
        QueryBuilder srcTypeFilter = getSourceTypeFilter(subsetDefinition);

        boolQuery.filter(srcTypeFilter).filter(spatialFilter).filter(startTimeFilter).filter(endTimeFilter); //add all filters (logical and)

        return boolQuery;
    }

}
