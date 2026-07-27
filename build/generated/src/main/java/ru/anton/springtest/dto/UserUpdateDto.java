package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.anton.springtest.dto.OrderUpdateDto;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserUpdateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-07-27T13:12:49.131843500+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class UserUpdateDto {

  private String username;

  @Valid
  private List<@Valid OrderUpdateDto> orders;

  public UserUpdateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserUpdateDto(String username) {
    this.username = username;
  }

  public UserUpdateDto username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  @NotNull @Size(min = 2, max = 50) 
  @Schema(name = "username", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserUpdateDto orders(List<@Valid OrderUpdateDto> orders) {
    this.orders = orders;
    return this;
  }

  public UserUpdateDto addOrdersItem(OrderUpdateDto ordersItem) {
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
  public List<@Valid OrderUpdateDto> getOrders() {
    return orders;
  }

  public void setOrders(List<@Valid OrderUpdateDto> orders) {
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
    UserUpdateDto userUpdateDto = (UserUpdateDto) o;
    return Objects.equals(this.username, userUpdateDto.username) &&
        Objects.equals(this.orders, userUpdateDto.orders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, orders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserUpdateDto {\n");
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

