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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;
    private final ResponseMapper responseMapper;

    @Override
    public Mono<ResponseDto> saveResponse(ResponseDto responseDto) {
        return responseRepository.save(responseMapper.responseDtoToResponse(responseDto)).map(responseMapper::responseToDto);
    }

    @Override
    public Flux<ResponseDto> findAllForOrder(int orderId) {
        return responseRepository.findAllByOrder_Id(orderId).map(responseMapper::responseToDto);
    }

    @Override
    public Flux<ResponseDto> findAllForUser(int userId) {
        return responseRepository.findAllByUser_Id(userId).map(responseMapper::responseToDto);
    }

    @Override
    public Mono<ResponseDto> findById(int id) {
        return responseRepository.findById(id).map(responseMapper::responseToDto);
    }
}
