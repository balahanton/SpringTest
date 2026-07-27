package ru.anton.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.anton.springtest.dto.OrderCreateDto;
import ru.anton.springtest.dto.OrderResponseDto;
import ru.anton.springtest.dto.OrderUpdateDto;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserEnrichmentClientRequestDto;
import ru.anton.springtest.dto.UserEnrichmentClientResponseDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.SagaTaskStatus;
import ru.anton.springtest.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-27T13:19:14+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.4.1.jar, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId( user.getId() );
        userResponseDto.setUsername( user.getUsername() );
        userResponseDto.setOrders( orderListToOrderResponseDtoList( user.getOrders() ) );

        return userResponseDto;
    }

    @Override
    public UserResponseDto toResponseDto(User user, UserEnrichmentClientResponseDto enrichment) {
        if ( user == null && enrichment == null ) {
            return null;
        }

        UserResponseDto userResponseDto = new UserResponseDto();

        if ( user != null ) {
            userResponseDto.setId( user.getId() );
            userResponseDto.setUsername( user.getUsername() );
            userResponseDto.setOrders( orderListToOrderResponseDtoList( user.getOrders() ) );
        }
        if ( enrichment != null ) {
            userResponseDto.setDiscountCardNumber( enrichment.getDiscountCardNumber() );
            userResponseDto.setBalance( enrichment.getBalance() );
        }

        return userResponseDto;
    }

    @Override
    public List<UserResponseDto> toResponseDtoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponseDto> list = new ArrayList<UserResponseDto>( users.size() );
        for ( User user : users ) {
            list.add( toResponseDto( user ) );
        }

        return list;
    }

    @Override
    public UserEnrichmentClientRequestDto toEnrichmentCreateDto(SagaTask task) {
        if ( task == null ) {
            return null;
        }

        UserEnrichmentClientRequestDto.UserEnrichmentClientRequestDtoBuilder userEnrichmentClientRequestDto = UserEnrichmentClientRequestDto.builder();

        userEnrichmentClientRequestDto.userId( task.getUserId() );
        userEnrichmentClientRequestDto.discountCardNumber( task.getDiscountCardNumber() );
        userEnrichmentClientRequestDto.balance( task.getBalance() );

        return userEnrichmentClientRequestDto.build();
    }

    @Override
    public UserEnrichmentClientRequestDto toEnrichmentRequest(User user, UserCreateDto dto) {
        if ( user == null && dto == null ) {
            return null;
        }

        UserEnrichmentClientRequestDto.UserEnrichmentClientRequestDtoBuilder userEnrichmentClientRequestDto = UserEnrichmentClientRequestDto.builder();

        if ( user != null ) {
            userEnrichmentClientRequestDto.userId( user.getId() );
        }
        if ( dto != null ) {
            userEnrichmentClientRequestDto.discountCardNumber( dto.getDiscountCardNumber() );
            userEnrichmentClientRequestDto.balance( dto.getBalance() );
        }

        return userEnrichmentClientRequestDto.build();
    }

    @Override
    public User toEntity(UserCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( dto.getUsername() );
        user.setOrders( orderCreateDtoListToOrderList( dto.getOrders() ) );

        user.setEnrichmentStatus( SagaTaskStatus.PENDING );

        linkOrders( user );

        return user;
    }

    @Override
    public SagaTask toSagaTask(User user, UserCreateDto dto) {
        if ( user == null && dto == null ) {
            return null;
        }

        SagaTask sagaTask = new SagaTask();

        if ( user != null ) {
            sagaTask.setUserId( user.getId() );
        }
        if ( dto != null ) {
            sagaTask.setDiscountCardNumber( dto.getDiscountCardNumber() );
            sagaTask.setBalance( dto.getBalance() );
        }
        sagaTask.setStatus( SagaTaskStatus.PENDING );
        sagaTask.setAttempts( 0 );

        return sagaTask;
    }

    @Override
    public void updateEntity(UserUpdateDto dto, User user) {
        if ( dto == null ) {
            return;
        }

        user.setUsername( dto.getUsername() );
        if ( user.getOrders() != null ) {
            List<Order> list = orderUpdateDtoListToOrderList( dto.getOrders() );
            if ( list != null ) {
                user.getOrders().clear();
                user.getOrders().addAll( list );
            }
            else {
                user.setOrders( null );
            }
        }
        else {
            List<Order> list = orderUpdateDtoListToOrderList( dto.getOrders() );
            if ( list != null ) {
                user.setOrders( list );
            }
        }

        linkOrders( user );
    }

    protected List<OrderResponseDto> orderListToOrderResponseDtoList(List<Order> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderResponseDto> list1 = new ArrayList<OrderResponseDto>( list.size() );
        for ( Order order : list ) {
            list1.add( toOrderResponseDto( order ) );
        }

        return list1;
    }

    protected List<Order> orderCreateDtoListToOrderList(List<OrderCreateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Order> list1 = new ArrayList<Order>( list.size() );
        for ( OrderCreateDto orderCreateDto : list ) {
            list1.add( toOrderEntity( orderCreateDto ) );
        }

        return list1;
    }

    protected List<Order> orderUpdateDtoListToOrderList(List<OrderUpdateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Order> list1 = new ArrayList<Order>( list.size() );
        for ( OrderUpdateDto orderUpdateDto : list ) {
            list1.add( toOrderEntityFromUpdate( orderUpdateDto ) );
        }

        return list1;
    }
}
