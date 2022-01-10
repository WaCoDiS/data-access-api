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
            List<QueryBuilder> filters = new ArrayList<>();
            CopernicusDataEnvelope copernicusEnvelope = (CopernicusDataEnvelope) envelope;

            //filters for mandatory attributes
            QueryBuilder datasetIdQuery = QueryBuilders.termQuery(DATASETID_ATTRIBUTE, copernicusEnvelope.getDatasetId());
            QueryBuilder satelliteQuery = QueryBuilders.termQuery(SATELLITE_ATTRIBUTE, copernicusEnvelope.getSatellite().toString());
            //QueryBuilder portalQuery = QueryBuilders.termQuery(PORTAL_ATTRIBUTE, copernicusEnvelope.getPortal().toString());
            filters.addAll(Arrays.asList(datasetIdQuery, satelliteQuery));

            //filters for optional attributes
            List<QueryBuilder> optionalFilters = new ArrayList<>();
            if (copernicusEnvelope.getInstrument() != null) {
                QueryBuilder instrumentQuery = QueryBuilders.termQuery(INSTRUMENT_ATTRIBUTE, copernicusEnvelope.getInstrument());
                optionalFilters.add(instrumentQuery);
            }
            if (copernicusEnvelope.getSensorMode() != null) {
                QueryBuilder sensorModeQuery = QueryBuilders.termQuery(SENSORMODE_ATTRIBUTE, copernicusEnvelope.getSensorMode());
                optionalFilters.add(sensorModeQuery);
            }
            if (copernicusEnvelope.getProductType() != null) {
                QueryBuilder productTypeQuery = QueryBuilders.termQuery(PRODUCTTYPE_ATTRIBUTE, copernicusEnvelope.getProductType());
                optionalFilters.add(productTypeQuery);
            }
            if (copernicusEnvelope.getProductLevel() != null) {
                QueryBuilder productLevelQuery = QueryBuilders.termQuery(PRODUCTLEVEL_ATTRIBUTE, copernicusEnvelope.getProductLevel());
                optionalFilters.add(productLevelQuery);
            }

            filters.addAll(optionalFilters);
            return filters;
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + CopernicusDataEnvelope.class.getSimpleName());
        }
    }

}
