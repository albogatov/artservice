package com.example.highload.order.services;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.network.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    ClientOrder saveOrder(OrderDto orderDto);

    ClientOrder updateOrder(OrderDto orderDto, int id);

    ClientOrder getOrderById(int id);

    Page<ClientOrder> getUserOrders(int userId, Pageable pageable);

    Page<ClientOrder> getUserOpenOrders(int userId, Pageable pageable);

    Page<ClientOrder> getOrdersByTags(List<Integer> tagIds, Pageable pageable);

    Page<ClientOrder> getOpenOrdersByTags(List<Integer> tagIds, Pageable pageable);

    Page<ClientOrder> getAllOrders(Pageable pageable);

    ClientOrder addTagsToOrder(List<Integer> tagIds, int orderId);

    ClientOrder deleteTagsFromOrder(List<Integer> tagIds, int orderId);
}
