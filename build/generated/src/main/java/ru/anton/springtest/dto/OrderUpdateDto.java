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
 * OrderUpdateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-08T14:28:59.128101500+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class OrderUpdateDto {

  @Valid 
  private UUID id;

  @Size(min = 5, max = 255) 
  private String description;

  public OrderUpdateDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Если ID передан — обновляем существующий заказ, если нет — создаем новый внутри пользователя
   * @return id
  */
  @Schema(name = "id", description = "Если ID передан — обновляем существующий заказ, если нет — создаем новый внутри пользователя", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OrderUpdateDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderUpdateDto orderUpdateDto = (OrderUpdateDto) o;
    return Objects.equals(this.id, orderUpdateDto.id) &&
        Objects.equals(this.description, orderUpdateDto.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderUpdateDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
