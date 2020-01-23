/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;


public class GdiDeDataEnvelopeConverter implements DataEnvelopeToResourceConverter<GdiDeDataEnvelope> {

    @Override
    public AbstractResource convertToResource(GdiDeDataEnvelope dataEnvelope, ResourceSearchContext searchContext) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<GdiDeDataEnvelope> supportedDataEnvelopeType() {
        return GdiDeDataEnvelope.class;
    }
    
}
