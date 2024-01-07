package com.example.highload.order.mapper;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.User;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.model.network.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto orderToDto(ClientOrder order);

    ClientOrder orderDtoToOrder(OrderDto orderDto);
}
