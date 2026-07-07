package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProductCreateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-07-06T15:30:13.211754700+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class ProductCreateDto {

  private String title;

  private BigDecimal price;

  public ProductCreateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProductCreateDto(String title, BigDecimal price) {
    this.title = title;
    this.price = price;
  }

  public ProductCreateDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  @NotNull @Size(min = 5, max = 255) 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ProductCreateDto price(BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * minimum: 1
   * @return price
  */
  @NotNull @Valid @DecimalMin("1") 
  @Schema(name = "price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("price")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductCreateDto productCreateDto = (ProductCreateDto) o;
    return Objects.equals(this.title, productCreateDto.title) &&
        Objects.equals(this.price, productCreateDto.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, price);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductCreateDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
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

