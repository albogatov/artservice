package com.example.highload.order.mapper;

import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.inner.Tag;
import com.example.highload.order.model.network.ResponseDto;
import com.example.highload.order.model.network.TagDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto tagToDto(Tag tag);

    Tag tagDtoToTag(TagDto tagDto);
}
