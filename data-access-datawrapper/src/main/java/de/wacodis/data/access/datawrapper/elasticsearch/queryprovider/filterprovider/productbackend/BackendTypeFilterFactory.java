/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider.productbackend;

import de.wacodis.dataaccess.model.AbstractBackend;
import de.wacodis.dataaccess.model.ArcGISImageServerBackend;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class BackendTypeFilterFactory {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BackendTypeFilterFactory.class);
    
    private static final ArcGISImageServerBackendFilterProvider IMAGESERVERPROVIDER = new ArcGISImageServerBackendFilterProvider();
    private static final AbstractBackendFilterProvider ABSTRACTPROVIDER = new AbstractBackendFilterProvider();
    
    public static List<QueryBuilder> getFilterForBackend(AbstractBackend backend){
        
        if(backend instanceof ArcGISImageServerBackend){
            return IMAGESERVERPROVIDER.getFiltersForBackend(((ArcGISImageServerBackend) backend));
        }else{
            LOGGER.warn("unknown backend type {}, only filter attributes of super type AbstractBackend, backend might not be entirely equal", backend.getClass().getSimpleName());
            return ABSTRACTPROVIDER.getFiltersForBackend(backend);
        }
        
        
    }
    
}
