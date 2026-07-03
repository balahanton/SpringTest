package ru.anton.springtest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryDetailsCreateDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.repository.DeliveryRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.anton.springtest.support.DeliveryTestFixtures.*;

@DisplayName("DeliveryController — интеграционные тесты")
public class DeliveryControllerTest extends AbstractIntegrationTest {

    private static final String DELIVERIES_URL = "/api/v1/deliveries";
    private static final String DELIVERY_BY_ID_URL = "/api/v1/deliveries/{id}";
    private static final String INVALID_FIELDS_PATH = "$.invalid_fields";

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("POST /api/v1/deliveries с валидным телом — 201 и тело с id")
    void createDelivery_valid_returns201WithId() throws Exception {
        DeliveryCreateDto dto = deliveryCreateDto(DEFAULT_ADDRESS, DEFAULT_STATUS);

        mockMvc.perform(postJson(DELIVERIES_URL, dto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }

    @Test
    @DisplayName("POST /api/v1/deliveries с деталями — 201, детали сохранены и привязаны")
    void createDelivery_withDetails_returns201WithDetails() throws Exception {
        DeliveryDetailsCreateDto detailsDto = deliveryDetailsCreateDto(DEFAULT_COURIER_NAME, DEFAULT_DELIVERY_NOTES);
        DeliveryCreateDto dto = deliveryCreateDtoWithDetails(DEFAULT_ADDRESS, DEFAULT_STATUS, detailsDto);

        mockMvc.perform(postJson(DELIVERIES_URL, dto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.details.courierName").value(DEFAULT_COURIER_NAME));
    }

    @Test
    @DisplayName("POST /api/v1/deliveries без адреса — 400 и invalid_fields")
    void createDelivery_missingAddress_returns400() throws Exception {
        DeliveryCreateDto dto = new DeliveryCreateDto();
        dto.setStatus(DEFAULT_STATUS);

        mockMvc.perform(postJson(DELIVERIES_URL, dto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(INVALID_FIELDS_PATH + ".address").exists());
    }

    @Test
    @DisplayName("GET /api/v1/deliveries — 200 и массив доставок")
    void getAllDeliveries_returns200WithArray() throws Exception {
        deliveryRepository.save(newDelivery(DEFAULT_ADDRESS, DEFAULT_STATUS));
        deliveryRepository.save(newDelivery("Second address", DEFAULT_STATUS));

        mockMvc.perform(get(DELIVERIES_URL).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/deliveries/{id} для существующей доставки — 200")
    void getDeliveryById_existing_returns200() throws Exception {
        Delivery saved = deliveryRepository.save(newDelivery(DEFAULT_ADDRESS, DEFAULT_STATUS));

        mockMvc.perform(get(DELIVERY_BY_ID_URL, saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()));
    }

    @Test
    @DisplayName("GET /api/v1/deliveries/{id} для несуществующей доставки — 404")
    void getDeliveryById_notFound_returns404() throws Exception {
        mockMvc.perform(get(DELIVERY_BY_ID_URL, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/deliveries/{id} без деталей — 200, поля обновлены")
    void updateDelivery_valid_returns200() throws Exception {
        Delivery saved = deliveryRepository.save(newDelivery(DEFAULT_ADDRESS, DEFAULT_STATUS));
        DeliveryUpdateDto dto = deliveryUpdateDto(UPDATED_ADDRESS, UPDATED_STATUS);

        mockMvc.perform(putJson(DELIVERY_BY_ID_URL, dto, saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(UPDATED_ADDRESS));
    }

    @Test
    @DisplayName("PUT /api/v1/deliveries/{id} для несуществующей доставки — 404")
    void updateDelivery_notFound_returns404() throws Exception {
        DeliveryUpdateDto dto = deliveryUpdateDto(UPDATED_ADDRESS, UPDATED_STATUS);

        mockMvc.perform(putJson(DELIVERY_BY_ID_URL, dto, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/deliveries/{id} — 204")
    void deleteDelivery_returns204() throws Exception {
        Delivery saved = deliveryRepository.save(newDelivery(DEFAULT_ADDRESS, DEFAULT_STATUS));

        mockMvc.perform(delete(DELIVERY_BY_ID_URL, saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/deliveries/{id} для несуществующей доставки — 404")
    void deleteDelivery_notFound_returns404() throws Exception {
        mockMvc.perform(delete(DELIVERY_BY_ID_URL, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}