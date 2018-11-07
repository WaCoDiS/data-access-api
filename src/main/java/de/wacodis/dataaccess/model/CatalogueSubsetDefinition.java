package de.wacodis.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/** CatalogueSubsetDefinition */
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-11-07T15:19:59.896+01:00[Europe/Berlin]")
public class CatalogueSubsetDefinition extends AbstractSubsetDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("datasetIdentifier")
    private String datasetIdentifier;

    @JsonProperty("serviceUrl")
    private Object serviceUrl = null;

    public CatalogueSubsetDefinition datasetIdentifier(String datasetIdentifier) {
        this.datasetIdentifier = datasetIdentifier;
        return this;
    }

    /**
     * the id of the dataset within the catalogue
     *
     * @return datasetIdentifier
     */
    @ApiModelProperty(required = true, value = "the id of the dataset within the catalogue ")
    @NotNull
    public String getDatasetIdentifier() {
        return datasetIdentifier;
    }

    public void setDatasetIdentifier(String datasetIdentifier) {
        this.datasetIdentifier = datasetIdentifier;
    }

    public CatalogueSubsetDefinition serviceUrl(Object serviceUrl) {
        this.serviceUrl = serviceUrl;
        return this;
    }

    /**
     * the base URL of the catalogue
     *
     * @return serviceUrl
     */
    @ApiModelProperty(required = true, value = "the base URL of the catalogue ")
    @NotNull
    public Object getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(Object serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CatalogueSubsetDefinition catalogueSubsetDefinition = (CatalogueSubsetDefinition) o;
        return Objects.equals(this.datasetIdentifier, catalogueSubsetDefinition.datasetIdentifier)
                && Objects.equals(this.serviceUrl, catalogueSubsetDefinition.serviceUrl)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datasetIdentifier, serviceUrl, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CatalogueSubsetDefinition {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    datasetIdentifier: ")
                .append(toIndentedString(datasetIdentifier))
                .append("\n");
        sb.append("    serviceUrl: ").append(toIndentedString(serviceUrl)).append("\n");
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