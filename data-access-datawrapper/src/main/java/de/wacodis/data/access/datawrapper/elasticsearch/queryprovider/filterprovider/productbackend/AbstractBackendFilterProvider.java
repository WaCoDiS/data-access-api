/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend;

import de.wacodis.dataaccess.model.AbstractBackend;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author Arne
 */
public class AbstractBackendFilterProvider implements ProductBackendFilterProvider<AbstractBackend> {

    private final static String BACKENDTYPE_ATTRIBUTE = "serviceDefinition.backendType";

    @Override
    public List<QueryBuilder> getFiltersForBackend(AbstractBackend backend) {
        return Arrays.asList(QueryBuilders.termQuery(BACKENDTYPE_ATTRIBUTE, backend.getBackendType()));
    }

    @Override
    public Class<AbstractBackend> supportedBackendType() {
        return AbstractBackend.class;
    }

}
