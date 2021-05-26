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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.resourceconverter;

import de.wacodis.data.access.datawrapper.ResourceSearchContext;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractResource;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import de.wacodis.dataaccess.model.ArcGISImageServerBackend;
import de.wacodis.dataaccess.model.ProductBackend;
import de.wacodis.dataaccess.model.WacodisProductDataEnvelope;
import de.wacodis.dataaccess.model.WacodisProductSubsetDefinition;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Arne
 */
public class WacodisProductDataEnvelopeConverterTest {

    public WacodisProductDataEnvelopeConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testConvertToResource() {
        WacodisProductDataEnvelope dataEnvelope = getProductDataEnvelope();
        ResourceSearchContext context = getSearchContext();
        AbstractResource resource = new WacodisProductDataEnvelopeConverter().convertToResource(dataEnvelope, context);
        
        String url = resource.getUrl();
        assertTrue(url.startsWith("https://example.com:9090/arcgis/rest/service/EO_WACODIS_DAT_LANDCOVERService/ImageServer"));
    }

    @Test
    public void testSupportedDataEnvelopeType() {
        assertEquals(WacodisProductDataEnvelope.class, new WacodisProductDataEnvelopeConverter().supportedDataEnvelopeType());
    }

    
    
    
    private WacodisProductDataEnvelope getProductDataEnvelope() {
        WacodisProductDataEnvelope productEnvelope = new WacodisProductDataEnvelope();
        productEnvelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
        productEnvelope.setProductType("land cover classification");

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);
        productEnvelope.setAreaOfInterest(aoi);

        AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
        tf.setStartTime(new DateTime(DateTime.parse("2018-01-01T07:30:15Z")));
        tf.setEndTime(new DateTime(DateTime.parse("2018-01-02T07:30:15Z")));
        productEnvelope.setTimeFrame(tf);

        ArcGISImageServerBackend serviceDef = new ArcGISImageServerBackend();
        serviceDef.setBackendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        serviceDef.setBaseUrl("https://example.com:9090/arcgis/rest/service");
        serviceDef.setProductCollection("EO_WACODIS_DAT_LANDCOVERService");
        serviceDef.setServiceTypes(Arrays.asList("ImageServer"));
        productEnvelope.setServiceDefinition(serviceDef);

        return productEnvelope;
    }

    private ResourceSearchContext getSearchContext() {
        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);
        
        AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
        tf.setStartTime(new DateTime(DateTime.parse("2018-01-01T07:30:15Z")));
        tf.setEndTime(new DateTime(DateTime.parse("2018-01-02T07:30:15Z")));
        
        WacodisProductSubsetDefinition subset = new WacodisProductSubsetDefinition();
        subset.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.WACODISPRODUCTSUBSETDEFINITION);
        subset.setIdentifier("productSubset1");
        subset.backendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        subset.setProductType("land cover classification");
               
        return new ResourceSearchContext(aoi, tf, subset);
    }
}
