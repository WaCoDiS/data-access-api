/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.DwdSubsetDefinition;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DWDSubsetDefinitionElasticsearchFilterProvider implements SubsetDefinitionElasticsearchFilterProvider {

    private final static String SERVICEURL_ATTRIBUTE = "serviceUrl";
    private final static String LAYERNAME_ATTRIBUTE = "layerName";

    @Override
    public List<QueryBuilder> buildFiltersForSubsetDefinition(AbstractSubsetDefinition subset) {
        if (subset instanceof DwdSubsetDefinition) {
            DwdSubsetDefinition dwdSubset = (DwdSubsetDefinition) subset;

            QueryBuilder serviceFilter = QueryBuilders.termQuery(SERVICEURL_ATTRIBUTE, dwdSubset.getServiceUrl());
            QueryBuilder layerFilter = QueryBuilders.termQuery(LAYERNAME_ATTRIBUTE, dwdSubset.getLayerName()); 

            return Arrays.asList(new QueryBuilder[]{serviceFilter, layerFilter});

        } else {
            throw new IllegalArgumentException("wrong type of SubsetDefinition, subset is of type " + subset.getClass().getSimpleName() + ", expected " + DwdSubsetDefinition.class.getSimpleName());
        }
    }

}
