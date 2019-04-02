/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface DataEnvelopeManipulator {
    
    /**
     * store DataEnvelope
     * @param dataEnvelope
     * @return id of stored DataEnvelope
     * @throws java.io.IOException
     */
    String createDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException;
    
    /**
     * update DataEnvelope
     * @param identifier resource to be modified
     * @param dataEnvelope
     * @return updated DataEnvelope
     * @throws java.io.IOException
     */
    RequestResponse<AbstractDataEnvelope> updateDataEnvelope(String identifier, AbstractDataEnvelope dataEnvelope) throws IOException;
    

    /**
     * delete DataEnvelope
     * @param identifier DataEnvelope to be deleted
     * @return true if successfully deleted
     * @throws java.io.IOException
     */
    RequestResult deleteDataEnvelope(String identifier) throws IOException;
}


