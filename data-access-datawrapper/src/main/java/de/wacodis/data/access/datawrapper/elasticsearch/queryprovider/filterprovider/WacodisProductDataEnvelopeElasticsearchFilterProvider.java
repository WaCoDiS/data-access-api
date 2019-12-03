/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import java.util.ArrayList;
import java.util.Collections;
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
    private final static String BACKENDTYPE_ATTRIBUTE = "backendType";

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof WacodisProductDataEnvelope) {
            WacodisProductDataEnvelope wacEnv = (WacodisProductDataEnvelope) envelope;
            List<QueryBuilder> queries = new ArrayList<>();

            //QueryBuilder productCollectionQuery = QueryBuilders.termQuery(PRODUCTCOLLECTION_ATTRIBUTE, wacEnv.getProductCollection());
            QueryBuilder processQuery = QueryBuilders.termQuery(PROCESS_ATTRIBUTE, wacEnv.getProcess());
            queries.add(processQuery);
            QueryBuilder backendTypeQuery = QueryBuilders.termQuery(BACKENDTYPE_ATTRIBUTE, wacEnv.getServiceDefinition().getBackendType().name());

            if (wacEnv.getProductType() != null && !wacEnv.getProductType().trim().isEmpty()) {
                QueryBuilder productTypeQuery = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, wacEnv.getProductType());
                queries.add(productTypeQuery);
            }

            return queries;
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + WacodisProductDataEnvelope.class.getSimpleName());
        }
    }

}
