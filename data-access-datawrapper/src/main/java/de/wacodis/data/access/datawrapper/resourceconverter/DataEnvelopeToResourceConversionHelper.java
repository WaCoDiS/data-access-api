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
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;
import de.wacodis.dataaccess.model.SensorWebDataEnvelope;

/**
 *
 * @author Arne
 */
public class DataEnvelopeToResourceConversionHelper {

    private static final DataEnvelopeToResourceConverter<CopernicusDataEnvelope> COPERNICUSCONVERTER = new CopernicusDataEnvelopeConverter();
    private static final DataEnvelopeToResourceConverter<GdiDeDataEnvelope> GDIDECONVERTER = new GdiDeDataEnvelopeConverter();
    private static final DataEnvelopeToResourceConverter<SensorWebDataEnvelope> SENSORWEBCONVERTER = new SensorWebDataEnvelopeConverter();

    public static AbstractResource convertToResource(AbstractDataEnvelope dataEnvelope, ResourceSearchContext searchContext) {
        AbstractResource resource = null;

        //to be extended for further subtypes of AbstractDataEnvelope
        if (dataEnvelope instanceof CopernicusDataEnvelope) {
            resource = COPERNICUSCONVERTER.convertToResource((CopernicusDataEnvelope) dataEnvelope, searchContext);
        } else if (dataEnvelope instanceof GdiDeDataEnvelope) {
            resource = GDIDECONVERTER.convertToResource((GdiDeDataEnvelope) dataEnvelope, searchContext);
        } else if (dataEnvelope instanceof SensorWebDataEnvelope) {
            resource = SENSORWEBCONVERTER.convertToResource((SensorWebDataEnvelope) dataEnvelope, searchContext);
        } else {
            throw new IllegalArgumentException("unable to convert AbstractDataEnvelope " + dataEnvelope.getIdentifier() + " to AbstractResource, no converter for type " + dataEnvelope.getClass().getSimpleName());
        }

        //add ID of corresponding DataEnvelope to Resource
        setDataEnvelopeID(resource, dataEnvelope);

        return resource;
    }

    private static void setDataEnvelopeID(AbstractResource resource, AbstractDataEnvelope dataEnvelope) {
        String dataEnvelopeID = dataEnvelope.getIdentifier();
        resource.setDataEnvelopeId(dataEnvelopeID);
    }
}
