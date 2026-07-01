package ru.anton.springtest.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEnrichmentCreateClientDto {

    private UUID userId;
    private String discountCardNumber;
    private BigDecimal balance;
}
