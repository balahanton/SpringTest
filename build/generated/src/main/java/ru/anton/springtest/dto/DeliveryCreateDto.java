package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import ru.anton.springtest.dto.DeliveryDetailsCreateDto;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DeliveryCreateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-10T18:56:10.992948900+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class DeliveryCreateDto {

  private String address;

  private String status;

  private DeliveryDetailsCreateDto details;

  public DeliveryCreateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DeliveryCreateDto(String address, String status) {
    this.address = address;
    this.status = status;
  }

  public DeliveryCreateDto address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
  */
  @NotNull @Size(min = 5, max = 255) 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public DeliveryCreateDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @NotNull 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public DeliveryCreateDto details(DeliveryDetailsCreateDto details) {
    this.details = details;
    return this;
  }

  /**
   * Get details
   * @return details
  */
  @Valid 
  @Schema(name = "details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public DeliveryDetailsCreateDto getDetails() {
    return details;
  }

  public void setDetails(DeliveryDetailsCreateDto details) {
    this.details = details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryCreateDto deliveryCreateDto = (DeliveryCreateDto) o;
    return Objects.equals(this.address, deliveryCreateDto.address) &&
        Objects.equals(this.status, deliveryCreateDto.status) &&
        Objects.equals(this.details, deliveryCreateDto.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, status, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryCreateDto {\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

