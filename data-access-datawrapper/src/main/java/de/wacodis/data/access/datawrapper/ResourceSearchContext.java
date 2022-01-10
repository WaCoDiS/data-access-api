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
