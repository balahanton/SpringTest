package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DeliveryDetailsCreateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-02T15:15:50.532087900+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class DeliveryDetailsCreateDto {

  private String courierName;

  private String deliveryNotes;

  public DeliveryDetailsCreateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DeliveryDetailsCreateDto(String courierName) {
    this.courierName = courierName;
  }

  public DeliveryDetailsCreateDto courierName(String courierName) {
    this.courierName = courierName;
    return this;
  }

  /**
   * Get courierName
   * @return courierName
  */
  @NotNull 
  @Schema(name = "courierName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("courierName")
  public String getCourierName() {
    return courierName;
  }

  public void setCourierName(String courierName) {
    this.courierName = courierName;
  }

  public DeliveryDetailsCreateDto deliveryNotes(String deliveryNotes) {
    this.deliveryNotes = deliveryNotes;
    return this;
  }

  /**
   * Get deliveryNotes
   * @return deliveryNotes
  */
  
  @Schema(name = "deliveryNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deliveryNotes")
  public String getDeliveryNotes() {
    return deliveryNotes;
  }

  public void setDeliveryNotes(String deliveryNotes) {
    this.deliveryNotes = deliveryNotes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryDetailsCreateDto deliveryDetailsCreateDto = (DeliveryDetailsCreateDto) o;
    return Objects.equals(this.courierName, deliveryDetailsCreateDto.courierName) &&
        Objects.equals(this.deliveryNotes, deliveryDetailsCreateDto.deliveryNotes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courierName, deliveryNotes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryDetailsCreateDto {\n");
    sb.append("    courierName: ").append(toIndentedString(courierName)).append("\n");
    sb.append("    deliveryNotes: ").append(toIndentedString(deliveryNotes)).append("\n");
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

