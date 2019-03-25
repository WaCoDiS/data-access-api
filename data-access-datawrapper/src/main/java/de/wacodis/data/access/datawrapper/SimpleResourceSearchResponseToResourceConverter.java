/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.GdiDeDataEnvelope;
import de.wacodis.dataaccess.model.GetResource;
import de.wacodis.dataaccess.model.SensorWebDataEnvelope;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SimpleResourceSearchResponseToResourceConverter implements ResourceSearchResponseToResourceConverter {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SimpleResourceSearchResponseToResourceConverter.class);
    
    private final static String SCIEHUB_URL = "https://scihub.copernicus.eu/dhus/odata/v1/Products";

    @Override
    public List<AbstractResource> convertToResource(ResourceSearchResponseContainer searchResponse) {
        List<AbstractResource> resources = new ArrayList<>();
        List<AbstractDataEnvelope> responseDataEnvelopes = searchResponse.getResponseDataEnvelopes();

        for (AbstractDataEnvelope dataEnvelope : responseDataEnvelopes) {
            GetResource resource = new GetResource();
            resource.setMethod(AbstractResource.MethodEnum.GETRESOURCE);

            if (dataEnvelope instanceof CopernicusDataEnvelope) {
                CopernicusDataEnvelope copernicusDataEnvelope = (CopernicusDataEnvelope) dataEnvelope;
                String productID = copernicusDataEnvelope.getDatasetId().toString();
                String url = SCIEHUB_URL+"('" +productID +"')/$value"; //TODO reflect portal
                resource.setUrl(url);
            } else if (dataEnvelope instanceof GdiDeDataEnvelope) {
                GdiDeDataEnvelope gdiDataEnvelope = (GdiDeDataEnvelope) dataEnvelope;
                String url = gdiDataEnvelope.getCatalougeUrl(); //TODO get correct URL
                resource.setUrl(url);
            } else if (dataEnvelope instanceof SensorWebDataEnvelope) {
                SensorWebDataEnvelope sensorWebDataEnvelope = (SensorWebDataEnvelope) dataEnvelope;
                String url = sensorWebDataEnvelope.getServiceUrl(); //TODO get correct URL
                resource.setUrl(url);
            } else {
                LOGGER.warn("unknown type " + dataEnvelope.getClass().getCanonicalName());
                throw new IllegalArgumentException("cannot convert AbstractDataEnvelope to AbstractResource, unknown type " + dataEnvelope.getClass().getSimpleName() + System.lineSeparator() + "DataEnvelope: " + System.lineSeparator() + dataEnvelope.toString());
            }

            resources.add(resource);
        }

        return resources;
    }

}
