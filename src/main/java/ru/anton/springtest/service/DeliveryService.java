package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.DeliveryMapper;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.repository.DeliveryRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Transactional
    public DeliveryResponseDto createDelivery(DeliveryCreateDto dto) {

        Delivery delivery = deliveryMapper.toEntity(dto);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("Доставка успешно создана с ID: {}", savedDelivery.getId());
        return deliveryMapper.toResponseDto(savedDelivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryById(UUID id) {

        Delivery delivery = findDeliveryOrThrow(id);

        return deliveryMapper.toResponseDto(delivery);
    }

    @Transactional
    public List<DeliveryResponseDto> getAllDeliveries(Pageable pageable) {

        Page<Delivery> deliveryPage = deliveryRepository.findWithLockByIsDeletedFalse(pageable);
        return deliveryMapper.toResponseDtoList(deliveryPage.getContent());
    }

    @Transactional
    public DeliveryResponseDto updateDelivery(UUID id, DeliveryUpdateDto dto) {

        Delivery existingDelivery = findDeliveryOrThrow(id);

        deliveryMapper.updateEntity(dto, existingDelivery);

        Delivery updatedDelivery = deliveryRepository.save(existingDelivery);
        log.info("Успешно обновлена доставка с ID: {}", id);
        return deliveryMapper.toResponseDto(updatedDelivery);
    }

    @Transactional
    public void deleteDelivery(UUID id) {

        Delivery delivery = findDeliveryOrThrow(id);

        delivery.setIsDeleted(true);

        if (delivery.getDetails() != null) {
            delivery.getDetails().setIsDeleted(true);
        }


        log.info("Доставка с ID {} и её детали успешно удалены", id);
    }

    private Delivery findDeliveryOrThrow(UUID id) {
        return deliveryRepository.findById(id)
                .filter(delivery -> !delivery.getIsDeleted())
                .orElseThrow(() -> {
                    log.error("Доставка с ID {} не найдена или была удалена", id);
                    return new EntityNotFoundException("Delivery with ID " + id + " not found");
                });
    }
}