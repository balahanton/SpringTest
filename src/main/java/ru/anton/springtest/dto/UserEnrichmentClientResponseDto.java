package ru.anton.springtest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserEnrichmentClientResponseDto {

    private UUID id;
    private UUID userId;
    private String discountCardNumber;
    private BigDecimal balance;
}
