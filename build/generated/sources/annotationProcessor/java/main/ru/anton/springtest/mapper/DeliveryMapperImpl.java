package ru.anton.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryDetailsCreateDto;
import ru.anton.springtest.dto.DeliveryDetailsResponseDto;
import ru.anton.springtest.dto.DeliveryDetailsUpdateDto;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-08T14:29:09+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.4.1.jar, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class DeliveryMapperImpl implements DeliveryMapper {

    @Override
    public DeliveryResponseDto toResponseDto(Delivery delivery) {
        if ( delivery == null ) {
            return null;
        }

        DeliveryResponseDto deliveryResponseDto = new DeliveryResponseDto();

        deliveryResponseDto.setId( delivery.getId() );
        deliveryResponseDto.setAddress( delivery.getAddress() );
        deliveryResponseDto.setStatus( delivery.getStatus() );
        deliveryResponseDto.setDetails( toDetailsResponseDto( delivery.getDetails() ) );

        return deliveryResponseDto;
    }

    @Override
    public List<DeliveryResponseDto> toResponseDtoList(List<Delivery> deliveries) {
        if ( deliveries == null ) {
            return null;
        }

        List<DeliveryResponseDto> list = new ArrayList<DeliveryResponseDto>( deliveries.size() );
        for ( Delivery delivery : deliveries ) {
            list.add( toResponseDto( delivery ) );
        }

        return list;
    }

    @Override
    public Delivery toEntity(DeliveryCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Delivery delivery = new Delivery();

        delivery.setAddress( dto.getAddress() );
        delivery.setStatus( dto.getStatus() );
        delivery.setDetails( toDetailsEntity( dto.getDetails() ) );

        linkDetails( delivery );

        return delivery;
    }

    @Override
    public void updateEntity(DeliveryUpdateDto dto, Delivery delivery) {
        if ( dto == null ) {
            return;
        }

        delivery.setAddress( dto.getAddress() );
        delivery.setStatus( dto.getStatus() );
        if ( dto.getDetails() != null ) {
            if ( delivery.getDetails() == null ) {
                delivery.setDetails( new DeliveryDetails() );
            }
            updateDetailsEntity( dto.getDetails(), delivery.getDetails() );
        }
        else {
            delivery.setDetails( null );
        }

        linkDetails( delivery );
    }

    @Override
    public DeliveryDetailsResponseDto toDetailsResponseDto(DeliveryDetails details) {
        if ( details == null ) {
            return null;
        }

        DeliveryDetailsResponseDto deliveryDetailsResponseDto = new DeliveryDetailsResponseDto();

        deliveryDetailsResponseDto.setId( details.getId() );
        deliveryDetailsResponseDto.setCourierName( details.getCourierName() );
        deliveryDetailsResponseDto.setDeliveryNotes( details.getDeliveryNotes() );

        return deliveryDetailsResponseDto;
    }

    @Override
    public DeliveryDetails toDetailsEntity(DeliveryDetailsCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        DeliveryDetails deliveryDetails = new DeliveryDetails();

        deliveryDetails.setCourierName( dto.getCourierName() );
        deliveryDetails.setDeliveryNotes( dto.getDeliveryNotes() );

        return deliveryDetails;
    }

    @Override
    public void updateDetailsEntity(DeliveryDetailsUpdateDto dto, DeliveryDetails details) {
        if ( dto == null ) {
            return;
        }

        details.setCourierName( dto.getCourierName() );
        details.setDeliveryNotes( dto.getDeliveryNotes() );
    }
}
