/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

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

    private final static String PRODUCTCOLLECTION_ATTRIBUTE = "productCollection";
    private final static String SERVICEURL_ATTRIBUTE = "serviceName";
    private final static String PRODUCTTYPE_ATTRIBUTE = "productType";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof WacodisProductSubsetDefinition) {
            List<QueryBuilder> queries = new ArrayList<>();
            WacodisProductSubsetDefinition productSubset = (WacodisProductSubsetDefinition) subset;

            QueryBuilder serviceURLFilter = QueryBuilders.termQuery(SERVICEURL_ATTRIBUTE, productSubset.getServiceUrl());
            queries.add(serviceURLFilter);
            QueryBuilder collectionFilter = QueryBuilders.termQuery(PRODUCTCOLLECTION_ATTRIBUTE, productSubset.getProductCollection());
            queries.add(collectionFilter);
            
            if(productSubset.getProductType() != null && !productSubset.getProductType().trim().isEmpty()){
                QueryBuilder productTypeFilter = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, productSubset.getProductType());
                queries.add(productTypeFilter);
            }

            return queries;
        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + DwdSubsetDefinition.class.getSimpleName());
        }
    }

}
