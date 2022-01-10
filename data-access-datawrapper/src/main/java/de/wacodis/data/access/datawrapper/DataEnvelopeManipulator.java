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
    RequestResponse<AbstractDataEnvelope> createDataEnvelope(AbstractDataEnvelope dataEnvelope) throws IOException;
    
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


