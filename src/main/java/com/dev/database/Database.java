package com.dev.database;

import java.util.List;

public interface Database {

    String reserve(String accountNumber, String seatNumber);
    List<Integer> getAvailableSeats();
    Integer getTotalCount();
}
