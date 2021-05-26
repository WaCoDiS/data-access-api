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
