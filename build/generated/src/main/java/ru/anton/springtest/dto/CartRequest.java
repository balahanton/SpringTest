package ru.anton.springtest.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CartRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-27T11:23:40.688816300+03:00[Europe/Moscow]", comments = "Generator version: 7.4.0")
public class CartRequest {

  @Valid
  private List<UUID> productIds;

  public CartRequest productIds(List<UUID> productIds) {
    this.productIds = productIds;
    return this;
  }

  public CartRequest addProductIdsItem(UUID productIdsItem) {
    if (this.productIds == null) {
      this.productIds = new ArrayList<>();
    }
    this.productIds.add(productIdsItem);
    return this;
  }

  /**
   * Get productIds
   * @return productIds
  */
  @Valid 
  @Schema(name = "productIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("productIds")
  public List<UUID> getProductIds() {
    return productIds;
  }

  public void setProductIds(List<UUID> productIds) {
    this.productIds = productIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CartRequest cartRequest = (CartRequest) o;
    return Objects.equals(this.productIds, cartRequest.productIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CartRequest {\n");
    sb.append("    productIds: ").append(toIndentedString(productIds)).append("\n");
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

