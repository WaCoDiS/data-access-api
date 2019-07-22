/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
