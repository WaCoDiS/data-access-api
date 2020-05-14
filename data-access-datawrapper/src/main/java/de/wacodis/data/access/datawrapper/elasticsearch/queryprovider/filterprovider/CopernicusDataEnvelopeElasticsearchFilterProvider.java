/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CopernicusDataEnvelopeElasticsearchFilterProvider implements DataEnvelopeElasticsearchFilterProvider {

    //mandatory attributes
    private final static String DATASETID_ATTRIBUTE = "datasetId";
    private final static String SATELLITE_ATTRIBUTE = "satellite";
    private final static String PORTAL_ATTRIBUTE = "portal";
    //optional attributes
    private static final String INSTRUMENT_ATTRIBUTE = "instrument";
    private static final String SENSORMODE_ATTRIBUTE = "sensorMode";
    private static final String PRODUCTTYPE_ATTRIBUTE = "productType";
    private static final String PRODUCTLEVEL_ATTRIBUTE = "productLevel";

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof CopernicusDataEnvelope) {
            List<QueryBuilder> filters;
            CopernicusDataEnvelope copernicusEnvelope = (CopernicusDataEnvelope) envelope;

            //filters for mandatory attributes
            QueryBuilder datasetIdQuery = QueryBuilders.termQuery(DATASETID_ATTRIBUTE, copernicusEnvelope.getDatasetId());
            QueryBuilder satelliteQuery = QueryBuilders.termQuery(SATELLITE_ATTRIBUTE, copernicusEnvelope.getSatellite().toString());
            //QueryBuilder portalQuery = QueryBuilders.termQuery(PORTAL_ATTRIBUTE, copernicusEnvelope.getPortal().toString());
            filters = Arrays.asList(new QueryBuilder[]{datasetIdQuery, satelliteQuery});

            //filters for optional attributes
            List<QueryBuilder> optionalFilters = new ArrayList<>();
            if (copernicusEnvelope.getInstrument() != null) {
                QueryBuilder instrumentFilter = QueryBuilders.termQuery(INSTRUMENT_ATTRIBUTE, copernicusEnvelope.getInstrument());
                optionalFilters.add(instrumentFilter);
            }
            if (copernicusEnvelope.getSensorMode() != null) {
                QueryBuilder sensorModeFilter = QueryBuilders.termQuery(SENSORMODE_ATTRIBUTE, copernicusEnvelope.getSensorMode());
                optionalFilters.add(sensorModeFilter);
            }
            if (copernicusEnvelope.getProductType() != null) {
                QueryBuilder productTypeFilter = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, copernicusEnvelope.getProductType());
                optionalFilters.add(productTypeFilter);
            }
            if (copernicusEnvelope.getProductLevel() != null) {
                QueryBuilder productLevelFilter = QueryBuilders.termQuery(PRODUCTLEVEL_ATTRIBUTE, copernicusEnvelope.getProductLevel());
                optionalFilters.add(productLevelFilter);
            }

            filters.addAll(optionalFilters);
            return filters;
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + CopernicusDataEnvelope.class.getSimpleName());
        }
    }

}
