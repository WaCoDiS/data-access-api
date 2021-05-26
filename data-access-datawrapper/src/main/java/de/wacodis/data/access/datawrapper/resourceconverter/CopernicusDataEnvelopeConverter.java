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
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.GetResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arne
 */
public class CopernicusDataEnvelopeConverter implements DataEnvelopeToResourceConverter<CopernicusDataEnvelope> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CopernicusDataEnvelopeConverter.class);

    private final static String SCIHUB_URL = "https://scihub.copernicus.eu/dhus/odata/v1/Products";
    private final static String CODE_DE_URL = "https://zipper.prod.cloud.code-de.org/download";

    @Override
    public AbstractResource convertToResource(CopernicusDataEnvelope dataEnvelope, ResourceSearchContext searchContext) {
        GetResource resource = new GetResource();
        String productID = dataEnvelope.getDatasetId().toString();

        switch (dataEnvelope.getPortal()) {
            case CODE_DE:
                resource.setUrl(String.join("/", CODE_DE_URL, productID));
                break;
            case SENTINEL_HUB:
                resource.setUrl(SCIHUB_URL + "('" + productID + "')/$value");
                break;
            default:
                LOG.warn("Portal '{}' is not valid. Set resource URL to SCIHUB endpoint.", dataEnvelope.getPortal());
                resource.setUrl(SCIHUB_URL + "('" + productID + "')/$value");
                break;
        }
        resource.setMethod(AbstractResource.MethodEnum.GETRESOURCE);

        return resource;
    }

    @Override
    public Class<CopernicusDataEnvelope> supportedDataEnvelopeType() {
        return CopernicusDataEnvelope.class;
    }

}
