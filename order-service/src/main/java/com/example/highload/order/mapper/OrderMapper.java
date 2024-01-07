package com.example.highload.order.mapper;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.User;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.model.network.UserDto;
import jakarta.persistence.criteria.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface OrderMapper {

    OrderDto orderToDto(ClientOrder order);

    ClientOrder orderDtoToOrder(OrderDto orderDto);

    List<ClientOrder> orderDtoListToOrderList(List<OrderDto> orders);
    List<OrderDto> orderListToOrderDtoList(List<ClientOrder> orders);
}
