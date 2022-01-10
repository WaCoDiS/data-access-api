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
import de.wacodis.dataaccess.model.DwdDataEnvelope;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DWDDataEnvelopeElasticsearchFilterProvider implements DataEnvelopeElasticsearchFilterProvider {

    private final static String SERVICEURL_ATTRIBUTE = "serviceUrl";
    private final static String LAYERNAME_ATTRIBUTE = "layerName";
    private final static String PARAMETER_ATTRIBUTE = "parameter";

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
        if (envelope instanceof DwdDataEnvelope) {
            DwdDataEnvelope dwdEnvelope = (DwdDataEnvelope) envelope;

            QueryBuilder serviceFilter = QueryBuilders.termQuery(SERVICEURL_ATTRIBUTE, dwdEnvelope.getServiceUrl());
            QueryBuilder layerFilter = QueryBuilders.termQuery(LAYERNAME_ATTRIBUTE, dwdEnvelope.getLayerName());
            QueryBuilder parameterFilter = QueryBuilders.termQuery(PARAMETER_ATTRIBUTE, dwdEnvelope.getParameter());

            return Arrays.asList(new QueryBuilder[]{serviceFilter, layerFilter, parameterFilter});
        } else {
            throw new IllegalArgumentException("wrong type of DataEnvelope, envelope is of type " + envelope.getClass().getSimpleName() + ", expected " + DwdDataEnvelope.class.getSimpleName());
        }
    }

}
