package ru.anton.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-13T11:19:50+0300",
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
}
