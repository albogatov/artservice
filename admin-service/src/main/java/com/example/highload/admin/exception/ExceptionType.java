package com.example.highload.admin.exception;

public enum ExceptionType {
    DATA(1L, "TODO");
    

    private final Long id;
    private final String name;

    ExceptionType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
