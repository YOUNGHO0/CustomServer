package com.dev.handler;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class SyncRequestHandler implements RequestHandler {

    private final RequestService requestService;

    public SyncRequestHandler(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public void handlePost(Socket clientSocket, Map<String, Object> request) {
        String responseBody = requestService.handlePost(request);
        sendResponse(clientSocket, responseBody);
    }

    @Override
    public void handleGet(Socket clientSocket) {
        String responseBody = requestService.handleGet();
        sendResponse(clientSocket, responseBody);
    }

    @Override
    public void handlePut(Socket clientSocket, Map<String, Object> request) {
        String responseBody = requestService.handlePut(request);
        sendResponse(clientSocket, responseBody);
    }

    @Override
    public void handleDelete(Socket clientSocket, Map<String, Object> request) {
        String responseBody = requestService.handleDelete(request);
        sendResponse(clientSocket, responseBody);
    }

    private void sendResponse(Socket clientSocket, String responseBody) {
        try (OutputStream outputStream = clientSocket.getOutputStream();
             PrintWriter out = new PrintWriter(outputStream, true)) {

            String response = buildHttpResponse(responseBody);
            out.write(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // 응답 후 소켓 닫기
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
