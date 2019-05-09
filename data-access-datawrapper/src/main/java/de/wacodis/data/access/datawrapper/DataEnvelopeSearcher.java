/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface DataEnvelopeSearcher {
    
    /**
     * retrieve AbstractDataEnvelope by ID
     * @param identifier
     * @return 
     * @throws java.io.IOException 
     */
    Optional<AbstractDataEnvelope> retrieveDataEnvelopeById(String identifier) throws IOException;
    
    /**
     * retrieve IDs of a already created AbstractDataEnvelope
     * @param dataEnvelope
     * @return 
     * @throws java.io.IOException 
     */
    RequestResponse<AbstractDataEnvelope> retrieveIdForDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException;
    
}
