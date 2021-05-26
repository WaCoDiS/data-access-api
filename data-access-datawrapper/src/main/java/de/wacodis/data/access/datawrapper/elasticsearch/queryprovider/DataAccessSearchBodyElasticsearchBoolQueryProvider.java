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
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider;

import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.FilterProviderFactory;
import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.SubsetDefinitionElasticsearchFilterProvider;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataAccessSearchBodyElasticsearchBoolQueryProvider implements ElasticsearchQueryProvider {

    private final SubsetDefinitionCommonAttributesElasticsearchBoolQueryProvider abstractSubsetDefinitionFilterProvider;
    private final FilterProviderFactory filterProviderFactory;

    public DataAccessSearchBodyElasticsearchBoolQueryProvider() {
        this.abstractSubsetDefinitionFilterProvider = new SubsetDefinitionCommonAttributesElasticsearchBoolQueryProvider();
        this.filterProviderFactory = new FilterProviderFactory();
    }
        
    @Override
    public BoolQueryBuilder buildQueryForSubsetDefinition(AbstractSubsetDefinition subset, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        List<QueryBuilder> filters = new ArrayList<>();
        
        filters.addAll(getFilterForCommonAttributes(subset, areaOfInterest, timeFrame));
        filters.addAll(getFiltersForSubsetDefinition(subset));
        
        filters.forEach(filter -> boolQuery.filter(filter)); //add all filters to Query (logical and)
        
        return boolQuery;
    }

    
    private List<QueryBuilder> getFiltersForSubsetDefinition(AbstractSubsetDefinition subset){
         SubsetDefinitionElasticsearchFilterProvider filterProvider = this.filterProviderFactory.getFilterProviderForSubsetDefinition(subset);
         List<QueryBuilder> filters = filterProvider.buildFiltersForSubsetDefinition(subset);
         
        return filters;
    }
    
    private List<QueryBuilder> getFilterForCommonAttributes(AbstractSubsetDefinition subset, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame){
        BoolQueryBuilder abstractAttributesQueries = this.abstractSubsetDefinitionFilterProvider.buildQueryForSubsetDefinition(subset, areaOfInterest, timeFrame);
        
        return abstractAttributesQueries.filter();
    }
    
   
}
