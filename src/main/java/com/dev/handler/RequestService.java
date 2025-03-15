package com.dev.handler;

import com.dev.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final Database database;

    public RequestService(Database database) {
        this.database = database;
    }

    public String handlePost(Map<String, Object> request) {
        logger.info("POST 요청 시작");

        // 데이터베이스에 저장
        database.reserve("1", "1");

        logger.info("POST 처리 완료");

        return "post success";
    }

    public String handleGet() {
        logger.info("GET 요청 처리 중");
        return "getSuccess";
    }

    public String handlePut(Map<String, Object> request) {
        logger.info("PUT 요청 시작");

        long startTime = System.currentTimeMillis();
        Integer result = database.getTotalCount();
        long endTime = System.currentTimeMillis();

        logger.info("getTotalCount() 수행 시간: {}ms", (endTime - startTime));

        return "putSuccess"+ result;
    }

    public String handleDelete(Map<String, Object> request) {
        logger.info("DELETE 요청 처리 중: {}", request);
        return "deleteSuccess";
    }
}
