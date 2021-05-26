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
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider;

import de.wacodis.data.access.datawrapper.elasticsearch.util.SubsetDefinitionDataEnvelopeSourceTypeMapping;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import java.io.IOException;
import java.util.Optional;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.LoggerFactory;

/**
 * provide queries for common attribute of every DataEnvelope
 * @author Arne
 */
public class CommonAttributeFilterUtil {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CommonAttributeFilterUtil.class);

    private static final String SOURCETYPE_ATTRIBUTE = "sourceType";
    private static final String SPATIALBBOXFILTER_ATTRIBUTE = "areaOfInterest";
    private static final String TIMEFRAMEFILTER_STARTATTRIBUTE = "timeFrame.startTime";
    private static final String TIMEFRAMEFILTER_ENDATTRIBUTE = "timeFrame.endTime";
    private static final ShapeRelation DEFAULT_SPATIALBBOXFILTER_RELATION = ShapeRelation.INTERSECTS;
    private static final String DEFAULT_DATEFORMAT = "yyyyMMdd'T'HH:mm:ss.SSS";

    public static GeoShapeQueryBuilder getSpatialBBOXFilter(AbstractDataEnvelopeAreaOfInterest areaOfInterest, ShapeRelation relation) {
        Float[] bbox = areaOfInterest.getExtent().toArray(new Float[0]); //geojson bbox format [minLon, minLat, maxLon, maxLat]
        Coordinate topLeft = new Coordinate(bbox[0], bbox[3]);
        Coordinate bottomRight = new Coordinate(bbox[2], bbox[1]);
        ShapeBuilder bboxShape = new EnvelopeBuilder(topLeft, bottomRight);

        GeoShapeQueryBuilder spatialQuery;
        try {
            spatialQuery = QueryBuilders.geoShapeQuery(SPATIALBBOXFILTER_ATTRIBUTE, bboxShape)
                    .relation(relation);
        } catch (IOException ex) {
            throw new IllegalArgumentException("cannot create bounding box filter for bounding box: " + areaOfInterest.getExtent().toString(), ex);
        }

        return spatialQuery;
    }

    public static GeoShapeQueryBuilder getSpatialBBOXFilter(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
        return getSpatialBBOXFilter(areaOfInterest, DEFAULT_SPATIALBBOXFILTER_RELATION);
    }

    public static RangeQueryBuilder getTimeFrameStartTimeFilter(AbstractDataEnvelopeTimeFrame timeFrame, String dateFormat) {
        return QueryBuilders.rangeQuery(TIMEFRAMEFILTER_STARTATTRIBUTE).gte(timeFrame.getStartTime().toString(dateFormat)).format(dateFormat);
    }

    public static RangeQueryBuilder getTimeFrameEndTimeFilter(AbstractDataEnvelopeTimeFrame timeFrame, String dateFormat) {
        return QueryBuilders.rangeQuery(TIMEFRAMEFILTER_ENDATTRIBUTE).lte(timeFrame.getEndTime().toString(dateFormat)).format(dateFormat);
    }

    public static RangeQueryBuilder getTimeFrameStartTimeFilter(AbstractDataEnvelopeTimeFrame timeFrame) {
        return getTimeFrameStartTimeFilter(timeFrame, DEFAULT_DATEFORMAT);
    }

    public static RangeQueryBuilder getTimeFrameEndTimeFilter(AbstractDataEnvelopeTimeFrame timeFrame) {
        return getTimeFrameEndTimeFilter(timeFrame, DEFAULT_DATEFORMAT);
    }

    public static TermQueryBuilder getSourceTypeFilter(AbstractSubsetDefinition subsetDefintion) {
        Optional<AbstractDataEnvelope.SourceTypeEnum> dataEnvelopeSrcType = SubsetDefinitionDataEnvelopeSourceTypeMapping.getCorrespondingDataEnvelopeSourceType(subsetDefintion.getSourceType());

        if (dataEnvelopeSrcType.isPresent()) {
            return QueryBuilders.termQuery(SOURCETYPE_ATTRIBUTE, dataEnvelopeSrcType.get().toString());
        } else { //no corresponding DataEnvelope source type for SubsetDefinition source type
            LOGGER.warn("no corresponding DataEnvelope source type or SubsetDefinition source type unknown for SubsetDefinition {} of type {}, query will not return resources for input {}", subsetDefintion.getIdentifier(), subsetDefintion.getSourceType(), subsetDefintion.getIdentifier());
            // mandatory SOURCETYPE_ATTRIBUTE cannot be empty, hence query will not match any DataEnvelope
            return QueryBuilders.termQuery(SOURCETYPE_ATTRIBUTE, "");
        }
    }

    public static String getDefaultDateFormat() {
        return DEFAULT_DATEFORMAT;
    }
    
    public static ShapeRelation getDefaultBBoxFilterRelation(){
        return DEFAULT_SPATIALBBOXFILTER_RELATION;
    }
}
