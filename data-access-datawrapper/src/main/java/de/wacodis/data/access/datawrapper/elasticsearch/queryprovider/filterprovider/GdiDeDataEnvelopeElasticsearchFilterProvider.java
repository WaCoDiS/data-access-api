/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class GdiDeDataEnvelopeElasticsearchFilterProvider implements DataEnvelopeElasticsearchFilterProvider {
    
    private static final String CATALOGUEURL_ATTRIBUTE = "catalougeUrl"; //typo stems from wacodis schema definitions
    private static final String RECORDID_ATTRIBUTE = "recordRefId";

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof GdiDeDataEnvelope) {
            GdiDeDataEnvelope gdiDeEnvelope = (GdiDeDataEnvelope) envelope;

            QueryBuilder urlFilter = QueryBuilders.termQuery(CATALOGUEURL_ATTRIBUTE, gdiDeEnvelope.getCatalougeUrl());
            QueryBuilder recordIDFilter = QueryBuilders.termQuery(RECORDID_ATTRIBUTE, gdiDeEnvelope.getRecordRefId());

            return Arrays.asList(new QueryBuilder[]{urlFilter, recordIDFilter});
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + GdiDeDataEnvelope.class.getSimpleName());
        }
    }

}
