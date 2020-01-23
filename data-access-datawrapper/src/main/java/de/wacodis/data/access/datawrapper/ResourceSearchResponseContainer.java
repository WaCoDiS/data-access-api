/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
