package de.wacodis.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** DataAccessResourceSearchBody */
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-11-27T14:57:01.610+01:00[Europe/Berlin]")
public class DataAccessResourceSearchBody implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("areaOfInterest")
    private AbstractDataEnvelopeAreaOfInterest areaOfInterest = null;

    @JsonProperty("timeFrame")
    private AbstractDataEnvelopeTimeFrame timeFrame = null;

    @JsonProperty("inputs")
    @Valid
    private List<AbstractSubsetDefinition> inputs = new ArrayList<AbstractSubsetDefinition>();

    public DataAccessResourceSearchBody areaOfInterest(
            AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
        this.areaOfInterest = areaOfInterest;
        return this;
    }

    /**
     * Get areaOfInterest
     *
     * @return areaOfInterest
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    public AbstractDataEnvelopeAreaOfInterest getAreaOfInterest() {
        return areaOfInterest;
    }

    public void setAreaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
        this.areaOfInterest = areaOfInterest;
    }

    public DataAccessResourceSearchBody timeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
        this.timeFrame = timeFrame;
        return this;
    }

    /**
     * Get timeFrame
     *
     * @return timeFrame
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    public AbstractDataEnvelopeTimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public DataAccessResourceSearchBody inputs(List<AbstractSubsetDefinition> inputs) {
        this.inputs = inputs;
        return this;
    }

    public DataAccessResourceSearchBody addInputsItem(AbstractSubsetDefinition inputsItem) {
        this.inputs.add(inputsItem);
        return this;
    }

    /**
     * Get inputs
     *
     * @return inputs
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    public List<AbstractSubsetDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(List<AbstractSubsetDefinition> inputs) {
        this.inputs = inputs;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataAccessResourceSearchBody dataAccessResourceSearchBody =
                (DataAccessResourceSearchBody) o;
        return Objects.equals(this.areaOfInterest, dataAccessResourceSearchBody.areaOfInterest)
                && Objects.equals(this.timeFrame, dataAccessResourceSearchBody.timeFrame)
                && Objects.equals(this.inputs, dataAccessResourceSearchBody.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(areaOfInterest, timeFrame, inputs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataAccessResourceSearchBody {\n");

        sb.append("    areaOfInterest: ").append(toIndentedString(areaOfInterest)).append("\n");
        sb.append("    timeFrame: ").append(toIndentedString(timeFrame)).append("\n");
        sb.append("    inputs: ").append(toIndentedString(inputs)).append("\n");
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
