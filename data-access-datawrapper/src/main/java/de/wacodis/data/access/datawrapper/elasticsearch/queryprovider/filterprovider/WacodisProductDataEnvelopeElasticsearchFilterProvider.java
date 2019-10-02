/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author Matthes Rieke
 */
public class WacodisProductDataEnvelopeElasticsearchFilterProvider implements DataEnvelopeElasticsearchFilterProvider {
    
    private final static String PRODUCTCOLLECTION_ATTRIBUTE = "productCollection";
    private final static String SERVICENAME_ATTRIBUTE = "serviceName";
    private final static String PRODUCTTYPE_ATTRIBUTE = "productType";

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof WacodisProductDataEnvelope) {
            List<QueryBuilder> queries = new ArrayList<>();
            WacodisProductDataEnvelope wacEnv = (WacodisProductDataEnvelope) envelope;

            QueryBuilder productCollectionQuery = QueryBuilders.termQuery(PRODUCTCOLLECTION_ATTRIBUTE, wacEnv.getProductCollection());
            queries.add(productCollectionQuery);
            QueryBuilder serviceNameQuery = QueryBuilders.termQuery(SERVICENAME_ATTRIBUTE, wacEnv.getServiceName());
            queries.add(serviceNameQuery);
            
            if(wacEnv.getProductType() != null && !wacEnv.getProductType().trim().isEmpty()){
                QueryBuilder productTypeQuery = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, wacEnv.getProductType());
                queries.add(productTypeQuery);
            }
            
            return queries;
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + WacodisProductDataEnvelope.class.getSimpleName());
        }
    }

}
