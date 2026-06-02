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
        log.info("Начало метода создания доставки по адресу: {}", dto.getAddress());

        Delivery delivery = deliveryMapper.toEntity(dto);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("Доставка успешно создана с ID: {}", savedDelivery.getId());
        return deliveryMapper.toResponseDto(savedDelivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryById(UUID id) {
        log.info("Запрос информации о доставке по ID: {}", id);

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Доставка с ID {} не найдена", id);
                    return new EntityNotFoundException("Доставка с ID " + id + " не найдена");
                });

        return deliveryMapper.toResponseDto(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getAllDeliveries(Pageable pageable) {
        log.info("Запрос списка доставок. Страница: {}, Размер: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Delivery> deliveryPage = deliveryRepository.findAll(pageable);
        return deliveryMapper.toResponseDtoList(deliveryPage.getContent());
    }

    @Transactional
    public DeliveryResponseDto updateDelivery(UUID id, DeliveryUpdateDto dto) {
        log.info("Начало процесса обновления доставки с ID: {}. Статус: {}", id, dto.getStatus());

        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Доставка с ID {} не найдена для обновления", id);
                    return new EntityNotFoundException("Доставка с ID " + id + " не найдена");
                });

        existingDelivery.setAddress(dto.getAddress());
        existingDelivery.setStatus(dto.getStatus());

        if (dto.getDetails() == null) {
            log.info("Детали доставки отсутствуют в запросе. Удаление деталей для доставки ID: {}", id);
            existingDelivery.setDetails(null);
        } else {
            DeliveryDetails existingDetails = existingDelivery.getDetails();
            if (existingDetails == null) {
                log.info("Создание новых деталей для существующей доставки ID: {}", id);
                existingDetails = new DeliveryDetails();
                existingDetails.setDelivery(existingDelivery);
                existingDelivery.setDetails(existingDetails);
            }
            existingDetails.setCourierName(dto.getDetails().getCourierName());
            existingDetails.setDeliveryNotes(dto.getDetails().getDeliveryNotes());
        }

        Delivery updatedDelivery = deliveryRepository.save(existingDelivery);
        log.info("Доставка с ID {} успешно обновлена", id);
        return deliveryMapper.toResponseDto(updatedDelivery);
    }

    @Transactional
    public void deleteDelivery(UUID id) {
        log.info("Запрос на удаление доставки с ID: {}", id);

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Доставка с ID {} не найдена для удаления", id);
                    return new EntityNotFoundException("Доставка с ID " + id + " не найдена");
                });

        deliveryRepository.delete(delivery);

        log.info("Доставка с ID {} и её детали успешно удалены", id);
    }
}