package com.dev.handler;


import java.util.Map;

public interface RequestHandler {
    String handlePost(Map<String, Object> request);
    String handleGet();
    String handlePut(Map<String, Object> request);
    String handleDelete(Map<String, Object> request);
}

