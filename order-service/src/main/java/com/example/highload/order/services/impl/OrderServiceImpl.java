package com.example.highload.order.services.impl;

import com.example.highload.order.mapper.OrderMapper;
import com.example.highload.order.model.enums.OrderStatus;
import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.Tag;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.services.OrderService;
import com.example.highload.order.services.TagService;
import com.example.highload.order.repos.OrderRepository;
import com.example.highload.order.services.OrderService;
import com.example.highload.order.services.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TagService tagService;
    private final OrderMapper orderMapper;

    @Override
    public Mono<OrderDto> saveOrder(OrderDto orderDto) {
        if (orderDto.getTags().size() > 10) return null;
        return orderRepository.save(orderMapper.orderDtoToOrder(orderDto)).map(orderMapper::orderToDto);
    }

    @Override
    public Mono<OrderDto> updateOrder(OrderDto orderDto, int id) {
        Mono<ClientOrder> order = orderRepository.findById(id);
        order.map(res -> {
            res.setPrice(orderDto.getPrice());
            res.setDescription(orderDto.getDescription());
            res.setStatus(orderDto.getStatus());
            orderRepository.save(res);
            return res;
        });
        return order.map(orderMapper::orderToDto);
    }

    @Override
    public Mono<OrderDto> getOrderById(int id) {
        return orderRepository.findById(id).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getUserOrders(int userId) {
        return orderRepository.findAllByUser_Id(userId).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getUserOpenOrders(int userId) {
        return orderRepository.findAllByUser_IdAndStatus(userId, OrderStatus.OPEN).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getOrdersByTags(List<Integer> tagIds) {
        return orderRepository.findAllByMultipleTagsIds(tagIds, tagIds.size()).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getOpenOrdersByTags(List<Integer> tagIds) {
        return orderRepository.findAllByMultipleTagsIdsAndStatus(tagIds, tagIds.size(), OrderStatus.OPEN.toString()).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getAllOrders() {
        return orderRepository.findAll().map(orderMapper::orderToDto);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public Mono<OrderDto> addTagsToOrder(List<Integer> tagIds, int orderId) {
        Mono<ClientOrder> order = orderRepository.findById(orderId);
        Flux<Tag> tagsToAdd = Flux.empty();
        tagIds.stream().forEach(id -> {
            Flux.concat(tagService.findById(id), tagsToAdd);
        });
        order.subscribe(res -> {
                    List<Tag> newTags = res.getTags().stream().filter(id -> !tagIds.contains(id)).toList();
                    newTags.addAll(tagsToAdd.collectList().block());
                    res.setTags(newTags);
                    orderRepository.save(res);
                }
            );
        return order.map(orderMapper::orderToDto);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public Mono<OrderDto> deleteTagsFromOrder(List<Integer> tagIds, int orderId) {
        Mono<ClientOrder> order = orderRepository.findById(orderId);
        order.subscribe(res -> {
            res.setTags(res.getTags().stream().filter(tag -> !tagIds.contains(tag.getId())).toList());
            orderRepository.save(res);
        });
        return order.map(orderMapper::orderToDto);
    }
}
