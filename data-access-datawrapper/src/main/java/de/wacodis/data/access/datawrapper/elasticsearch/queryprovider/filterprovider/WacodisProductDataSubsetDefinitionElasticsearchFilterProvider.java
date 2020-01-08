/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend.BackendTypeFilterFactory;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.DwdSubsetDefinition;
import de.wacodis.dataaccess.model.WacodisProductSubsetDefinition;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author Arne
 */
public class WacodisProductDataSubsetDefinitionElasticsearchFilterProvider implements SubsetDefinitionElasticsearchFilterProvider {

    private final static String BACKENDTYPE_ATTRIBUTE = "backendType";
    private final static String PRODUCTTYPE_ATTRIBUTE = "productType";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof WacodisProductSubsetDefinition) {
            List<QueryBuilder> queries = new ArrayList<>();
            WacodisProductSubsetDefinition productSubset = (WacodisProductSubsetDefinition) subset;

            QueryBuilder collectionFilter = QueryBuilders.termQuery(BACKENDTYPE_ATTRIBUTE, productSubset.getBackendType());
            queries.add(collectionFilter);
            QueryBuilder productTypeFilter = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, productSubset.getProductType());
            queries.add(productTypeFilter);

            return queries;
        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + DwdSubsetDefinition.class.getSimpleName());
        }
    }

}
