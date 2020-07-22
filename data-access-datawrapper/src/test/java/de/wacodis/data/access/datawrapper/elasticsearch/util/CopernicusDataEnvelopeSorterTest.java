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

        List<AbstractDataEnvelope> prioritizedEnvs = prioritizer.sortDataEnvelopes(envs);

        //order should be unchanged since not all DataEnvelopes are instance of CopernicusDataEnvelope
        assertEquals(envs.get(0), prioritizedEnvs.get(0));
        assertEquals(envs.get(1), prioritizedEnvs.get(1));
    }

}
