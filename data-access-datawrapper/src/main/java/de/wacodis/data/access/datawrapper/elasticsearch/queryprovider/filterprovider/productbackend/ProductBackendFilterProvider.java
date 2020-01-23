/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend;

import de.wacodis.dataaccess.model.AbstractBackend;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;

/**
 *
 * @author Arne
 * @param <T>
 */
public interface ProductBackendFilterProvider<T extends AbstractBackend> {
    

    List<QueryBuilder> getFiltersForBackend(T backend);

    Class<T> supportedBackendType();
}
