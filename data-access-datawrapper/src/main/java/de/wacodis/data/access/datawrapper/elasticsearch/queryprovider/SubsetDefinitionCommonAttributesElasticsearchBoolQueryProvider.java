/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.data.access.datawrapper.elasticsearch.queryprovider;

import de.wacodis.data.access.datawrapper.elasticsearch.ElasticsearchResourceSearcher;
import de.wacodis.data.access.datawrapper.elasticsearch.util.SubsetDefinitionDataEnvelopeSourceTypeMapping;
import de.wacodis.dataaccess.model.AbstractDataEnvelope;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeTimeFrame;
import de.wacodis.dataaccess.model.AbstractSubsetDefinition;
import java.io.IOException;
import java.util.Optional;
import org.elasticsearch.index.query.QueryBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SubsetDefinitionCommonAttributesElasticsearchBoolQueryProvider implements ElasticsearchQueryProvider {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SubsetDefinitionCommonAttributesElasticsearchBoolQueryProvider.class);

    private static final ShapeRelation SPATIALBBOXFILTER_RELATION = ShapeRelation.INTERSECTS;
    private static final String SOURCETYPE_ATTRIBUTE = "sourceType";
    private static final String SPATIALBBOXFILTER_ATTRIBUTE = "areaOfInterest";
    private static final String TIMEFRAMEFILTER_STARTATTRIBUTE = "timeFrame.startTime";
    private static final String TIMEFRAMEFILTER_ENDATTRIBUTE = "timeFrame.endTime";
    private static final String DATEFORMAT = "yyyyMMdd'T'HH:mm:ss.SSS";

    
    @Override
    public BoolQueryBuilder buildQueryForSubsetDefinition(AbstractSubsetDefinition subsetDefinition, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        QueryBuilder spatialFilter = getSpatialBBOXFilter(areaOfInterest);
        QueryBuilder startTimeFilter = getTimeFrameStartTimeFilter(timeFrame);
        QueryBuilder endTimeFilter = getTimeFrameEndTimeFilter(timeFrame);
        QueryBuilder srcTypeFilter = getSourceTypeFilter(subsetDefinition);

        boolQuery.filter(srcTypeFilter).filter(spatialFilter).filter(startTimeFilter).filter(endTimeFilter); //add all filters (logical and)

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

    private RangeQueryBuilder getTimeFrameEndTimeFilter(AbstractDataEnvelopeTimeFrame timeFrame) {
        return QueryBuilders.rangeQuery(TIMEFRAMEFILTER_ENDATTRIBUTE).lte(timeFrame.getEndTime().toString(DATEFORMAT)).format(DATEFORMAT);
    }

    private TermQueryBuilder getSourceTypeFilter(AbstractSubsetDefinition subsetDefintion) {
        Optional<AbstractDataEnvelope.SourceTypeEnum> dataEnvelopeSrcType = SubsetDefinitionDataEnvelopeSourceTypeMapping.getCorrespondingDataEnvelopeSourceType(subsetDefintion.getSourceType());

        if (dataEnvelopeSrcType.isPresent()) {
            return QueryBuilders.termQuery(SOURCETYPE_ATTRIBUTE, dataEnvelopeSrcType.get().toString());
        } else { //no corresponding DataEnvelope source type for SubsetDefinition source type
            LOGGER.warn("no corresponding DataEnvelope source type or SubsetDefinition source type unknown for SubsetDefinition {} of type {}, query will not return resources for input {}", subsetDefintion.getIdentifier(), subsetDefintion.getSourceType(), subsetDefintion.getIdentifier() );
            // mandatory SOURCETYPE_ATTRIBUTE cannot be empty, hence query will not match any DataEnvelope
            return QueryBuilders.termQuery(SOURCETYPE_ATTRIBUTE, "");
        }
    }
}
