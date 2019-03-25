/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import java.util.List;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ResourceSearchResponseContainer {
    
    private final AbstractDataEnvelopeAreaOfInterest inputAreaOfInterest;
    private final AbstractDataEnvelopeTimeFrame inputTimeFrame;
    private final AbstractSubsetDefinition inputSubset;
    private final List<AbstractDataEnvelope> responseDataEnvelopes;

    public ResourceSearchResponseContainer(AbstractDataEnvelopeAreaOfInterest inputAreaOfInterest, AbstractDataEnvelopeTimeFrame inputTimeFrame, AbstractSubsetDefinition inputSubset, List<AbstractDataEnvelope> responseDataEnvelopes) {
        this.inputAreaOfInterest = inputAreaOfInterest;
        this.inputTimeFrame = inputTimeFrame;
        this.inputSubset = inputSubset;
        this.responseDataEnvelopes = responseDataEnvelopes;
    }


    public AbstractDataEnvelopeAreaOfInterest getInputAreaOfInterest() {
        return inputAreaOfInterest;
    }

    public AbstractDataEnvelopeTimeFrame getInputTimeFrame() {
        return inputTimeFrame;
    }

    public AbstractSubsetDefinition getInputSubset() {
        return inputSubset;
    }

    public List<AbstractDataEnvelope> getResponseDataEnvelopes() {
        return responseDataEnvelopes;
    }
}
