package com.dev.handler;
import java.util.Map;

public class SyncRequestHandler implements RequestHandler {

    private final RequestService requestService;

    public SyncRequestHandler(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public String handlePost(Map<String, Object> request) {
        return buildHttpResponse(requestService.handlePost(request));
    }

    @Override
    public String handleGet() {
        return buildHttpResponse(requestService.handleGet());
    }

    @Override
    public String handlePut(Map<String, Object> request) {
        return buildHttpResponse(requestService.handlePut(request));
    }

    @Override
    public String handleDelete(Map<String, Object> request) {
        return buildHttpResponse(requestService.handleDelete(request));
    }

    private String buildHttpResponse(String responseBody) {
        int contentLength = responseBody.getBytes().length;
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "\r\n" +
                responseBody;
    }
}
