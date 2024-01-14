package com.example.highload.order.services;

import com.example.highload.order.model.inner.Tag;
import com.example.highload.order.model.network.TagDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TagService {

    Mono<TagDto> saveTag(TagDto tagDto);

    Flux<TagDto> findAll();

    Mono<TagDto> findById(Integer tagIdToAdd);
}
