package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.anton.springtest.dto.ProductUpdateDto;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * WarehouseUpdateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-02T18:02:56.808734+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class WarehouseUpdateDto {

  private String name;

  @Valid
  private List<@Valid ProductUpdateDto> products;

  public WarehouseUpdateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public WarehouseUpdateDto(String name) {
    this.name = name;
  }

  public WarehouseUpdateDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public WarehouseUpdateDto products(List<@Valid ProductUpdateDto> products) {
    this.products = products;
    return this;
  }

  public WarehouseUpdateDto addProductsItem(ProductUpdateDto productsItem) {
    if (this.products == null) {
      this.products = new ArrayList<>();
    }
    this.products.add(productsItem);
    return this;
  }

  /**
   * Get products
   * @return products
  */
  @Valid 
  @Schema(name = "products", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("products")
  public List<@Valid ProductUpdateDto> getProducts() {
    return products;
  }

  public void setProducts(List<@Valid ProductUpdateDto> products) {
    this.products = products;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WarehouseUpdateDto warehouseUpdateDto = (WarehouseUpdateDto) o;
    return Objects.equals(this.name, warehouseUpdateDto.name) &&
        Objects.equals(this.products, warehouseUpdateDto.products);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, products);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WarehouseUpdateDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    products: ").append(toIndentedString(products)).append("\n");
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

