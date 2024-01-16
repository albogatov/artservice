package com.example.highload.order.services.impl;

import com.example.highload.order.mapper.TagMapper;
import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.inner.Tag;
import com.example.highload.order.model.network.TagDto;
import com.example.highload.order.services.OrderService;
import com.example.highload.order.services.TagService;
import com.example.highload.order.repos.OrderRepository;
import com.example.highload.order.repos.TagRepository;
import com.example.highload.order.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public Mono<TagDto> saveTag(TagDto tagDto) {
        return Mono.just(tagDto).map(t ->  {
            return tagRepository.save(tagMapper.tagDtoToTag(t));
        }).onErrorResume(t -> {
            return Mono.error(new IllegalArgumentException("Tag already exists"));
        }).map(tagMapper::tagToDto);
    }

    @Override
    public Flux<TagDto> findAll() {
        return Flux.fromIterable(tagRepository.findAll()).map(tagMapper::tagToDto);
    }

    @Override
    public Mono<TagDto> findById(Integer tagIdToAdd) {
        return Mono.just(tagRepository.findById(tagIdToAdd).orElseThrow()).map(tagMapper::tagToDto);
    }
}
