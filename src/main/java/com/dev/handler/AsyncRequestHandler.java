package com.dev.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncRequestHandler implements RequestHandler {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(30);
    private final RequestService requestService;

    public AsyncRequestHandler(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public void handlePost(Socket clientSocket, Map<String, Object> request) {
//        System.out.println(clientSocket.hashCode() + " " + clientSocket.isClosed());
        CompletableFuture.supplyAsync(() -> requestService.handlePost(request), threadPool)
                .thenAccept(response -> {
                    sendResponse(clientSocket, response);
                    try {
                        clientSocket.close(); // 비동기 작업 후에 소켓을 닫는다
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void handleGet(Socket clientSocket) {
        CompletableFuture.supplyAsync(requestService::handleGet, threadPool)
                .thenAccept(response -> sendResponse(clientSocket, response));
    }

    @Override
    public void handlePut(Socket clientSocket, Map<String, Object> request) {
        CompletableFuture.supplyAsync(() -> requestService.handlePut(request), threadPool)
                .thenAccept(response -> sendResponse(clientSocket, response));
    }

    @Override
    public void handleDelete(Socket clientSocket, Map<String, Object> request) {
        CompletableFuture.supplyAsync(() -> requestService.handleDelete(request), threadPool)
                .thenAccept(response -> sendResponse(clientSocket, response));
    }

    private void sendResponse(Socket clientSocket, String responseBody) {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream, true);
            int contentLength = responseBody.getBytes().length;
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + contentLength + "\r\n" +
                    "\r\n" +
                    responseBody;

            out.write(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
