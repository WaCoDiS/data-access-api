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
package de.wacodis.dataaccess.model.extension.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.wacodis.dataaccess.model.AbstractDataEnvelopeAreaOfInterest;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.Size;

/**
 * provides compatibility for elasticsearch geoshape type, 
 * only supports geohape types: envelope, linestring, multipoint, polygon (without inner rings)
 * 
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@JsonIgnoreProperties({"extent"}) //do not include 'extent' in json
public class GeoShapeCompatibilityAreaOfInterest extends AbstractDataEnvelopeAreaOfInterest {

    private static final long serialVersionUID = 1L;

    @JsonProperty("type")
    private GeoShapeType type;

    @JsonProperty("coordinates")
    @Valid
    List<List<Float>> coordinates;

    public GeoShapeType getType() {
        return type;
    }

    public void setType(GeoShapeType type) {
        this.type = type;
    }

    @Size(min = 2)
    public List<List<Float>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Float>> coordinates) {
        this.coordinates = coordinates;
    }
    
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoShapeCompatibilityAreaOfInterest geoShapeCompatibilityAreaOfInterest = (GeoShapeCompatibilityAreaOfInterest) o;
        return (Objects.deepEquals(this.coordinates, geoShapeCompatibilityAreaOfInterest.coordinates) && Objects.equals(this.type, geoShapeCompatibilityAreaOfInterest.type));
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GeoShapeCompatibilityAreaOfInterest {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public enum GeoShapeType {
        @JsonProperty("linestring")
        LINESTRING,
        @JsonProperty("polygon")
        POLYGON,
        @JsonProperty("multipoint")
        MULTIPOINT,
        @JsonProperty("envelope")
        ENVELOPE;
    }

}
