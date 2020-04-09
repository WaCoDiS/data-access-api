/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
