/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.SensorWebSubsetDefinition;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SensorWebSubsetDefinitionElasticsearchFilterProvider implements SubsetDefinitionElasticsearchFilterProvider {
    
    private static final String URLFILTER_ATTRIBUTE = "serviceUrl";
    private static final String FEATUREOFINTERESTFILTER_ATTRIBUTE = "featureOfInterest";
    private static final String PROCEDUREFILTER_ATTRIBUTE = "procedure";
    private static final String OBSERVERDPROPERTYFILTER_ATTRIBUTE = "observedProperty";
    private static final String OFFERINGFILTER_ATTRIBUTE = "offering";
    
    
    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof SensorWebSubsetDefinition) {
            SensorWebSubsetDefinition sensorWebSubset = (SensorWebSubsetDefinition) subset;
            
            QueryBuilder urlFilter = QueryBuilders.termQuery(URLFILTER_ATTRIBUTE, sensorWebSubset.getServiceUrl());
            QueryBuilder featureOfInterestFilter = QueryBuilders.termQuery(FEATUREOFINTERESTFILTER_ATTRIBUTE, sensorWebSubset.getFeatureOfInterest());
            QueryBuilder procedureFilter = QueryBuilders.termQuery(PROCEDUREFILTER_ATTRIBUTE, sensorWebSubset.getProcedure());
            QueryBuilder observedPropertyFilter = QueryBuilders.termQuery(OBSERVERDPROPERTYFILTER_ATTRIBUTE, sensorWebSubset.getObservedProperty());
            QueryBuilder offeringFilter = QueryBuilders.termQuery(OFFERINGFILTER_ATTRIBUTE, sensorWebSubset.getOffering());

            return Arrays.asList(new QueryBuilder[]{urlFilter, featureOfInterestFilter, procedureFilter, observedPropertyFilter, offeringFilter});
        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + SensorWebSubsetDefinition.class.getSimpleName());
        }
    }

}
