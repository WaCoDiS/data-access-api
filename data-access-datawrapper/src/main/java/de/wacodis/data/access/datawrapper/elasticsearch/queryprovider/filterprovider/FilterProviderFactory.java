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
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.CatalogueSubsetDefinition;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.CopernicusSubsetDefinition;
import de.wacodis.dataaccess.model.DwdDataEnvelope;
import de.wacodis.dataaccess.model.DwdSubsetDefinition;
import de.wacodis.dataaccess.model.SensorWebSubsetDefinition;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;
import de.wacodis.dataaccess.model.SensorWebDataEnvelope;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import de.wacodis.dataaccess.model.WacodisProductSubsetDefinition;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class FilterProviderFactory {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FilterProviderFactory.class);

    public SubsetDefinitionElasticsearchFilterProvider getFilterProviderForSubsetDefinition(AbstractSubsetDefinition subset) {

        if (subset instanceof CopernicusSubsetDefinition) {
            return new CopernicusSubsetDefinitionElasticsearchFilterProvider();
        } else if (subset instanceof CatalogueSubsetDefinition) {
            return new CatalogueSubsetDefinitionElasticsearchFilterProvider();
        } else if (subset instanceof SensorWebSubsetDefinition) {
            return new SensorWebSubsetDefinitionElasticsearchFilterProvider();
        } else if (subset instanceof WacodisProductSubsetDefinition){
            return new WacodisProductDataSubsetDefinitionElasticsearchFilterProvider();
        } else if (subset instanceof DwdSubsetDefinition) {
            return new DWDSubsetDefinitionElasticsearchFilterProvider();
        } else {
            LOGGER.warn("no specific " + SubsetDefinitionElasticsearchFilterProvider.class.getSimpleName() + " for SubsetDefinition " + subset.getIdentifier() + " of type " + subset.getClass().getSimpleName() + " available, return empty filter list");
            return new NullSubsetDefintionElasticsearchFilterProvider();
        }

    }

    public DataEnvelopeElasticsearchFilterProvider getFilterProviderForDataEnvelope(AbstractDataEnvelope envelope) {

        if (envelope instanceof GdiDeDataEnvelope) {
            return new GdiDeDataEnvelopeElasticsearchFilterProvider();
        } else if (envelope instanceof CopernicusDataEnvelope) {
            return new CopernicusDataEnvelopeElasticsearchFilterProvider();
        } else if (envelope instanceof SensorWebDataEnvelope) {
            return new SensorWebDataEnvelopeElasticsearchFilterProvider();
        } else if (envelope instanceof WacodisProductDataEnvelope) {
            return new WacodisProductDataEnvelopeElasticsearchFilterProvider();
        } else if (envelope instanceof DwdDataEnvelope) {
            return new DWDDataEnvelopeElasticsearchFilterProvider();
        } else {
            LOGGER.warn("no specific " + DataEnvelopeElasticsearchFilterProvider.class.getSimpleName() + " for DataEnvelope of type " + envelope.getClass().getSimpleName() + " available, return empty filter list");
            return new NullDataEnvelopeElasticsearchFilterProvider();
        }
    }

}
