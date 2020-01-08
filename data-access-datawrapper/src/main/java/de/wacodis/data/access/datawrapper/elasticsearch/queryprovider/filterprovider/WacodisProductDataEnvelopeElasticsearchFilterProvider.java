/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend.BackendTypeFilterFactory;
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

    private final static String PRODUCTTYPE_ATTRIBUTE = "productType";
    private final static String PROCESS_ATTRIBUTE = "process";
    private final static String DATAENVELOPEREFERENCES_ATTRIBUTE = "dataEnvelopeReferences";
    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof WacodisProductDataEnvelope) {
            WacodisProductDataEnvelope wacEnv = (WacodisProductDataEnvelope) envelope;
            List<QueryBuilder> queries = new ArrayList<>();

            QueryBuilder processQuery = QueryBuilders.termQuery(PROCESS_ATTRIBUTE, wacEnv.getProcess());
            queries.add(processQuery);
            QueryBuilder productTypeQuery = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, wacEnv.getProductType());
            queries.add(productTypeQuery);
            QueryBuilder dataEnvReferencesQuery = QueryBuilders.termsQuery(DATAENVELOPEREFERENCES_ATTRIBUTE, wacEnv.getDataEnvelopeReferences()); // TODO should be 'and' instead of 'or' operator
            queries.add(dataEnvReferencesQuery);
            queries.addAll(BackendTypeFilterFactory.getFilterForBackend(wacEnv.getServiceDefinition()));

            return queries;
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + WacodisProductDataEnvelope.class.getSimpleName());
        }
    }

    
    private List<QueryBuilder> getQueryForDataEnvelopeReference(List<String> dataEnvelopeIDs){
        List<QueryBuilder> queries = new ArrayList<>();
        
        for(String dataEnvelopeID : dataEnvelopeIDs){
            queries.add(QueryBuilders.termQuery(DATAENVELOPEREFERENCES_ATTRIBUTE, dataEnvelopeID));
        }
        
        return queries;
    }
}
