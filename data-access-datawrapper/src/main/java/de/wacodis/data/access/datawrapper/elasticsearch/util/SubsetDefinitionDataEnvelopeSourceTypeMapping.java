/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.LoggerFactory;

/**
 * mapping for SubsetDefinition source type and corresponding DataEnvelope source type
 * @author Arne
 */
public class SubsetDefinitionDataEnvelopeSourceTypeMapping {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SubsetDefinitionDataEnvelopeSourceTypeMapping.class);
    
    private static final Map<AbstractSubsetDefinition.SourceTypeEnum, AbstractDataEnvelope.SourceTypeEnum> SOURCETYPEMAPPING = new HashMap<AbstractSubsetDefinition.SourceTypeEnum, AbstractDataEnvelope.SourceTypeEnum>(){{
        put(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION, AbstractDataEnvelope.SourceTypeEnum.GDIDEDATAENVELOPE); //CatalogueSubsetDefintion
        put(AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION, AbstractDataEnvelope.SourceTypeEnum.DWDDATAENVELOPE); //DWDSubsetDefiontion
        put(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION, AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE); //CopernicusSubsetDefinition
        put(AbstractSubsetDefinition.SourceTypeEnum.SENSORWEBSUBSETDEFINITION, AbstractDataEnvelope.SourceTypeEnum.SENSORWEBDATAENVELOPE); //SensorWebSubsetDefiontion
        put(AbstractSubsetDefinition.SourceTypeEnum.WACODISPRODUCTSUBSETDEFINITION, AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE); //WacodisProductSubsetDefintion
    }};
    
    /**
     * get corresponding DataEnvelope source type for SubsetDefinition source type, empty return value if SubsetDefinition source type has no mapping or is unknown
     * @param subsetDefinitionType
     * @return 
     */
    public static Optional<AbstractDataEnvelope.SourceTypeEnum> getCorrespondingDataEnvelopeSourceType(AbstractSubsetDefinition.SourceTypeEnum subsetDefinitionType){
        AbstractDataEnvelope.SourceTypeEnum dataEnvType = SOURCETYPEMAPPING.get(subsetDefinitionType);
        
        if(dataEnvType != null){
            return Optional.of(dataEnvType);
        }else{
            LOGGER.debug("no mapping found for SubsetDefinition Source Type {}",  subsetDefinitionType);
            return Optional.empty();
        }
    }
    
}
