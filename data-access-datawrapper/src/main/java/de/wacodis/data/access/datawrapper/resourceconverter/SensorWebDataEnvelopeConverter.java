/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.SensorWebDataEnvelope;


public class SensorWebDataEnvelopeConverter implements DataEnvelopeToResourceConverter<SensorWebDataEnvelope> {

    @Override
    public AbstractResource convertToResource(SensorWebDataEnvelope dataEnvelope) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<SensorWebDataEnvelope> supportedDataEnvelopeType() {
        return SensorWebDataEnvelope.class;
    }
    
}
