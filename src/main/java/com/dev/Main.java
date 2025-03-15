package com.dev;


import com.dev.database.Database;
import com.dev.database.GeneralDatabase;
import com.dev.database.SmartDatabase;
import com.dev.datasource.DataSource;
import com.dev.datasource.HikariCPDataSource;
import com.dev.handler.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {

    DataSource dataSource;
    RequestService requestService;
    RequestHandler handler;

    Main(){

        String url = "jdbc:mariadb://localhost:3306/spring";
        String user = "root"; // 사용자 이름 (기본은 root)
        String password = "root"; // 비밀번호 (설정한 비밀번호)
        HikariCPDataSource dataSource = new HikariCPDataSource(url, user, password);
        Database db = new SmartDatabase(dataSource);
        this.requestService = new RequestService(db);
        this.handler = new AsyncRequestHandler(requestService);
    }


    public static void main(String[] args) {

        Main main = new Main();


        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("HTTP Server is listening on port 8080...");
            Socket clientSocket;
            while (true) {
                   clientSocket = serverSocket.accept();

                    // 클라이언트 요청 처리
                    main.handleRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket clientSocket) {
            BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String requestLine = in.readLine();
            if (requestLine == null) {
                clientSocket.close();
                return;
            }


            // POST 요청인 경우, content-length 헤더를 읽어 해당 크기만큼 body를 읽음
            StringBuilder requestBody = parseRequestBody(in);

            String response = "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 11\r\n" +
                    "\r\n" +
                    "Bad Request";


            Map<String, Object> jsonMap = extractJsonMap(requestBody);
            // handler 호출
            requestLine = requestLine.split(" ")[0];
            switch (requestLine){
                case "GET":
                    handler.handleGet(clientSocket);
                    break;
                case "POST":
                    handler.handlePost(clientSocket,jsonMap);
                    break;
                case "PUT":
                    handler.handlePut(clientSocket,jsonMap);
                    break;
                case "DELETE":
                    handler.handleDelete(clientSocket,jsonMap);
                    break;
            }

//        System.out.println("Socket "+ clientSocket.hashCode() + " " + clientSocket.isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String,Object> extractJsonMap(StringBuilder requestBody) {
        Map<String, Object> jsonMap;
//        System.out.println("parse start");
        try {
            // Jackson ObjectMapper를 사용하여 JSON을 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 데이터를 Map으로 변환
            jsonMap = objectMapper.readValue(requestBody.toString(), Map.class);

        } catch (Exception e) {
            return new HashMap<String,Object>();
        }

//        System.out.println("parse end");
        return jsonMap;
    }

    private StringBuilder parseRequestBody(BufferedReader in) throws IOException {
        String line;
        StringBuilder requestBody = new StringBuilder();
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {

                int contentLength = Integer.parseInt(line.split(":")[1].trim());
                char[] bodyBuffer = new char[contentLength];
                in.read(bodyBuffer, 0, contentLength);
                requestBody.append(bodyBuffer);
                break;
            }
        }
//        System.out.println("parse end : " + requestBody.toString());
        return requestBody;
    }


}