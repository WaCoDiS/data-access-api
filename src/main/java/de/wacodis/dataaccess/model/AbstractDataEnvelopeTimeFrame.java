package de.wacodis.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.joda.time.DateTime;

/** time frame the dataset covers */
@ApiModel(description = "time frame the dataset covers")
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-11-07T15:19:59.896+01:00[Europe/Berlin]")
public class AbstractDataEnvelopeTimeFrame implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("startTime")
    private DateTime startTime;

    @JsonProperty("endTime")
    private DateTime endTime;

    public AbstractDataEnvelopeTimeFrame startTime(DateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * the beginning of the time frame
     *
     * @return startTime
     */
    @ApiModelProperty(required = true, value = "the beginning of the time frame ")
    @NotNull
    @Valid
    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public AbstractDataEnvelopeTimeFrame endTime(DateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * the ending of the time frame
     *
     * @return endTime
     */
    @ApiModelProperty(required = true, value = "the ending of the time frame ")
    @NotNull
    @Valid
    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractDataEnvelopeTimeFrame abstractDataEnvelopeTimeFrame =
                (AbstractDataEnvelopeTimeFrame) o;
        return Objects.equals(this.startTime, abstractDataEnvelopeTimeFrame.startTime)
                && Objects.equals(this.endTime, abstractDataEnvelopeTimeFrame.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AbstractDataEnvelopeTimeFrame {\n");

        sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
        sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
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
