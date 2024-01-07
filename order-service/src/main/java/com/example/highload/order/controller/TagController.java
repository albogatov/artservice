package com.example.highload.order.controller;

import com.example.highload.order.model.inner.Tag;
import com.example.highload.order.model.network.TagDto;
import com.example.highload.order.services.TagService;
import com.example.highload.utils.DataTransformer;
import com.example.highload.order.utils.PaginationHeadersCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.postgresql.util.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final PaginationHeadersCreator paginationHeadersCreator;
    private final DataTransformer dataTransformer;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> save(@Valid @RequestBody TagDto data) {
        if (tagService.saveTag(data) != null)
            return ResponseEntity.ok("Tag successfully created");
        else return ResponseEntity.badRequest().body("Couldn't save tag, check data");
    }

    @GetMapping("/all/{page}")
    public ResponseEntity<?> getAll(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Tag> entityList = tagService.findAll(pageable);
        List<TagDto> dtoList = dataTransformer.tagListToDto(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.endlessSwipeHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @PostMapping("/remove/{orderId}/{tagId}")
    public ResponseEntity<?> removeTagFromOrder(@PathVariable int orderId, @PathVariable int tagId) {
        tagService.removeTagFromOrder(tagId, orderId);
        return ResponseEntity.ok("Tag successfully removed from order");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions() {
        return ResponseEntity.badRequest().body("Request body validation failed!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handlePathExceptions() {
        return ResponseEntity.badRequest().body("Wrong pages or ids in path!");
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<?> handlePSQLException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


}
