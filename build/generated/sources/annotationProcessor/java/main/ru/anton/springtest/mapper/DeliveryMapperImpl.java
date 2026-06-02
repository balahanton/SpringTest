package ru.anton.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryDetailsCreateDto;
import ru.anton.springtest.dto.DeliveryDetailsResponseDto;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-02T18:20:18+0300",
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
        deliveryResponseDto.setDetails( deliveryDetailsToDeliveryDetailsResponseDto( delivery.getDetails() ) );

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
        delivery.setDetails( deliveryDetailsCreateDtoToDeliveryDetails( dto.getDetails() ) );

        linkDetails( delivery );

        return delivery;
    }

    protected DeliveryDetailsResponseDto deliveryDetailsToDeliveryDetailsResponseDto(DeliveryDetails deliveryDetails) {
        if ( deliveryDetails == null ) {
            return null;
        }

        DeliveryDetailsResponseDto deliveryDetailsResponseDto = new DeliveryDetailsResponseDto();

        deliveryDetailsResponseDto.setId( deliveryDetails.getId() );
        deliveryDetailsResponseDto.setCourierName( deliveryDetails.getCourierName() );
        deliveryDetailsResponseDto.setDeliveryNotes( deliveryDetails.getDeliveryNotes() );

        return deliveryDetailsResponseDto;
    }

    protected DeliveryDetails deliveryDetailsCreateDtoToDeliveryDetails(DeliveryDetailsCreateDto deliveryDetailsCreateDto) {
        if ( deliveryDetailsCreateDto == null ) {
            return null;
        }

        DeliveryDetails deliveryDetails = new DeliveryDetails();

        deliveryDetails.setCourierName( deliveryDetailsCreateDto.getCourierName() );
        deliveryDetails.setDeliveryNotes( deliveryDetailsCreateDto.getDeliveryNotes() );

        return deliveryDetails;
    }
}
