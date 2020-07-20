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
    
    List<AbstractDataEnvelope> orderDataEnvelopes(List<AbstractDataEnvelope> dataEnvelopes);
    
}
