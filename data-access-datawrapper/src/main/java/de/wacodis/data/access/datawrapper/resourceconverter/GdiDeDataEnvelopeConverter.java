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
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;


public class GdiDeDataEnvelopeConverter implements DataEnvelopeToResourceConverter<GdiDeDataEnvelope> {

    @Override
    public AbstractResource convertToResource(GdiDeDataEnvelope dataEnvelope, ResourceSearchContext searchContext) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<GdiDeDataEnvelope> supportedDataEnvelopeType() {
        return GdiDeDataEnvelope.class;
    }
    
}
