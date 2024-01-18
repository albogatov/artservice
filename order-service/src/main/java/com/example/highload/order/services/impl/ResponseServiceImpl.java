package com.example.highload.order.services.impl;

import com.example.highload.order.mapper.ResponseMapper;
import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.ResponseDto;
import com.example.highload.order.services.ResponseService;
import com.example.highload.order.repos.ResponseRepository;
import com.example.highload.order.services.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;
    private final ResponseMapper responseMapper;
    private final KafkaTemplate<String, ResponseDto> kafkaTemplate;

    @Override
    public Mono<ResponseDto> saveResponse(ResponseDto responseDto) {
        return Mono.just(responseRepository.save(responseMapper.responseDtoToResponse(responseDto))).map(responseMapper::responseToDto);
    }

    @Override
    public Mono<ResponseDto> approve(Integer id) {
        Mono<Response> response = Mono.just(responseRepository.findById(id).orElseThrow());
        return response.map(res -> {
            res.setIsApproved(true);
            kafkaTemplate.send("notifications", responseMapper.responseToDto(res));
            return responseRepository.save(res);
        }).map(responseMapper::responseToDto);
    }

    @Override
    public Flux<ResponseDto> findAllForOrder(int orderId) {
        return Flux.fromIterable(responseRepository.findAllByOrder_Id(orderId).orElseThrow()).map(responseMapper::responseToDto);
    }

    @Override
    public Flux<ResponseDto> findAllForUser(int userId) {
        return Flux.fromIterable(responseRepository.findAllByUser_Id(userId).orElseThrow()).map(responseMapper::responseToDto);
    }

    @Override
    public Mono<ResponseDto> findById(int id) {
        return Mono.just(responseRepository.findById(id).orElseThrow()).map(responseMapper::responseToDto);
    }
}
