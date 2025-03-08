package com.dev.handler;

import com.dev.datasource.DataSource;

import java.util.Map;

public class RequestService {

    private final DataSource dataSource;

    public RequestService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String handlePost(Map<String, Object> request) {
        // 비즈니스 로직 (예: 데이터베이스 삽입)
        // 예시: 데이터 처리
        System.out.println("Handling POST request with data: " + request);
        return "postSuccess";
    }

    public String handleGet() {
        // 비즈니스 로직 (예: 데이터베이스 조회)
        System.out.println("Handling GET request with data: ");
        return "getSuccess";
    }

    public String handlePut(Map<String, Object> request) {
        // 비즈니스 로직 (예: 데이터 업데이트)
        System.out.println("Handling PUT request with data: " + request);
        return "putSuccess";
    }

    public String handleDelete(Map<String, Object> request) {
        // 비즈니스 로직 (예: 데이터 삭제)
        System.out.println("Handling DELETE request with data: " + request);
        return "deleteSuccess";
    }
}

