package com.dev.handler;

import com.dev.datasource.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class RequestService {

    private final DataSource dataSource;

    public RequestService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String handlePost(Map<String, Object> request) {
        // 비즈니스 로직 (예: 데이터베이스 삽입)
        String userId = "3";  // 예시: 사용자 ID
        String seatNumber = "10";  // 예시: 좌석 번호
        System.out.println("시작");
        // 데이터베이스에 저장
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO test (user_id, seat_number) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userId);
                statement.setString(2, seatNumber);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User ID and seat number successfully inserted.");
                    return "postSuccess";
                } else {
                    System.out.println("Failed to insert data.");
                    return "postFailure";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "postError";
        }
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

