/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
