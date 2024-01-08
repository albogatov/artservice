package com.example.highload.order.controller;

import com.example.highload.order.mapper.ResponseMapper;
import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.ResponseDto;
import com.example.highload.order.services.ResponseService;
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

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/order/response")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;
    private final ResponseMapper responseMapper;
    private final PaginationHeadersCreator paginationHeadersCreator;

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody ResponseDto data){
        if(responseService.saveResponse(data) != null)
            return ResponseEntity.ok("Response added");
        else return ResponseEntity.badRequest().body("Couldn't save response, check data");
    }

    @GetMapping("/all/order/{orderId}/{page}")
    @PreAuthorize("hasAnyAuthority('ARTIST', 'CLIENT')")
    public ResponseEntity<?> getAllByOrder(@PathVariable int orderId, @PathVariable int page){
        Pageable pageable = PageRequest.of(page, 50);
        Page<Response> entityList = responseService.findAllForOrder(orderId, pageable);
        List<ResponseDto> dtoList = responseMapper.responseListToResponseDtoList(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.endlessSwipeHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @GetMapping("/all/user/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('ARTIST')")
    public ResponseEntity<?> getAllByUser(@PathVariable int userId, @PathVariable int page){
        Pageable pageable = PageRequest.of(page, 50);
        Page<Response> entityList = responseService.findAllForUser(userId, pageable);
        List<ResponseDto> dtoList = responseMapper.responseListToResponseDtoList(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @GetMapping("/single/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getById(@PathVariable int id){
        Response entity = responseService.findById(id);
        return ResponseEntity.ok(responseMapper.responseToDto(entity));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(){
        return ResponseEntity.badRequest().body("Request body validation failed!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions(){
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

}
