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
