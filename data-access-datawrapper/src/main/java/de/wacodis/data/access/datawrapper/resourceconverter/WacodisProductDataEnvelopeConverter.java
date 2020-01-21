/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractBackend;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.ArcGISImageServerBackend;
import de.wacodis.dataaccess.model.GetResource;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Arne
 */
public class WacodisProductDataEnvelopeConverter implements DataEnvelopeToResourceConverter<WacodisProductDataEnvelope> {

    private final static String IMAGESERVER_SERVICETYPE = "ImageServer";
    private final static String IMAGESERVER_LITERAL = "ImageServer";
    private final static String IMAGESERVER_OPERATION = "exportImage";
    private final static String RESPONSE_FORMAT = "image";
    private final static String BBOX_SRS = "4326";
    private final static String IMAGE_FORMAT = "tiff";

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
            List<NameValuePair> queryParams = new ArrayList<>();

            if (imageServerBackend.getServiceTypes().contains(IMAGESERVER_SERVICETYPE)) {
                path += "/" + IMAGESERVER_LITERAL;
                path += "/" + IMAGESERVER_OPERATION;

                NameValuePair bboxFilter = new BasicNameValuePair("bbox", getFormattedBBoxParam(context.getInputAreaOfInterest()));
                NameValuePair bboxSRSFilter = new BasicNameValuePair("bboxSR", BBOX_SRS);
                NameValuePair formatFilter = new BasicNameValuePair("f", RESPONSE_FORMAT);
                NameValuePair imageFormatFilter = new BasicNameValuePair("format", IMAGE_FORMAT);
                NameValuePair timeFilter = new BasicNameValuePair("time",getFormattedTimeParam(context.getInputTimeFrame()));

                queryParams.addAll(Arrays.asList(bboxFilter, bboxSRSFilter, formatFilter, imageFormatFilter, timeFilter));
            } else {
                throw new UnsupportedOperationException("cannot for create resource from WacodisProductDataEnvelope " + dataEnvelope.getIdentifier() + ", only service type ImageServer is supported for ArcGISImageServerBackend");
            }

            try {
                URI url = URI.create(imageServerBackend.getBaseUrl());
                url = new URIBuilder(url)
                        .setPath(url.getPath() + "/" + path)
                        .setParameters(queryParams)
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

    private String getFormattedBBoxParam(AbstractDataEnvelopeAreaOfInterest searchExtent) {
        String bboxParam = String.format("%f, %f, %f, %f",
                searchExtent.getExtent().get(0), //minLon
                searchExtent.getExtent().get(1), //minLat
                searchExtent.getExtent().get(2), //maxLon
                searchExtent.getExtent().get(3)); //maxLat

        return bboxParam;
    }

    private String getFormattedTimeParam(AbstractDataEnvelopeTimeFrame searchTimeFrame) {
        String timeParam = String.format("%d,%d",
                searchTimeFrame.getStartTime().getMillis(), //startTime
                searchTimeFrame.getEndTime().getMillis() //endTime
        );

        return timeParam;
    }

}
