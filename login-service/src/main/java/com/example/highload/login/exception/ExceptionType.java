package com.example.highload.login.exception;

public enum ExceptionType {
    DATA(1L, "Ошибка в работе сервиса уровня сущности"),
    BUSINESS_MANAGER(2L, "Ошибка в работе сервиса менеджера"),
    BUSINESS_ADMIN(3L, "Ошибка в работе сервиса администратора"),
    BUSINESS_EMPLOYEE(4L, "Ошибка в работе сервиса работника"),
    BUSINESS_CLIENT(5L, "Ошибка в работе сервиса пользователя");

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
