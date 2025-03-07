package com.dev.handler;

import java.util.concurrent.*;
import java.util.Map;

public class AsyncRequestHandler implements RequestHandler {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(30);
    private final RequestService requestService;

    public AsyncRequestHandler(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public String handlePost(Map<String, Object> request) {
        Future<String> future = threadPool.submit(() -> requestService.handlePost(request));
        return buildHttpResponse(future);
    }

    @Override
    public String handleGet() {
        Future<String> future = threadPool.submit(requestService::handleGet);
        return buildHttpResponse(future);
    }

    @Override
    public String handlePut(Map<String, Object> request) {
        Future<String> future = threadPool.submit(() -> requestService.handlePut(request));
        return buildHttpResponse(future);
    }

    @Override
    public String handleDelete(Map<String, Object> request) {
        Future<String> future = threadPool.submit(() -> requestService.handleDelete(request));
        return buildHttpResponse(future);
    }

    private String buildHttpResponse(Future<String> future) {
        try {
            String responseBody = future.get(); // 결과 값을 가져옴
            int contentLength = responseBody.getBytes().length;
            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + contentLength + "\r\n" +
                    "\r\n" +
                    responseBody;
        } catch (Exception e) {
            return "HTTP/1.1 500 Internal Server Error\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 21\r\n" +
                    "\r\n" +
                    "Internal Server Error";
        }
    }
}
