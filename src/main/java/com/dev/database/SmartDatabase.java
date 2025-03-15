package com.dev.database;

import com.dev.datasource.DataSource;
import com.dev.handler.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class SmartDatabase extends GeneralDatabase {
    // ConcurrentHashMap을 사용해 캐시된 값을 관리
    private final ConcurrentHashMap<String, Integer> countCache = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SmartDatabase.class);
    public SmartDatabase(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Integer getTotalCount() {
        // 캐시에서 값을 먼저 조회
        Integer count = countCache.get("totalCount");

        // 값이 없으면 동기화하여 한 스레드만 값을 계산하도록 처리
        if (count == null) {
            synchronized (this) {
                // 다른 스레드가 값 계산 중일 수 있으므로 다시 한 번 확인
                count = countCache.get("totalCount");
                if (count == null) {
                    // 계산 로직 (한 번만 호출됨)
                    logger.info("DB Request Fired");
                    count = super.getTotalCount();  // 데이터베이스에서 총 개수를 가져옴
                    countCache.put("totalCount", count); // 캐시된 값에 저장
                }
            }
        }
        return count;
    }
}
