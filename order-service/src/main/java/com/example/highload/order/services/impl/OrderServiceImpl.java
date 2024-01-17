package com.example.highload.order.services.impl;

import com.example.highload.order.mapper.OrderMapper;
import com.example.highload.order.mapper.TagMapper;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TagService tagService;
    private final OrderMapper orderMapper;
    private final TagMapper tagMapper;

    @Override
    public Mono<OrderDto> saveOrder(OrderDto orderDto) {
        ClientOrder order = orderMapper.orderDtoToOrder(orderDto);
        if (orderDto.getTags().size() > 10) return null;
        return Mono.just(orderRepository.save(order)).map(orderMapper::orderToDto);
    }

    @Override
    public Mono<ClientOrder> saveOrder(ClientOrder order) {
        if (order.getTags().size() > 10) return null;
        return Mono.just(orderRepository.save(order));
    }

    @Override
    public Mono<OrderDto> updateOrder(OrderDto orderDto, int id) {
        Mono<ClientOrder> order = Mono.just(orderRepository.findById(id).orElseThrow());
        return order.map(res -> {
            res.setPrice(orderDto.getPrice());
            res.setDescription(orderDto.getDescription());
            res.setStatus(orderDto.getStatus());
            return orderRepository.save(res);
        }).map(orderMapper::orderToDto);
    }

    @Override
    public Mono<OrderDto> getOrderById(int id) {
        return Mono.just(orderRepository.findById(id).orElseThrow()).map(orderMapper::orderToDto);
    }

    @Override
    public Mono<ClientOrder> findById(int id) {
        return Mono.just(orderRepository.findById(id).orElseThrow());
    }

    @Override
    public Flux<OrderDto> getUserOrders(int userId) {
        return Flux.fromIterable(orderRepository.findAllByUser_Id(userId).orElseThrow()).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getUserOpenOrders(int userId) {
        return Flux.fromIterable(orderRepository.findAllByUser_IdAndStatus(userId, OrderStatus.OPEN).orElseThrow()).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getOrdersByTags(List<Integer> tagIds) {
        return Flux.fromIterable(orderRepository.findAllByMultipleTagsIds(tagIds, tagIds.size()).orElseThrow()).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getOpenOrdersByTags(List<Integer> tagIds) {
        return Flux.fromIterable(orderRepository.findAllByMultipleTagsIdsAndStatus(tagIds, tagIds.size(), OrderStatus.OPEN.toString()).orElseThrow()).map(orderMapper::orderToDto);
    }

    @Override
    public Flux<OrderDto> getAllOrders() {
        return Flux.fromIterable(orderRepository.findAll()).map(orderMapper::orderToDto);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public Mono<OrderDto> addTagsToOrder(List<Integer> tagIds, int orderId) {
        Mono<ClientOrder> order = Mono.just(orderRepository.findById(orderId).orElseThrow());
        Flux<Integer> tagReceived = Flux.fromIterable(tagIds);
        List<Integer> existingTagIds = order.block().getTags().stream().map(tag -> {
            return tag.getId();
        }).toList();
        return order.map(res -> {
            res.setTags(new ArrayList<>(Stream.concat(tagReceived.flatMap(id -> {
                    return tagService.findById(id).map(tagDto -> {
                        return tagMapper.tagDtoToTag(tagDto);
                    });
            }).filter(tag -> !existingTagIds.contains(tag.getId())).collectList().block().stream(), res.getTags().stream()).toList()));
            return orderRepository.save(res);
        })
                .map(orderMapper::orderToDto);
      }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {NoSuchElementException.class, Exception.class})
    public Mono<OrderDto> deleteTagsFromOrder(List<Integer> tagIds, int orderId) {
        Mono<ClientOrder> order = Mono.just(orderRepository.findById(orderId).orElseThrow());
        return order.map(res -> {
            res.setTags(new ArrayList<>(res.getTags().stream().filter(tag -> !tagIds.contains(tag.getId())).toList()));
            return orderRepository.save(res);
        }).map(orderMapper::orderToDto);
    }
}
