/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    private static final String SATELLITEFILTER_ATTRIBUTE = "satellite";
    private static final String CLOUDCOVERAGEFILTER_ATTRIBUTE = "cloudCoverage";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof CopernicusSubsetDefinition) {
            CopernicusSubsetDefinition copernicusSubset = (CopernicusSubsetDefinition) subset;
            List<QueryBuilder> filters = new ArrayList<>();

            QueryBuilder satelliteFilter = QueryBuilders.termQuery(SATELLITEFILTER_ATTRIBUTE, copernicusSubset.getSatellite().toString());
            QueryBuilder cloudCoverageFilter = QueryBuilders.rangeQuery(CLOUDCOVERAGEFILTER_ATTRIBUTE).gte(0).lte(copernicusSubset.getMaximumCloudCoverage()); //cloud coverage must be between 0 and maximumCloudCoverage

            filters.add(satelliteFilter);
            filters.add(cloudCoverageFilter);

            return filters;

        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + CopernicusSubsetDefinition.class.getSimpleName());
        }
    }

}
