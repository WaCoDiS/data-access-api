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
import java.util.List;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ResourceSearchResponseContainer {
    
 
    private final List<AbstractDataEnvelope> responseDataEnvelopes;
    private final ResourceSearchContext searchContext;

    public ResourceSearchResponseContainer(List<AbstractDataEnvelope> responseDataEnvelopes, ResourceSearchContext searchContext) {
        this.responseDataEnvelopes = responseDataEnvelopes;
        this.searchContext = searchContext;
    }

    
    
    public ResourceSearchContext getSearchContext() {
        return searchContext;
    }

    public List<AbstractDataEnvelope> getResponseDataEnvelopes() {
        return responseDataEnvelopes;
    }
}
