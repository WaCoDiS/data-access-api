/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;

/**
 *
 * @author Arne
 */
public class ResourceSearchContext {
    
    private final AbstractDataEnvelopeAreaOfInterest inputAreaOfInterest;
    private final AbstractDataEnvelopeTimeFrame inputTimeFrame;
    private final AbstractSubsetDefinition inputSubset;

    public ResourceSearchContext(AbstractDataEnvelopeAreaOfInterest inputAreaOfInterest, AbstractDataEnvelopeTimeFrame inputTimeFrame, AbstractSubsetDefinition inputSubset) {
        this.inputAreaOfInterest = inputAreaOfInterest;
        this.inputTimeFrame = inputTimeFrame;
        this.inputSubset = inputSubset;
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
    
}
