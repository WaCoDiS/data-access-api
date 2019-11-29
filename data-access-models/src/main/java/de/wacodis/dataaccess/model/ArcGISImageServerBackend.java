package de.wacodis.dataaccess.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.dataaccess.model.AbstractBackend;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * contains specific information about a ArcGIS Image Server product backend
 */
@ApiModel(description = "contains specific information about a ArcGIS Image Server product backend")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-29T10:16:33.967+01:00[Europe/Berlin]")

public class ArcGISImageServerBackend extends AbstractBackend implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("productCollection")
  private String productCollection = null;

  @JsonProperty("baseUrl")
  private String baseUrl = null;

  @JsonProperty("serviceTypes")
  private String serviceTypes = null;

  public ArcGISImageServerBackend productCollection(String productCollection) {
    this.productCollection = productCollection;
    return this;
  }

  /**
   * name of the product collection
   * @return productCollection
  **/
  @ApiModelProperty(required = true, value = "name of the product collection")
  @NotNull


  public String getProductCollection() {
    return productCollection;
  }

  public void setProductCollection(String productCollection) {
    this.productCollection = productCollection;
  }

  public ArcGISImageServerBackend baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  /**
   * base URL of the ArcGIS Image Server backend
   * @return baseUrl
  **/
  @ApiModelProperty(required = true, value = "base URL of the ArcGIS Image Server backend")
  @NotNull


  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public ArcGISImageServerBackend serviceTypes(String serviceTypes) {
    this.serviceTypes = serviceTypes;
    return this;
  }

  /**
   * the supported ArcGIS Image Server services (e.g. ImageServer, WmsServer) 
   * @return serviceTypes
  **/
  @ApiModelProperty(required = true, value = "the supported ArcGIS Image Server services (e.g. ImageServer, WmsServer) ")
  @NotNull


  public String getServiceTypes() {
    return serviceTypes;
  }

  public void setServiceTypes(String serviceTypes) {
    this.serviceTypes = serviceTypes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArcGISImageServerBackend arcGISImageServerBackend = (ArcGISImageServerBackend) o;
    return Objects.equals(this.productCollection, arcGISImageServerBackend.productCollection) &&
        Objects.equals(this.baseUrl, arcGISImageServerBackend.baseUrl) &&
        Objects.equals(this.serviceTypes, arcGISImageServerBackend.serviceTypes) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productCollection, baseUrl, serviceTypes, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArcGISImageServerBackend {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    productCollection: ").append(toIndentedString(productCollection)).append("\n");
    sb.append("    baseUrl: ").append(toIndentedString(baseUrl)).append("\n");
    sb.append("    serviceTypes: ").append(toIndentedString(serviceTypes)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

