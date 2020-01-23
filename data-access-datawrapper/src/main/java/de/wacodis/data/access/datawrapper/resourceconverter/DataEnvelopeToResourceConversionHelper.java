/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;
import de.wacodis.dataaccess.model.SensorWebDataEnvelope;

/**
 *
 * @author Arne
 */
public class DataEnvelopeToResourceConversionHelper {

    private static final DataEnvelopeToResourceConverter<CopernicusDataEnvelope> COPERNICUSCONVERTER = new CopernicusDataEnvelopeConverter();
    private static final DataEnvelopeToResourceConverter<GdiDeDataEnvelope> GDIDECONVERTER = new GdiDeDataEnvelopeConverter();
    private static final DataEnvelopeToResourceConverter<SensorWebDataEnvelope> SENSORWEBCONVERTER = new SensorWebDataEnvelopeConverter();

    public static AbstractResource convertToResource(AbstractDataEnvelope dataEnvelope, ResourceSearchContext searchContext) {
        AbstractResource resource = null;

        //to be extended for further subtypes of AbstractDataEnvelope
        if (dataEnvelope instanceof CopernicusDataEnvelope) {
            resource = COPERNICUSCONVERTER.convertToResource((CopernicusDataEnvelope) dataEnvelope, searchContext);
        } else if (dataEnvelope instanceof GdiDeDataEnvelope) {
            resource = GDIDECONVERTER.convertToResource((GdiDeDataEnvelope) dataEnvelope, searchContext);
        } else if (dataEnvelope instanceof SensorWebDataEnvelope) {
            resource = SENSORWEBCONVERTER.convertToResource((SensorWebDataEnvelope) dataEnvelope, searchContext);
        } else {
            throw new IllegalArgumentException("unable to convert AbstractDataEnvelope " + dataEnvelope.getIdentifier() + " to AbstractResource, no converter for type " + dataEnvelope.getClass().getSimpleName());
        }

        //add ID of corresponding DataEnvelope to Resource
        setDataEnvelopeID(resource, dataEnvelope);

        return resource;
    }

    private static void setDataEnvelopeID(AbstractResource resource, AbstractDataEnvelope dataEnvelope) {
        String dataEnvelopeID = dataEnvelope.getIdentifier();
        resource.setDataEnvelopeId(dataEnvelopeID);
    }
}
