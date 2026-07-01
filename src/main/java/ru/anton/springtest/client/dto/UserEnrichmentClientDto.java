package ru.anton.springtest.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserEnrichmentClientDto {

    private UUID id;
    private UUID userId;
    private String discountCardNumber;
    private BigDecimal balance;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
