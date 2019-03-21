/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.CatalogueSubsetDefinition;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CatalogueSubsetDefinitionElasticsearchFilterProvider implements SubsetDefinitionElasticsearchFilterProvider {
    
    private static final String URLFILTER_ATTRIBUTE = "catalougeUrl";
    private static final String RECORDIDFILTER_ATTRIBUTE = "recordRefId";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof CatalogueSubsetDefinition) {
            CatalogueSubsetDefinition catalogueSubset = (CatalogueSubsetDefinition) subset;
            List<QueryBuilder> filters = new ArrayList<>();

            QueryBuilder urlFilter = QueryBuilders.termQuery(URLFILTER_ATTRIBUTE, catalogueSubset.getServiceUrl());
            QueryBuilder recordIDFilter = QueryBuilders.termQuery(RECORDIDFILTER_ATTRIBUTE, catalogueSubset.getDatasetIdentifier());

            filters.add(urlFilter);
            filters.add(recordIDFilter);

            return filters;
        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + CatalogueSubsetDefinition.class.getSimpleName());
        }
    }

}
