package com.example.highload.order.services;

import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.ResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ResponseService {

    Mono<ResponseDto> saveResponse(ResponseDto responseDto);

    Flux<ResponseDto> findAllForOrder(int orderId);

    Flux<ResponseDto> findAllForUser(int userId);

    Mono<ResponseDto> findById(int id);
}
