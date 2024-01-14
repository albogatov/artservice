package com.example.highload.image.mapper;

import com.example.highload.image.model.inner.ClientOrder;
import com.example.highload.image.model.network.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userName")
    OrderDto orderToDto(ClientOrder order);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "user.login", source = "userName")
    ClientOrder orderDtoToOrder(OrderDto orderDto);

    List<ClientOrder> orderDtoListToOrderList(List<OrderDto> orders);
    List<OrderDto> orderListToOrderDtoList(List<ClientOrder> orders);
}
