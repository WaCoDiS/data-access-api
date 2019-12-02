/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.GetResource;

/**
 *
 * @author Arne
 */
public class CopernicusDataEnvelopeConverter implements DataEnvelopeToResourceConverter<CopernicusDataEnvelope> {
    
     private final static String SCIEHUB_URL = "https://scihub.copernicus.eu/dhus/odata/v1/Products";

    @Override
    public AbstractResource convertToResource(CopernicusDataEnvelope dataEnvelope) {
        GetResource resource = new GetResource();

        String productID = dataEnvelope.getDatasetId().toString();
        String url = SCIEHUB_URL + "('" + productID + "')/$value"; //TODO reflect portal and make url configurable
        resource.setUrl(url);
        resource.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        
        return resource;
    }

    @Override
    public Class<CopernicusDataEnvelope> supportedDataEnvelopeType() {
        return CopernicusDataEnvelope.class;
    }

}
