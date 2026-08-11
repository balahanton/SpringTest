package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.anton.springtest.dto.OrderCreateDto;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserCreateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-07-20T12:35:07.455428200+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class UserCreateDto {

  private String username;

  @Valid
  private List<@Valid OrderCreateDto> orders;

  private String discountCardNumber;

  private BigDecimal balance;

  public UserCreateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserCreateDto(String username, String discountCardNumber, BigDecimal balance) {
    this.username = username;
    this.discountCardNumber = discountCardNumber;
    this.balance = balance;
  }

  public UserCreateDto username(String username) {
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

  public UserCreateDto orders(List<@Valid OrderCreateDto> orders) {
    this.orders = orders;
    return this;
  }

  public UserCreateDto addOrdersItem(OrderCreateDto ordersItem) {
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
  public List<@Valid OrderCreateDto> getOrders() {
    return orders;
  }

  public void setOrders(List<@Valid OrderCreateDto> orders) {
    this.orders = orders;
  }

  public UserCreateDto discountCardNumber(String discountCardNumber) {
    this.discountCardNumber = discountCardNumber;
    return this;
  }

  /**
   * Номер скидочной карты
   * @return discountCardNumber
  */
  @NotNull 
  @Schema(name = "discountCardNumber", description = "Номер скидочной карты", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("discountCardNumber")
  public String getDiscountCardNumber() {
    return discountCardNumber;
  }

  public void setDiscountCardNumber(String discountCardNumber) {
    this.discountCardNumber = discountCardNumber;
  }

  public UserCreateDto balance(BigDecimal balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Начальный баланс скидочной карты
   * minimum: 0
   * @return balance
  */
  @NotNull @Valid @DecimalMin("0") 
  @Schema(name = "balance", description = "Начальный баланс скидочной карты", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("balance")
  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCreateDto userCreateDto = (UserCreateDto) o;
    return Objects.equals(this.username, userCreateDto.username) &&
        Objects.equals(this.orders, userCreateDto.orders) &&
        Objects.equals(this.discountCardNumber, userCreateDto.discountCardNumber) &&
        Objects.equals(this.balance, userCreateDto.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, orders, discountCardNumber, balance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserCreateDto {\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    orders: ").append(toIndentedString(orders)).append("\n");
    sb.append("    discountCardNumber: ").append(toIndentedString(discountCardNumber)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
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

