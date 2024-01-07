package com.example.highload.order.mapper;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.model.network.ResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResponseMapper {

    ResponseDto responseToDto(Response response);

    Response responseDtoToResponse(ResponseDto responseDto);
}
