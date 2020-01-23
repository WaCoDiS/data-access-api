/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractResource;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <T>
 */
public interface DataEnvelopeToResourceConverter<T extends AbstractDataEnvelope> {
    
    AbstractResource convertToResource(T dataEnvelope, ResourceSearchContext context);
    
    Class<T>  supportedDataEnvelopeType();
}
