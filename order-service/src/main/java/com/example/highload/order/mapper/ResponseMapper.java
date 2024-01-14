package com.example.highload.order.mapper;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.model.network.ResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResponseMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userName")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "isApproved", target = "approved")
    ResponseDto responseToDto(Response response);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "user.login", source = "userName")
    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "isApproved", source = "approved")
    Response responseDtoToResponse(ResponseDto responseDto);

    List<ResponseDto> responseListToResponseDtoList(List<Response> responses);
    List<Response> responseDtoListToResponseList(List<ResponseDto> responsesDto);
}
