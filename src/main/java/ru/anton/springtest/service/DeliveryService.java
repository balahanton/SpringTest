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
import ru.anton.springtest.excteption.EntityNotFoundException;
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

        Delivery delivery = deliveryMapper.toEntity(dto);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("Доставка успешно создана с ID: {}", savedDelivery.getId());
        return deliveryMapper.toResponseDto(savedDelivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryById(UUID id) {

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Delivery with ID " + id + " not found"));

        return deliveryMapper.toResponseDto(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getAllDeliveries(Pageable pageable) {

        Page<Delivery> deliveryPage = deliveryRepository.findAll(pageable);
        return deliveryMapper.toResponseDtoList(deliveryPage.getContent());
    }

    @Transactional
    public DeliveryResponseDto updateDelivery(UUID id, DeliveryUpdateDto dto) {

        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Delivery with ID " + id + " not found"));

        existingDelivery.setAddress(dto.getAddress());
        existingDelivery.setStatus(dto.getStatus());

        if (dto.getDetails() == null) {
            existingDelivery.setDetails(null);
        } else {
            DeliveryDetails existingDetails = existingDelivery.getDetails();
            if (existingDetails == null) {
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

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Delivery with ID " + id + " not found"));

        deliveryRepository.delete(delivery);

        log.info("Доставка с ID {} и её детали успешно удалены", id);
    }
}
