package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import ru.anton.springtest.dto.OrderResponseDto;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-22T11:29:31.079268100+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class UserResponseDto {

  private UUID id;

  private String username;

  @Valid
  private List<@Valid OrderResponseDto> orders;

  public UserResponseDto id(UUID id) {
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

  public UserResponseDto username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  
  @Schema(name = "username", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserResponseDto orders(List<@Valid OrderResponseDto> orders) {
    this.orders = orders;
    return this;
  }

  public UserResponseDto addOrdersItem(OrderResponseDto ordersItem) {
    if (this.orders == null) {
      this.orders = new ArrayList<>();
    }
    this.orders.add(ordersItem);
    return this;
  }

  /**
   * Get orders
   * @return orders
  */
  @Valid 
  @Schema(name = "orders", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orders")
  public List<@Valid OrderResponseDto> getOrders() {
    return orders;
  }

  public void setOrders(List<@Valid OrderResponseDto> orders) {
    this.orders = orders;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserResponseDto userResponseDto = (UserResponseDto) o;
    return Objects.equals(this.id, userResponseDto.id) &&
        Objects.equals(this.username, userResponseDto.username) &&
        Objects.equals(this.orders, userResponseDto.orders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, orders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    orders: ").append(toIndentedString(orders)).append("\n");
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

