/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CopernicusDataEnvelopeElasticsearchFilterProvider implements DataEnvelopeElasticsearchFilterProvider {
    
    private final static String DATASETID_ATTRIBUTE = "datasetId";
    private final static String SATELLITE_ATTRIBUTE = "satellite";
    private final static String PORTAL_ATTRIBUTE = "portal";

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof CopernicusDataEnvelope) {
            CopernicusDataEnvelope copernicusEnvelope = (CopernicusDataEnvelope) envelope;

            QueryBuilder datasetIdQuery = QueryBuilders.termQuery(DATASETID_ATTRIBUTE, copernicusEnvelope.getDatasetId());
            QueryBuilder satelliteQuery = QueryBuilders.termQuery(SATELLITE_ATTRIBUTE, copernicusEnvelope.getSatellite().toString());
            //QueryBuilder portalQuery = QueryBuilders.termQuery(PORTAL_ATTRIBUTE, copernicusEnvelope.getPortal().toString());
            
            return Arrays.asList(new QueryBuilder[]{datasetIdQuery, satelliteQuery});
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + CopernicusDataEnvelope.class.getSimpleName());
        }
    }

}
