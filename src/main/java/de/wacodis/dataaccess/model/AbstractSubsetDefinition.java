package de.wacodis.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.*;

/** AbstractSubsetDefinition */
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-09-14T15:57:11.999+02:00[Europe/Berlin]")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "sourceType",
        visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SensorWebSubsetDefinition.class, name = "SensorWebSubsetDefinition"),
    @JsonSubTypes.Type(
            value = CopernicusSubsetDefinition.class,
            name = "CopernicusSubsetDefinition"),
    @JsonSubTypes.Type(value = GdiDeSubsetDefinition.class, name = "GdiDeSubsetDefinition"),
})
public class AbstractSubsetDefinition {
    /** shall be used to determine the responsible data backend */
    public enum SourceTypeEnum {
        SENSORWEBSUBSETDEFINITION("SensorWebSubsetDefinition"),

        COPERNICUSSUBSETDEFINITION("CopernicusSubsetDefinition"),

        GDIDESUBSETDEFINITION("GdiDeSubsetDefinition");

        private String value;

        SourceTypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SourceTypeEnum fromValue(String text) {
            for (SourceTypeEnum b : SourceTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + text + "'");
        }
    }

    @JsonProperty("sourceType")
    private SourceTypeEnum sourceType = null;

    public AbstractSubsetDefinition sourceType(SourceTypeEnum sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    /**
     * shall be used to determine the responsible data backend
     *
     * @return sourceType
     */
    @ApiModelProperty(
            required = true,
            value = "shall be used to determine the responsible data backend ")
    @NotNull
    public SourceTypeEnum getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceTypeEnum sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractSubsetDefinition abstractSubsetDefinition = (AbstractSubsetDefinition) o;
        return Objects.equals(this.sourceType, abstractSubsetDefinition.sourceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AbstractSubsetDefinition {\n");

        sb.append("    sourceType: ").append(toIndentedString(sourceType)).append("\n");
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
