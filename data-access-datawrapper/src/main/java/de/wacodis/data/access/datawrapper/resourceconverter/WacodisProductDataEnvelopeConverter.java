/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractBackend;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.ArcGISImageServerBackend;
import de.wacodis.dataaccess.model.GetResource;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author Arne
 */
public class WacodisProductDataEnvelopeConverter implements DataEnvelopeToResourceConverter<WacodisProductDataEnvelope> {

    private final static String IMAGESERVER_SERVICETYPE = "ImageServer";
    private final static String IMAGESERVER_LITERAL = "ImageServer";

    @Override
    public AbstractResource convertToResource(WacodisProductDataEnvelope dataEnvelope, ResourceSearchContext context) {
        AbstractResource resource = null;
        AbstractBackend backend = dataEnvelope.getServiceDefinition();

        if (backend instanceof ArcGISImageServerBackend) {
            resource = getResourceForArcGisImageServerBackend(dataEnvelope, context);
        } else {
            throw new UnsupportedOperationException("cannot convert DataEnvelope " + dataEnvelope.getIdentifier() + " to Resource, serviceDefinition of type " + backend.getClass().getSimpleName() + " is not supported");
        }

        return resource;
    }

    @Override
    public Class<WacodisProductDataEnvelope> supportedDataEnvelopeType() {
        return WacodisProductDataEnvelope.class;
    }

    private AbstractResource getResourceForArcGisImageServerBackend(WacodisProductDataEnvelope dataEnvelope, ResourceSearchContext context) {
        AbstractBackend backend = dataEnvelope.getServiceDefinition();

        if (backend instanceof ArcGISImageServerBackend) {
            ArcGISImageServerBackend imageServerBackend = (ArcGISImageServerBackend) backend;
            String path = imageServerBackend.getProductCollection();

            if (imageServerBackend.getServiceTypes().contains(IMAGESERVER_SERVICETYPE)) {
                path += "/" + IMAGESERVER_LITERAL;
            } else {
                throw new UnsupportedOperationException("cannot for create resource from WacodisProductDataEnvelope " + dataEnvelope.getIdentifier() + ", only service type ImageServer is supported for ArcGISImageServerBackend");
            }

               
            try {
                URI url = URI.create(imageServerBackend.getBaseUrl());     
                url = new URIBuilder(url)
                        .setPath(url.getPath() + "/" + path)
                        .build()
                        .normalize();

                GetResource resource = new GetResource();
                resource.setDataEnvelopeId(dataEnvelope.getIdentifier());
                resource.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
                resource.setUrl(url.toString());

                return resource;
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("unable to concatenate resource url for data envelope " + dataEnvelope.getIdentifier(), ex);
            }

        } else {
            throw new IllegalArgumentException("expected serviceDefinition of type " + ArcGISImageServerBackend.class.getSimpleName() + ", but serviceDefintion of WacodisProductDataEnvelope " + dataEnvelope.getIdentifier() + " is of type " + backend.getClass().getSimpleName());
        }
    }

}
