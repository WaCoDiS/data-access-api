package de.wacodis.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** AbstractDataEnvelopeAreaOfInterest */
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-11-27T14:57:01.610+01:00[Europe/Berlin]")
public class AbstractDataEnvelopeAreaOfInterest implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("extent")
    @Valid
    private List<Float> extent = new ArrayList<Float>();

    public AbstractDataEnvelopeAreaOfInterest extent(List<Float> extent) {
        this.extent = extent;
        return this;
    }

    public AbstractDataEnvelopeAreaOfInterest addExtentItem(Float extentItem) {
        this.extent.add(extentItem);
        return this;
    }

    /**
     * the coordinates, using EPSG:4326, (in analogy to GeoJSON bbox) in the order \"southwesterly
     * point followed by more northeasterly point\". Schema is [minLon, minLat, maxLon, maxLat]
     *
     * @return extent
     */
    @ApiModelProperty(
            required = true,
            value =
                    "the coordinates, using EPSG:4326, (in analogy to GeoJSON bbox) in the order \"southwesterly point followed by more northeasterly point\". Schema is [minLon, minLat, maxLon, maxLat] ")
    @NotNull
    @Size(min = 4, max = 4)
    public List<Float> getExtent() {
        return extent;
    }

    public void setExtent(List<Float> extent) {
        this.extent = extent;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractDataEnvelopeAreaOfInterest abstractDataEnvelopeAreaOfInterest =
                (AbstractDataEnvelopeAreaOfInterest) o;
        return Objects.equals(this.extent, abstractDataEnvelopeAreaOfInterest.extent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extent);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AbstractDataEnvelopeAreaOfInterest {\n");

        sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first
     * line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
