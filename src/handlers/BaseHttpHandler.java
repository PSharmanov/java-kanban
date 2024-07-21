package handlers;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import models.Epic;
import models.SubTask;
import models.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BaseHttpHandler {

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
        String response = "Not Found - запрашиваемый ресурс отсутствует на сервере";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, 0);
        h.getResponseBody().write(resp);
        h.close();
    }

    //ответа в случае, если при создании или обновлении задача пересекается с уже существующими
    protected void sendHasInteractions(HttpExchange h) throws IOException {
        String response = "Not Acceptable - создание/обновление невозможно, ресурса не существует)";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
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

class TasksListTypeToken extends TypeToken<List<Task>> {

}

class SubTasksListTypeToken extends TypeToken<List<SubTask>> {

}

class EpicListTypeToken extends TypeToken<List<Epic>> {

}
