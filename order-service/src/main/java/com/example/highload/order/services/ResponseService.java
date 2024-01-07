package com.example.highload.order.services;

import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.ResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResponseService {

    Response saveResponse(ResponseDto responseDto);

    Page<Response> findAllForOrder(int orderId, Pageable pageable);

    Page<Response> findAllForUser(int userId, Pageable pageable);

    Response findById(int id);
}
