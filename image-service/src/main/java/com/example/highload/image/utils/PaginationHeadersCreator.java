package com.example.highload.image.utils;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;


@Component("paginationHeadersCreator")
public class PaginationHeadersCreator {

    public <T> HttpHeaders endlessSwipeHeadersCreate(Page<T> entityList) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("app-current-page-num", String.valueOf(entityList.getNumber()));
        responseHeaders.set("app-page-has-next", String.valueOf(entityList.hasNext()));
        return responseHeaders;
    }

    public <T> HttpHeaders pageWithTotalElementsHeadersCreate(Page<T> entityList) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("app-total-page-num", String.valueOf(entityList.getTotalPages()));
        responseHeaders.set("app-total-items-num", String.valueOf(entityList.getTotalElements()));
        responseHeaders.set("app-current-page-num", String.valueOf(entityList.getNumber()));
        responseHeaders.set("app-current-items-num", String.valueOf(entityList.getNumberOfElements()));
        return responseHeaders;
    }
}
