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
