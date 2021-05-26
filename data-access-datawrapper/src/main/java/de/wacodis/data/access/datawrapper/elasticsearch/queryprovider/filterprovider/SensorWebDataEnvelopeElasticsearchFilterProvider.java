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

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.SensorWebDataEnvelope;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SensorWebDataEnvelopeElasticsearchFilterProvider implements DataEnvelopeElasticsearchFilterProvider {
    
    private final static String SERVICEURL_ATTRIBUTE = "serviceUrl";
    private final static String OFFERING_ATTRIBUTE = "offering";
    private final static String FEATUREOFINTEREST_ATTRIBUTE = "featureOfInterest";
    private final static String OBSERVEDPROPERTY_ATTRIBUTE = "observedProperty";
    private final static String PROCEDURE_ATTRIBUTE = "procedure";

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof SensorWebDataEnvelope) {
            SensorWebDataEnvelope sensorWebEnvelope = (SensorWebDataEnvelope) envelope;

            QueryBuilder serviceUrlFilter = QueryBuilders.termQuery(SERVICEURL_ATTRIBUTE, sensorWebEnvelope.getServiceUrl());
            QueryBuilder offeringFilter = QueryBuilders.termQuery(OFFERING_ATTRIBUTE, sensorWebEnvelope.getOffering());
            QueryBuilder featureOfInterestFilter = QueryBuilders.termQuery(FEATUREOFINTEREST_ATTRIBUTE, sensorWebEnvelope.getFeatureOfInterest());
            QueryBuilder observedPropertyFilter = QueryBuilders.termQuery(OBSERVEDPROPERTY_ATTRIBUTE, sensorWebEnvelope.getObservedProperty());
            QueryBuilder procedureFilter = QueryBuilders.termQuery(PROCEDURE_ATTRIBUTE, sensorWebEnvelope.getProcedure());

            return Arrays.asList(new QueryBuilder[]{serviceUrlFilter, offeringFilter, featureOfInterestFilter, observedPropertyFilter, procedureFilter});
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + SensorWebDataEnvelope.class.getSimpleName());
        }
    }

}
