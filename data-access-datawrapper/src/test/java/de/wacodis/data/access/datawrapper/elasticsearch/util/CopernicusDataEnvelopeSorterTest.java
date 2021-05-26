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
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.DataAccessResourceSearchBody;
import de.wacodis.dataaccess.model.DwdDataEnvelope;

/**
 *
 * @author Arne
 */
public class CopernicusDataEnvelopeSorterTest {

    private AbstractDataEnvelopeAreaOfInterest aoiReference;

    public CopernicusDataEnvelopeSorterTest() {
    }

    @Before
    public void setUp() {
        aoiReference = new AbstractDataEnvelopeAreaOfInterest();

        List<Float> extentRef = new ArrayList<>();
        extentRef.add(0.0f);
        extentRef.add(0.0f);
        extentRef.add(10.0f);
        extentRef.add(10.0f);
        aoiReference.setExtent(extentRef);
    }

    /**
     * Test of sortDataEnvelopes method, of class CopernicusDataEnvelopeSorter.
     */
    @Test
    public void testOrderDataEnvelopes() {
        //50% overlap with this.aoiReference
        AbstractDataEnvelopeAreaOfInterest aoiEnv = new AbstractDataEnvelopeAreaOfInterest();
        List<Float> extentEnv = new ArrayList<>();
        extentEnv.add(0.0f);
        extentEnv.add(0.0f);
        extentEnv.add(10.0f);
        extentEnv.add(10.0f);
        aoiEnv.setExtent(extentEnv);

        CopernicusDataEnvelope env1 = new CopernicusDataEnvelope();
        env1.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);
        env1.setIdentifier("env1");
        env1.setAreaOfInterest(aoiEnv);
        env1.setCloudCoverage(50.0f);
        //0 0 , 0 10, 10 10, 10 0, 0 0
        String footprint = "{\r\n    \"type\": \"Polygon\",\r\n    \"coordinates\": [\r\n        [\r\n            [\r\n                0,\r\n                0\r\n            ],\r\n            [\r\n                0,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                10\r\n            ],\r\n            [\r\n                10,\r\n                0\r\n            ],\r\n            [\r\n                0,\r\n                0\r\n            ]\r\n        ]\r\n    ]\r\n}";
        env1.setFootprint(footprint);

        CopernicusDataEnvelope env2 = new CopernicusDataEnvelope();
        env2.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);
        env2.setIdentifier("env2");
        env2.setAreaOfInterest(aoiEnv);
        env2.setCloudCoverage(25.0f);

        List<AbstractDataEnvelope> envs = new ArrayList<>();
        envs.add(env1);
        envs.add(env2);

        DataAccessResourceSearchBody searchReq = new DataAccessResourceSearchBody();
        searchReq.setAreaOfInterest(this.aoiReference);
        searchReq.setInputs(new ArrayList<>());
        CopernicusDataEnvelopeSorter prioritizer = new CopernicusDataEnvelopeSorter(searchReq);
        //only compare bboxes
        prioritizer.setCompareSentinelFootpring(true);

        List<AbstractDataEnvelope> prioritizedEnvs = prioritizer.sortDataEnvelopes(envs);

        //order should be reversed since both envelopes have the same overlap but env2 has less cloud coverage
        assertEquals(envs.get(1), prioritizedEnvs.get(0));
        assertEquals(envs.get(0), prioritizedEnvs.get(1));
    }

    /**
     * Test of sortDataEnvelopes method, of class CopernicusDataEnvelopeSorter.
     */
    @Test
    public void testOrderDataEnvelopes_MixedSourceTypes() {
        //50% overlap with this.aoiReference
        AbstractDataEnvelopeAreaOfInterest aoiEnv = new AbstractDataEnvelopeAreaOfInterest();
        List<Float> extentEnv = new ArrayList<>();
        extentEnv.add(5.0f);
        extentEnv.add(0.0f);
        extentEnv.add(10.0f);
        extentEnv.add(10.0f);
        aoiEnv.setExtent(extentEnv);

        CopernicusDataEnvelope env1 = new CopernicusDataEnvelope();
        env1.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);
        env1.setIdentifier("env1");
        env1.setAreaOfInterest(aoiEnv);
        env1.setCloudCoverage(50.0f);

        DwdDataEnvelope env2 = new DwdDataEnvelope();
        env2.setSourceType(AbstractDataEnvelope.SourceTypeEnum.DWDDATAENVELOPE);
        env2.identifier("env2");
        env2.setAreaOfInterest(aoiEnv);

        List<AbstractDataEnvelope> envs = new ArrayList<>();
        envs.add(env1);
        envs.add(env2);

        DataAccessResourceSearchBody searchReq = new DataAccessResourceSearchBody();
        searchReq.setAreaOfInterest(this.aoiReference);
        searchReq.setInputs(new ArrayList<>());
        CopernicusDataEnvelopeSorter prioritizer = new CopernicusDataEnvelopeSorter(searchReq);
        //only compare bboxes
        prioritizer.setCompareSentinelFootpring(false);

        List<AbstractDataEnvelope> prioritizedEnvs = prioritizer.sortDataEnvelopes(envs);

        //order should be unchanged since not all DataEnvelopes are instance of CopernicusDataEnvelope
        assertEquals(envs.get(0), prioritizedEnvs.get(0));
        assertEquals(envs.get(1), prioritizedEnvs.get(1));
    }

}
