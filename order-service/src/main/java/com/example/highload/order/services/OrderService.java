package com.example.highload.order.services;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.network.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {

    Mono<ClientOrder> saveOrder(OrderDto orderDto);

    ClientOrder updateOrder(OrderDto orderDto, int id);

    Mono<ClientOrder> getOrderById(int id);

    Flux<ClientOrder> getUserOrders(int userId, Pageable pageable);

    Flux<ClientOrder> getUserOpenOrders(int userId, Pageable pageable);

    Flux<ClientOrder> getOrdersByTags(List<Integer> tagIds, Pageable pageable);

    Flux<ClientOrder> getOpenOrdersByTags(List<Integer> tagIds, Pageable pageable);

    Flux<ClientOrder> getAllOrders(Pageable pageable);

    ClientOrder addTagsToOrder(List<Integer> tagIds, int orderId);

    ClientOrder deleteTagsFromOrder(List<Integer> tagIds, int orderId);
}
