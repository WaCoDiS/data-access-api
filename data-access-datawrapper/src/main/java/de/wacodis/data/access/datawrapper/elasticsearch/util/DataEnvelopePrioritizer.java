/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.util.List;

/**
 *
 * @author Arne
 */
public interface DataEnvelopePrioritizer {
    
    /**
     * sort list of data envelope by specific priority
     * @param dataEnvelopes
     * @return return sorted list where the DataEnvelope with higher priority have lower index (highest priority (best) DataEnvelope must be the first element (index 0)
     */
    List<AbstractDataEnvelope> sortDataEnvelopes(List<AbstractDataEnvelope> dataEnvelopes);
    
}
