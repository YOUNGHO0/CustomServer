package com.dev.handler;

import java.util.concurrent.*;
import java.sql.*;
import java.util.Map;
import com.dev.datasource.DataSource;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRequestHandler {

    private final ExecutorService threadPool;
    private final DataSource dataSource;

    public JsonRequestHandler(ExecutorService threadPool, DataSource dataSource) {
        this.threadPool = threadPool;
        this.dataSource = dataSource;
    }

    // JSON을 처리하는 메서드
    public void handleRequest(String jsonRequest) {
        threadPool.execute(() -> {
            try {
                // JSON을 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> requestMap = objectMapper.readValue(jsonRequest, Map.class);
                String type = (String) requestMap.get("type");

                // type에 따라 다른 동작을 수행하는 람다
                Runnable task = getTaskForType(type);
                if (task != null) {
                    task.run();
                } else {
                    System.out.println("Invalid type: " + type);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // type에 맞는 동작을 반환하는 메서드
    private Runnable getTaskForType(String type) {
        // 동작을 람다로 정의하여 type에 따라 실행하도록 설정
        switch (type) {
            case "insert":
                return () -> insertData();
            case "update":
                return () -> updateData();
            case "delete":
                return () -> deleteData();
            default:
                return null;
        }
    }

    // 실제 DB 작업 수행하는 예시 메서드
    private void insertData() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO my_table (column1, column2) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "value1");
                stmt.setString(2, "value2");
                stmt.executeUpdate();
                System.out.println("Insert completed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateData() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE my_table SET column1 = ? WHERE column2 = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "newValue");
                stmt.setString(2, "oldValue");
                stmt.executeUpdate();
                System.out.println("Update completed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteData() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM my_table WHERE column2 = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "valueToDelete");
                stmt.executeUpdate();
                System.out.println("Delete completed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
