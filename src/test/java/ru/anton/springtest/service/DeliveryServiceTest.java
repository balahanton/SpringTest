package ru.anton.springtest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.DeliveryMapper;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;
import ru.anton.springtest.repository.DeliveryRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.anton.springtest.support.DeliveryTestFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryService — юнит-тесты")
public class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryMapper deliveryMapper;

    @InjectMocks
    private DeliveryService deliveryService;

    private UUID deliveryId;
    private Delivery delivery;
    private DeliveryResponseDto responseDto;

    @BeforeEach
    void setUp() {
        deliveryId = UUID.randomUUID();
        delivery = newDelivery(DEFAULT_ADDRESS, DEFAULT_STATUS);
        delivery.setId(deliveryId);
        delivery.setIsDeleted(false);

        responseDto = new DeliveryResponseDto();
        responseDto.setId(deliveryId);
        responseDto.setAddress(DEFAULT_ADDRESS);
        responseDto.setStatus(DEFAULT_STATUS);
    }

    @Nested
    @DisplayName("createDelivery")
    class CreateDelivery {

        @Test
        @DisplayName("создаёт доставку через маппер и репозиторий")
        void createDelivery_savesAndReturnsDto() {
            var dto = deliveryCreateDto(DEFAULT_ADDRESS, DEFAULT_STATUS);
            given(deliveryMapper.toEntity(dto)).willReturn(delivery);
            given(deliveryRepository.save(delivery)).willReturn(delivery);
            given(deliveryMapper.toResponseDto(delivery)).willReturn(responseDto);

            DeliveryResponseDto result = deliveryService.createDelivery(dto);

            assertThat(result.getId()).isEqualTo(deliveryId);
            verify(deliveryRepository).save(delivery);
        }
    }

    @Nested
    @DisplayName("getDeliveryById")
    class GetDeliveryById {

        @Test
        @DisplayName("возвращает DTO для существующей доставки")
        void getDeliveryById_found_returnsDto() {
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));
            given(deliveryMapper.toResponseDto(delivery)).willReturn(responseDto);

            DeliveryResponseDto result = deliveryService.getDeliveryById(deliveryId);

            assertThat(result.getId()).isEqualTo(deliveryId);
        }

        @Test
        @DisplayName("несуществующая доставка — EntityNotFoundException")
        void getDeliveryById_notFound_throws() {
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.getDeliveryById(deliveryId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("удалённая доставка — EntityNotFoundException")
        void getDeliveryById_deleted_throws() {
            delivery.setIsDeleted(true);
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

            assertThatThrownBy(() -> deliveryService.getDeliveryById(deliveryId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateDelivery")
    class UpdateDelivery {

        @Test
        @DisplayName("обновляет поля через маппер и сохраняет")
        void updateDelivery_updatesFieldsAndReturnsDto() {
            DeliveryUpdateDto dto = deliveryUpdateDto(UPDATED_ADDRESS, UPDATED_STATUS);
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));
            given(deliveryRepository.save(delivery)).willReturn(delivery);

            DeliveryResponseDto updatedResponse = new DeliveryResponseDto();
            updatedResponse.setId(deliveryId);
            updatedResponse.setAddress(UPDATED_ADDRESS);
            updatedResponse.setStatus(UPDATED_STATUS);
            given(deliveryMapper.toResponseDto(delivery)).willReturn(updatedResponse);

            DeliveryResponseDto result = deliveryService.updateDelivery(deliveryId, dto);

            verify(deliveryMapper).updateEntity(dto, delivery);
            assertThat(result.getAddress()).isEqualTo(UPDATED_ADDRESS);
        }

        @Test
        @DisplayName("несуществующая доставка — EntityNotFoundException")
        void updateDelivery_notFound_throws() {
            DeliveryUpdateDto dto = deliveryUpdateDto(UPDATED_ADDRESS, UPDATED_STATUS);
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.updateDelivery(deliveryId, dto))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteDelivery")
    class DeleteDelivery {

        @Test
        @DisplayName("soft delete доставки без деталей")
        void deleteDelivery_withoutDetails_softDeletesDeliveryOnly() {
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

            deliveryService.deleteDelivery(deliveryId);

            assertThat(delivery.getIsDeleted()).isTrue();
            verify(deliveryRepository).save(delivery);
        }

        @Test
        @DisplayName("soft delete доставки каскадно помечает детали удалёнными")
        void deleteDelivery_withDetails_cascadesToDetails() {
            DeliveryDetails details = newDeliveryDetails(DEFAULT_COURIER_NAME, DEFAULT_DELIVERY_NOTES, delivery);
            delivery.setDetails(details);
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

            deliveryService.deleteDelivery(deliveryId);

            assertThat(delivery.getIsDeleted()).isTrue();
            assertThat(details.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("несуществующая доставка — EntityNotFoundException, save не вызывается")
        void deleteDelivery_notFound_throwsAndDoesNotSave() {
            given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.deleteDelivery(deliveryId))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(deliveryRepository, never()).save(any());
        }
    }
}