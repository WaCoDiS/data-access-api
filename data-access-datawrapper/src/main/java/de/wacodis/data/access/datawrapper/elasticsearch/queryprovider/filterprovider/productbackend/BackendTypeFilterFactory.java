/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
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
