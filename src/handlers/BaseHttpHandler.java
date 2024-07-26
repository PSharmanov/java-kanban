package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    final String NOT_FOUND = "Not Found - запрашиваемый ресурс отсутствует на сервере";
    final String NOT_ACCEPTABLE = "Not Acceptable - создание/обновление невозможно, ресурса не существует)";

    //ответ в случае успеха
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
        System.out.println("Запрос выполнен!");
    }

    //ответ в случае ошибки обработки запроса
    protected void sendInternalServerError(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
        System.out.println("Запрос не выполнен! Произошла ошибка при обработке запроса!");
    }

    //ответ в случае, если объект не был найден
    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = NOT_FOUND.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, 0);
        h.getResponseBody().write(resp);
        h.close();
    }

    //ответа в случае, если при создании или обновлении задача пересекается с уже существующими
    protected void sendHasInteractions(HttpExchange h) throws IOException {
        byte[] resp = NOT_ACCEPTABLE.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

}

