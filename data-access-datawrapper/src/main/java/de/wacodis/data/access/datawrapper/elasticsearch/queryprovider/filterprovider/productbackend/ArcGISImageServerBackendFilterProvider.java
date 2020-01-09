/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend;

import de.wacodis.dataaccess.model.ArcGISImageServerBackend;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

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
