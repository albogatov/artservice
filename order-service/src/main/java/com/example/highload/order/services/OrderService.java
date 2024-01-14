package com.example.highload.order.services;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.network.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {

    Mono<OrderDto> saveOrder(OrderDto orderDto);

    Mono<ClientOrder> saveOrder(ClientOrder order);

    Mono<OrderDto> updateOrder(OrderDto orderDto, int id);

    Mono<OrderDto> getOrderById(int id);

    Mono<ClientOrder> findById(int id);

    Flux<OrderDto> getUserOrders(int userId);

    Flux<OrderDto> getUserOpenOrders(int userId);

    Flux<OrderDto> getOrdersByTags(List<Integer> tagIds);

    Flux<OrderDto> getOpenOrdersByTags(List<Integer> tagIds);

    Flux<OrderDto> getAllOrders();

    Mono<OrderDto> addTagsToOrder(List<Integer> tagIds, int orderId);

    Mono<OrderDto> deleteTagsFromOrder(List<Integer> tagIds, int orderId);
}
