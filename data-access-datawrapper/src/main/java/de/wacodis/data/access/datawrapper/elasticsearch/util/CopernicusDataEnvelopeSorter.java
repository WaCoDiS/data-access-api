/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.util;

import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.CopernicusDataEnvelope;
import de.wacodis.dataaccess.model.DataAccessResourceSearchBody;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

/**
 * sorts only CopernicusDataEnvelopes other subtypes of AbstractDataEnvelope or
 * mixed types are left unchanged
 *
 * @author Arne
 */
public class CopernicusDataEnvelopeSorter implements DataEnvelopePrioritizer {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CopernicusDataEnvelopeSorter.class);

    private DataAccessResourceSearchBody searchRequest;

    public CopernicusDataEnvelopeSorter(DataAccessResourceSearchBody searchRequest) {
        this.searchRequest = searchRequest;
    }

    public DataAccessResourceSearchBody getSearchRequest() {
        return searchRequest;
    }

    public void setSearchRequest(DataAccessResourceSearchBody searchRequest) {
        this.searchRequest = searchRequest;
    }

    @Override
    public List<AbstractDataEnvelope> orderDataEnvelopes(List<AbstractDataEnvelope> dataEnvelopes) {
        List<CopernicusDataEnvelope> copernicusEnvs = filterCopernicusDataEnvelopes(dataEnvelopes);

        //only sort if list contains only CopernicusDataEnvelopes
        if (copernicusEnvs.size() == dataEnvelopes.size()) {
            Comparator<CopernicusDataEnvelope> prioritizer = new CopernicusDataEnvelopePrioritizer(this.searchRequest.getAreaOfInterest());
            //sort CopernicusDataEnvelopes
            copernicusEnvs.sort(prioritizer);

            LOGGER.debug("return sorted list of CopernicusDataEnvelope");
            return new ArrayList<>(copernicusEnvs);
        } else {
            if (copernicusEnvs.isEmpty()) {
                LOGGER.debug("list does not contain instances of CopernicusDataEnvelope, return unchanged list");
            } else {
                LOGGER.debug("list contains mixed (sub-)types of AbstractDataEnvelope, return unchanged list");
            }

            return new ArrayList<>(dataEnvelopes);
        }

    }

    /**
     * return all instances of CopernicusDataEnvelope
     *
     * @param dataEnvelopes
     * @return
     */
    private List<CopernicusDataEnvelope> filterCopernicusDataEnvelopes(List<AbstractDataEnvelope> dataEnvelopes) {
        return dataEnvelopes.stream().filter(env -> env instanceof CopernicusDataEnvelope).map(env -> (CopernicusDataEnvelope) env).collect(Collectors.toList());
    }

    private class CopernicusDataEnvelopePrioritizer implements Comparator<CopernicusDataEnvelope> {

        AbstractDataEnvelopeAreaOfInterest areaOfInterest;

        public CopernicusDataEnvelopePrioritizer(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
            this.areaOfInterest = areaOfInterest;
        }

        public AbstractDataEnvelopeAreaOfInterest getAreaOfInterest() {
            return areaOfInterest;
        }

        public void setAreaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
            this.areaOfInterest = areaOfInterest;
        }

        @Override
        public int compare(CopernicusDataEnvelope env1, CopernicusDataEnvelope env2) {
            float indexEnv1 = calculatePriorityIndex(env1);
            float indexEnv2 = calculatePriorityIndex(env2);
            
            if(indexEnv1 == indexEnv2){
                return 0;
            }else if(indexEnv1 < indexEnv2){
                return -1;
            }else{
                return 1;
            }    
        }

        /**
         * percentage of overlap (area of interest) minus cloud coverage
         * @param env
         * @return 
         */
        private float calculatePriorityIndex(CopernicusDataEnvelope env){
            float cloudCov = env.getCloudCoverage();
            float extentOverlap = AreaOfInterestIntersectionCalculator.calculateOverlapPercentage(this.areaOfInterest, env.getAreaOfInterest());
            
            return (extentOverlap - cloudCov);
        }
        

        
    }
}
