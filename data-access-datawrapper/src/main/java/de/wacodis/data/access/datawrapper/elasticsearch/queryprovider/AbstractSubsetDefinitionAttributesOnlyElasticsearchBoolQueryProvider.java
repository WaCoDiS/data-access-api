/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider;

import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import java.io.IOException;
import org.elasticsearch.index.query.QueryBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class AbstractSubsetDefinitionAttributesOnlyElasticsearchBoolQueryProvider implements ElasticsearchQueryProvider {

    private static final ShapeRelation SPATIALBBOXFILTER_RELATION = ShapeRelation.CONTAINS;
    private static final String SPATIALBBOXFILTER_ATTRIBUTE = "areaOfInterest.extent";
    private static final String TIMEFRAMEFILTER_STARTATTRIBUTE = "timeFrame.starttime";
    private static final String TIMEFRAMEFILTER_ENDATTRIBUTE = "timeFrame.endtime";
    private static final String DATEFORMAT = "yyyyMMdd'T'HH:mm:ss.SSSZZ";

    @Override
    public BoolQueryBuilder buildQueryForSubsetDefinition(AbstractSubsetDefinition subsetDefinition, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame) {
        return buildQueryForSubsetDefinition(areaOfInterest, timeFrame); //SubsetDefinition not needed
    }

    public BoolQueryBuilder buildQueryForSubsetDefinition(AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        QueryBuilder spatialFilter = getSpatialBBOXFilter(areaOfInterest);
        QueryBuilder startTimeFilter = getTimeFrameStartEndFilter(timeFrame);
        QueryBuilder endTimeFilter = getTimeFrameStartEndFilter(timeFrame);

        boolQuery.filter(spatialFilter).filter(startTimeFilter).filter(endTimeFilter); //add all filters (logical and)

        return boolQuery;
    }

    private GeoShapeQueryBuilder getSpatialBBOXFilter(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
        Float[] bbox = areaOfInterest.getExtent().toArray(new Float[0]); //geojson bbox format [minLon, minLat, maxLon, maxLat]
        Coordinate topLeft = new Coordinate(bbox[0], bbox[3]);
        Coordinate bottomRight = new Coordinate(bbox[2], bbox[1]);
        ShapeBuilder bboxShape = new EnvelopeBuilder(topLeft, bottomRight);

        GeoShapeQueryBuilder spatialQuery;
        try {
            spatialQuery = QueryBuilders.geoShapeQuery(SPATIALBBOXFILTER_ATTRIBUTE, bboxShape)
                    .relation(SPATIALBBOXFILTER_RELATION);
        } catch (IOException ex) {
            throw new IllegalArgumentException("cannot create bounding box filter for bounding box: " + areaOfInterest.getExtent().toString(), ex);
        }

        return spatialQuery;
    }

    private RangeQueryBuilder getTimeFrameStartTimeFilter(AbstractDataEnvelopeTimeFrame timeFrame) {
        return QueryBuilders.rangeQuery(TIMEFRAMEFILTER_STARTATTRIBUTE).gte(timeFrame.getStartTime().toString(DATEFORMAT)).format(DATEFORMAT);
    }

    private RangeQueryBuilder getTimeFrameStartEndFilter(AbstractDataEnvelopeTimeFrame timeFrame) {
        return QueryBuilders.rangeQuery(TIMEFRAMEFILTER_ENDATTRIBUTE).lte(timeFrame.getEndTime().toString(DATEFORMAT)).format(DATEFORMAT);
    }
}
