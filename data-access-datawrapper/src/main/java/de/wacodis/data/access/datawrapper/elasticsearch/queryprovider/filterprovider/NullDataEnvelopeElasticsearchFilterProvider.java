/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider.filterprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class NullDataEnvelopeElasticsearchFilterProvider implements DataEnvelopeElasticsearchFilterProvider{

    @Override
    public List<QueryBuilder> buildFiltersForDataEnvelope(AbstractDataEnvelope envelope) {
       return new ArrayList<>();
    }
    
}
