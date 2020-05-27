/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.DataEnvelopeQuery;
import java.util.List;

/**
 *
 * @author Arne
 */
public interface DataEnvelopeExplorer {
    
    RequestResponse<List<AbstractDataEnvelope>> queryDataEnvelopes(DataEnvelopeQuery query);
    
}
