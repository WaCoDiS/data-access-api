/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
