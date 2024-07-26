package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;
import managers.Manager;
import models.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Manager.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String requestMethod = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + requestMethod + " /epic запроса от клиента.");

            switch (requestMethod) {
                case "GET":
                    handleGetRequest(httpExchange);
                    break;
                case "POST":
                    handlePostRequest(httpExchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(httpExchange);
                    break;
                default:
                    System.out.println("Сервер не поддерживает метод, указанный в запросе: " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
            }

        } catch (Exception exception) {
            String response = exception.getLocalizedMessage();
            sendInternalServerError(httpExchange, response);
        }

    }

    //обработчик GET
    private void handleGetRequest(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();

        //обработчик GET /api/epics/{id}/subtasks
        if (Pattern.matches("^/api/epics/\\d+/subtasks$", path)) {

            String pathId = path.replaceFirst("/api/epics/", "").replaceFirst("/subtasks", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getEpicById(id) != null) {

                String response = gson.toJson(taskManager.getListAllSubTaskByEpicId(id));

                sendText(httpExchange, response);

                return;

            } else {

                System.out.println("Получен некорректный id = " + id);

                sendNotFound(httpExchange);

                return;

            }

        }

        //обработчик GET /api/epics
        if (Pattern.matches("^/api/epics$", path)) {

            String response = gson.toJson(taskManager.getListAllEpic());

            sendText(httpExchange, response);

            return;

        }

        //обработчик GET /api/epics/{id}
        if (Pattern.matches("^/api/epics/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/epics/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getEpicById(id) != null) {

                String response = gson.toJson(taskManager.getEpicById(id));

                sendText(httpExchange, response);

                return;

            } else {

                System.out.println("Получен некорректный id = " + id);

                sendNotFound(httpExchange);

                return;

            }
        }

        System.out.println("Запрашиваемый ресурс на сервере отсутствует " + httpExchange.getRequestURI());

        sendNotFound(httpExchange);

    }

    //обработчик POST
    private void handlePostRequest(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();

        //обработчик POST /api/epics
        if (Pattern.matches("^/api/epics$", path)) {

            InputStream inputStream = httpExchange.getRequestBody();

            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Epic epic = gson.fromJson(body, Epic.class);

            taskManager.createEpic(epic);

            System.out.println("Запрос обработан, задача создана :" + epic);

            httpExchange.sendResponseHeaders(201, 0);

            return;

        }

        //обработчик POST /api/epics/{id}
        if (Pattern.matches("^/api/epics/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/epics/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getEpicById(id) != null) {

                InputStream inputStream = httpExchange.getRequestBody();

                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                Epic epic = gson.fromJson(body, Epic.class);

                taskManager.updateEpic(epic);

                System.out.println("Запрос обработан, задача обновлена :" + epic);

                httpExchange.sendResponseHeaders(201, 0);

                return;

            } else {

                System.out.println("Получен некорректный id = " + id);

                sendNotFound(httpExchange);

                return;

            }
        }

        System.out.println("Запрашиваемый ресурс на сервере отсутствует " + httpExchange.getRequestURI());

        sendNotFound(httpExchange);

    }

    //обработчик DELETE
    private void handleDeleteRequest(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();

        //обработчик DELETE /api/epics
        if (Pattern.matches("^/api/epics$", path)) {

            taskManager.deletingAllEpics();

            System.out.println("Запрос обработан, все задачи удалены");

            httpExchange.sendResponseHeaders(200, 0);

            return;

        }

        //обработчик DELETE /api/task/{id}
        if (Pattern.matches("^/api/epics/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/epics/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getEpicById(id) != null) {

                taskManager.deletingEpicById(id);

                System.out.println("Удалили задачу id = " + id);

                httpExchange.sendResponseHeaders(200, 0);

                return;

            } else {

                System.out.println("Получен некорректный id = " + id);

                sendNotFound(httpExchange);

                return;

            }

        }

        System.out.println("Запрашиваемый ресурс на сервере отсутствует " + httpExchange.getRequestURI());

        sendNotFound(httpExchange);
    }

}
