package com.dev.database;

import com.dev.datasource.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneralDatabase implements Database {

    // 아무런 처리 없이 단순하게 데이터를 삽입하는 클래스
    DataSource dataSource;

    public GeneralDatabase(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public String reserve(String accountNumber, String seatNumber) {

        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO test (user_id, seat_number) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, accountNumber);
                statement.setInt(2, Integer.parseInt(seatNumber));
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
//                    System.out.println("User ID and seat number successfully inserted.");
                    return "postSuccess";
                } else {
//                    System.out.println("Failed to insert data.");
                    return "postFailure";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "postError";
        }
    }



    @Override
    public List<Integer> getAvailableSeats() {
        Set<Integer> reservedSeats = new HashSet<>();
        String sql = "SELECT * FROM reservations";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                reservedSeats.add(rs.getInt("seat_number"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1~100까지의 좌석 중 예약되지 않은 좌석 찾기
        List<Integer> availableSeats = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            if (!reservedSeats.contains(i)) {
                availableSeats.add(i);
            }
        }

        return availableSeats;
    }

    @Override
    public Integer getTotalCount() {
        String sql = "SELECT COUNT(id) FROM test";
        int rowCount = -1;

        try (Connection conn = dataSource.getConnection()) {
          conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    rowCount = rs.getInt(1);  // 첫 번째 컬럼 (COUNT(*) 결과)
                    // System.out.println("Row count: " + rowCount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowCount;
    }


}
