package com.dev.handler;


import java.net.Socket;
import java.util.Map;

public interface RequestHandler {
    void handlePost(Socket clientSocket, Map<String, Object> request);
    void handleGet(Socket clientSocket);
    void handlePut(Socket clientSocket,Map<String, Object> request);
    void handleDelete(Socket clientSocket,Map<String, Object> request);
}

