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
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend;

import de.wacodis.dataaccess.model.ArcGISImageServerBackend;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;

/**
 *
 * @author Arne
 */
public class ArcGISImageServerBackendFilterProvider implements ProductBackendFilterProvider<ArcGISImageServerBackend> {

    private static final String PRODUCTCOLLECTION_ATTRIBUTE = "serviceDefinition.productCollection";
    private static final String BASEURL_ATTRIBUTE = "serviceDefinition.baseUrl";
    private static final String SERVICETYPES_ATTRIBUTE = "serviceDefinition.serviceTypes";

    @Override
    public List<QueryBuilder> getFiltersForBackend(ArcGISImageServerBackend backend) {
        List<QueryBuilder> queries = new ArrayList<>();
        AbstractBackendFilterProvider abstractFilterProvider = new AbstractBackendFilterProvider();

        queries.addAll(abstractFilterProvider.getFiltersForBackend(backend)); //filters for attributes of super type
        queries.add(QueryBuilders.termQuery(PRODUCTCOLLECTION_ATTRIBUTE, backend.getProductCollection()));
        queries.add(QueryBuilders.termQuery(BASEURL_ATTRIBUTE, backend.getBaseUrl()));

        List<QueryBuilder> serviceTypeQuery = QueryBuilders.boolQuery().filter(); //match all service types
        serviceTypeQuery.addAll(getQueriesForServiceTypes(backend.getServiceTypes()));
        queries.addAll(serviceTypeQuery);
         //script query ensures that matching data envelope (serviceDefinition) in elasticsearch index does not contain more serviceTypes than backend
        String matchCountScript = String.format("doc['%s'].values.length == %d", SERVICETYPES_ATTRIBUTE, backend.getServiceTypes().size());
        queries.add(QueryBuilders.scriptQuery(new Script(matchCountScript)));

        return queries;
    }

    @Override
    public Class<ArcGISImageServerBackend> supportedBackendType() {
        return ArcGISImageServerBackend.class;
    }

    private List<QueryBuilder> getQueriesForServiceTypes(List<String> serviceTypes) {
        List<QueryBuilder> queries = new ArrayList<>();

        serviceTypes.forEach((serviceType) -> {
            queries.add(QueryBuilders.termQuery(SERVICETYPES_ATTRIBUTE, serviceType));
        });

        return queries;
    }

}
