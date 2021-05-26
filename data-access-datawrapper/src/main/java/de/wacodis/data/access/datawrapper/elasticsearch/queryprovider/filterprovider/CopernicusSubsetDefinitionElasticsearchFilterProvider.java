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
import de.wacodis.dataaccess.model.CopernicusSubsetDefinition;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CopernicusSubsetDefinitionElasticsearchFilterProvider implements SubsetDefinitionElasticsearchFilterProvider {

    //mandatory attributes
    private static final String SATELLITEFILTER_ATTRIBUTE = "satellite";
    private static final String CLOUDCOVERAGEFILTER_ATTRIBUTE = "cloudCoverage";
    //optional attributes
    private static final String INSTRUMENT_ATTRIBUTE = "instrument";
    private static final String SENSORMODE_ATTRIBUTE = "sensorMode";
    private static final String PRODUCTTYPE_ATTRIBUTE = "productType";
    private static final String PRODUCTLEVEL_ATTRIBUTE = "productLevel";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof CopernicusSubsetDefinition) {
            CopernicusSubsetDefinition copernicusSubset = (CopernicusSubsetDefinition) subset;
            List<QueryBuilder> filters = new ArrayList<>();
            
            //filters for mandatory attributes
            QueryBuilder satelliteFilter = QueryBuilders.termQuery(SATELLITEFILTER_ATTRIBUTE, copernicusSubset.getSatellite().toString());
            //cloud coverage must be between 0 and maximumCloudCoverage
            QueryBuilder cloudCoverageFilter = QueryBuilders.rangeQuery(CLOUDCOVERAGEFILTER_ATTRIBUTE).gte(0).lte(copernicusSubset.getMaximumCloudCoverage());
            filters.add(satelliteFilter);
            filters.add(cloudCoverageFilter);

            //filters for optional attributes
            if (copernicusSubset.getInstrument() != null) {
                QueryBuilder instrumentFilter = QueryBuilders.termQuery(INSTRUMENT_ATTRIBUTE, copernicusSubset.getInstrument());
                filters.add(instrumentFilter);
            }
            if (copernicusSubset.getSensorMode() != null) {
                QueryBuilder sensorModeFilter = QueryBuilders.termQuery(SENSORMODE_ATTRIBUTE, copernicusSubset.getSensorMode());
                filters.add(sensorModeFilter);
            }
            if (copernicusSubset.getProductType() != null) {
                QueryBuilder productTypeFilter = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, copernicusSubset.getProductType());
                filters.add(productTypeFilter);
            }
            if (copernicusSubset.getProductLevel()!= null) {
                QueryBuilder productLevelFilter = QueryBuilders.termQuery(PRODUCTLEVEL_ATTRIBUTE, copernicusSubset.getProductLevel());
                filters.add(productLevelFilter);
            }

            return filters;
        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + CopernicusSubsetDefinition.class.getSimpleName());
        }
    }

}
