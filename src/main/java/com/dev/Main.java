package com.dev;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("HTTP Server is listening on port 8080...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    // 클라이언트 요청 처리
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
//
            // HTTP 요청 읽기
            String requestLine = in.readLine();
            if (requestLine == null) return;
            System.out.println("Request: ," + requestLine + "SocketNumber : " + clientSocket.hashCode() );  // 요청 라인 출력 (예: GET / HTTP/1.1)
            // 요청 헤더 읽기 (빈 줄이 올 때까지)
            String line;
            System.out.println("requestLine :" +requestLine);
            // 요청이 POST인 경우, body에 JSON이 포함되어 있을 수 있음
            StringBuilder requestBody = new StringBuilder();
            if (requestLine.startsWith("POST")) {
                // POST 요청인 경우, content-length 헤더를 읽어 해당 크기만큼 body를 읽음
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    if (line.startsWith("Content-Length:")) {
                        int contentLength = Integer.parseInt(line.split(":")[1].trim());
                        char[] bodyBuffer = new char[contentLength];
                        in.read(bodyBuffer, 0, contentLength);
                        System.out.println("body Buffer :" + bodyBuffer.toString());
                        requestBody.append(bodyBuffer);
                        break;
                    }
                }
                System.out.println("Request Body: " + requestBody.toString());  // body 출력 (JSON 데이터)
            }

            // JSON 파싱 (예: POST 요청에서 JSON 받기)
            String response;
            System.out.println(requestLine);
            if (requestLine.startsWith("POST")) {
                try {
                    // Jackson ObjectMapper를 사용하여 JSON을 파싱
                    ObjectMapper objectMapper = new ObjectMapper();
                    // JSON 데이터를 Map으로 변환
                    Map<String, Object> jsonMap = objectMapper.readValue(requestBody.toString(), Map.class);
                    System.out.println("Parsed JSON: " + jsonMap);

                    // "action"이라는 키를 이용해 동작을 구분
                    String action = (String) jsonMap.get("action");
                    if ("delete".equals(action)) {
                        // DELETE 동작 처리
                        response = handleDelete(jsonMap);
                    } else if ("patch".equals(action)) {
                        // PATCH 동작 처리
                        response = handlePatch(jsonMap);
                    } else {
                        response = "HTTP/1.1 400 Bad Request\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "Content-Length: 16\r\n" +
                                "\r\n" +
                                "Invalid action";
                    }

                } catch (Exception e) {
                    response = "HTTP/1.1 400 Bad Request\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: 16\r\n" +
                            "\r\n" +
                            "Invalid JSON";
                }
            } else {
                response = "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: 16\r\n" +
                        "\r\n" +
                        "Bad Request";
            }

            out.println(response);  // 응답 전송

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // DELETE 동작 처리
    private static String handleDelete(Map<String, Object> jsonMap) {
        String itemId = (String) jsonMap.get("id");
        // 실제 DB나 자원에서 해당 항목을 삭제하는 코드 (여기서는 단순히 출력)
        System.out.println("Deleting item with ID: " + itemId);
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: 17\r\n" +
                "\r\n" +
                "Item deleted successfully";
    }

    // PATCH 동작 처리
    private static String handlePatch(Map<String, Object> jsonMap) {
        String itemId = (String) jsonMap.get("id");
        String newValue = (String) jsonMap.get("value");
        // 실제 DB나 자원에서 해당 항목을 수정하는 코드 (여기서는 단순히 출력)
        System.out.println("Patching item with ID: " + itemId + " to value: " + newValue);
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: 22\r\n" +
                "\r\n" +
                "Item patched successfully";
    }
}