package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DeliveryDetailsUpdateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-16T15:53:58.385910500+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class DeliveryDetailsUpdateDto {

  private UUID id;

  private String courierName;

  private String deliveryNotes;

  public DeliveryDetailsUpdateDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public DeliveryDetailsUpdateDto courierName(String courierName) {
    this.courierName = courierName;
    return this;
  }

  /**
   * Get courierName
   * @return courierName
  */
  @Size(min = 2, max = 50) 
  @Schema(name = "courierName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courierName")
  public String getCourierName() {
    return courierName;
  }

  public void setCourierName(String courierName) {
    this.courierName = courierName;
  }

  public DeliveryDetailsUpdateDto deliveryNotes(String deliveryNotes) {
    this.deliveryNotes = deliveryNotes;
    return this;
  }

  /**
   * Get deliveryNotes
   * @return deliveryNotes
  */
  @Size(min = 5, max = 255) 
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
    DeliveryDetailsUpdateDto deliveryDetailsUpdateDto = (DeliveryDetailsUpdateDto) o;
    return Objects.equals(this.id, deliveryDetailsUpdateDto.id) &&
        Objects.equals(this.courierName, deliveryDetailsUpdateDto.courierName) &&
        Objects.equals(this.deliveryNotes, deliveryDetailsUpdateDto.deliveryNotes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, courierName, deliveryNotes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryDetailsUpdateDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

