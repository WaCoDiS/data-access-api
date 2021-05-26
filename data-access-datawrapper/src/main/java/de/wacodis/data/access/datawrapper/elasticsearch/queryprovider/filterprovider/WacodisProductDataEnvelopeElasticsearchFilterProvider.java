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
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend.BackendTypeFilterFactory;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;

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
            queries.addAll(BackendTypeFilterFactory.getFilterForBackend(wacEnv.getServiceDefinition())); //match backend (serviceDefinition)

            List<QueryBuilder> dataEnvReferencesQuery = QueryBuilders.boolQuery().must(); //match all dataenvelope references
            dataEnvReferencesQuery.addAll(getQueriesForDataEnvelopeReferences(wacEnv.getDataEnvelopeReferences()));
            queries.addAll(dataEnvReferencesQuery);
            //script query ensures that matching data envelope in elasticsearch index does not contain more dataEnvelopeReferences than wacEnv
            String matchCountScript = String.format("doc['%s'].values.length == %d", DATAENVELOPEREFERENCES_ATTRIBUTE, wacEnv.getDataEnvelopeReferences().size()); 
            queries.add(QueryBuilders.scriptQuery(new Script(matchCountScript)));

            return queries;
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + WacodisProductDataEnvelope.class.getSimpleName());
        }
    }

    private List<QueryBuilder> getQueriesForDataEnvelopeReferences(List<String> dataEnvelopeIDs) {
        List<QueryBuilder> queries = new ArrayList<>();

        for (String dataEnvelopeID : dataEnvelopeIDs) {
            queries.add(QueryBuilders.termQuery(DATAENVELOPEREFERENCES_ATTRIBUTE, dataEnvelopeID));
        }

        return queries;
    }
}
