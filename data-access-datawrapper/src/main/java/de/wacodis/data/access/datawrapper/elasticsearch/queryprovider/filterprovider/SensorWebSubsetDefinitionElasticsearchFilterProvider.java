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
