/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.CatalogueSubsetDefinition;
import de.wacodis.dataaccess.model.CopernicusSubsetDefinition;
import de.wacodis.dataaccess.model.SensorWebSubsetDefinition;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class FilterProviderFactory {
   
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FilterProviderFactory.class);
    
    public SubsetDefinitionElasticsearchFilterProvider getFilterProviderForSubsetDefinition(AbstractSubsetDefinition subset){      
        
        if(subset instanceof CopernicusSubsetDefinition){
            return new CopernicusSubsetDefinitionElasticsearchFilterProvider();
        }else if(subset instanceof CatalogueSubsetDefinition){
            return new CatalogueSubsetDefinitionElasticsearchFilterProvider();
        }else if(subset instanceof SensorWebSubsetDefinition){
            return new SensorWebSubsetDefinitionElasticsearchFilterProvider();
        }else{
            LOGGER.warn("no specific " + SubsetDefinitionElasticsearchFilterProvider.class.getSimpleName() + " for SubsetDefinition " + subset.getIdentifier() + " of type " + subset.getClass().getSimpleName() + " available, return empty filter list");
            return new NullElasticsearchFilterProvider();
        }
        
    }
    
    
}
