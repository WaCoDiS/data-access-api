/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
