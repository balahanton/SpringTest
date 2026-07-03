package ru.anton.springtest.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;
import ru.anton.springtest.repository.DeliveryRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.anton.springtest.support.DeliveryTestFixtures.*;

@DisplayName("DeliveryService — тесты Redis-кэша")
public class DeliveryServiceCacheTest extends AbstractIntegrationTest {

    @Autowired
    private DeliveryService deliveryService;

    @MockitoSpyBean
    private DeliveryRepository deliveryRepository;

    private UUID deliveryId;

    @BeforeEach
    void setUp() {
        Delivery delivery = newDelivery(DEFAULT_ADDRESS, DEFAULT_STATUS);
        deliveryId = deliveryRepository.save(delivery).getId();
    }

    @Test
    @DisplayName("повторный getDeliveryById не идёт в БД — данные отдаются из кэша")
    void getDeliveryById_secondCall_isServedFromCache() {
        DeliveryResponseDto first = deliveryService.getDeliveryById(deliveryId);
        DeliveryResponseDto second = deliveryService.getDeliveryById(deliveryId);

        assertThat(first.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(second.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        verify(deliveryRepository, times(1)).findById(deliveryId);
    }

    @Test
    @DisplayName("после deleteDelivery кэш инвалидируется — следующий getDeliveryById снова идёт в БД")
    void deleteDelivery_evictsCache_nextGetGoesToDatabase() {
        deliveryService.getDeliveryById(deliveryId);

        deliveryService.deleteDelivery(deliveryId);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> deliveryService.getDeliveryById(deliveryId));

        verify(deliveryRepository, times(3)).findById(deliveryId);
    }

    @Test
    @DisplayName("после updateDelivery без деталей кэш обновляется свежими данными (@CachePut)")
    void updateDelivery_withoutDetails_refreshesCacheEntry() {
        deliveryService.getDeliveryById(deliveryId);

        DeliveryUpdateDto dto = deliveryUpdateDto(UPDATED_ADDRESS, UPDATED_STATUS);
        deliveryService.updateDelivery(deliveryId, dto);

        DeliveryResponseDto afterUpdate = deliveryService.getDeliveryById(deliveryId);

        assertThat(afterUpdate.getAddress()).isEqualTo(UPDATED_ADDRESS);
        verify(deliveryRepository, times(2)).findById(deliveryId);
    }

    @Test
    @DisplayName("updateDelivery без details у доставки с уже существующими деталями")
    void updateDelivery_omittingDetailsOnExistingDetails_mayThrowOrphanRemoval() {
        Delivery deliveryWithDetails = newDelivery("Another address", DEFAULT_STATUS);
        DeliveryDetails details = newDeliveryDetails(DEFAULT_COURIER_NAME, DEFAULT_DELIVERY_NOTES, deliveryWithDetails);
        deliveryWithDetails.setDetails(details);
        UUID idWithDetails = deliveryRepository.save(deliveryWithDetails).getId();

        DeliveryUpdateDto dto = deliveryUpdateDto(UPDATED_ADDRESS, UPDATED_STATUS);

        deliveryService.updateDelivery(idWithDetails, dto);
    }
}
