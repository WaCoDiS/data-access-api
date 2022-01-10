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
