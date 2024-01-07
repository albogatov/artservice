package com.example.highload.order.mapper;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.model.network.ResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResponseMapper {

    ResponseDto responseToDto(Response response);

    Response responseDtoToResponse(ResponseDto responseDto);

    List<ResponseDto> responseListToResponseDtoList(List<Response> responses);
    List<Response> responseDtoListToResponseList(List<ResponseDto> responsesDto);
}
