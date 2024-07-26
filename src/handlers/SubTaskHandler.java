package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import interfaces.TaskManager;
import managers.Manager;
import models.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Manager.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            String requestMethod = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + requestMethod + " /subtask запроса от клиента.");

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

        } catch (NotFoundException exception) {
            sendHasInteractions(httpExchange);
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            String response = exception.getLocalizedMessage();
            sendInternalServerError(httpExchange, response);
        }
    }

    //обработчик GET
    private void handleGetRequest(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();

        //обработчик GET /api/subtasks
        if (Pattern.matches("^/api/subtasks$", path)) {

            String response = gson.toJson(taskManager.getListAllSubTasks());

            sendText(httpExchange, response);

            return;
        }

        //обработчик GET /api/subtasks/{id}
        if (Pattern.matches("^/api/subtasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/subtasks/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getSubTaskById(id) != null) {

                String response = gson.toJson(taskManager.getSubTaskById(id));

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

        //обработчик POST /api/subtasks
        if (Pattern.matches("^/api/subtasks$", path)) {

            InputStream inputStream = httpExchange.getRequestBody();

            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            SubTask subTask = gson.fromJson(body, SubTask.class);

            try {

                taskManager.createSubTask(subTask);

            } catch (NotFoundException exception) {

                sendNotFound(httpExchange);

            } catch (RuntimeException exception) {

                sendHasInteractions(httpExchange);
            }


            System.out.println("Запрос обработан, задача создана :" + subTask);

            httpExchange.sendResponseHeaders(201, 0);

            return;

        }

        //обработчик POST /api/subtasks/{id}
        if (Pattern.matches("^/api/subtasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/subtasks/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getSubTaskById(id) != null) {

                InputStream inputStream = httpExchange.getRequestBody();

                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                SubTask task = gson.fromJson(body, SubTask.class);

                taskManager.updateSubTask(task);

                System.out.println("Запрос обработан, задача обновлена :" + task);

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

        //обработчик DELETE /api/tasks
        if (Pattern.matches("^/api/subtasks$", path)) {

            taskManager.deletingAllSubTasks();

            System.out.println("Запрос обработан, все задачи удалены");

            httpExchange.sendResponseHeaders(200, 0);

            return;

        }

        //обработчик DELETE /api/subtask/{id}
        if (Pattern.matches("^/api/subtasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/subtasks/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getSubTaskById(id) != null) {

                taskManager.deletingSubTaskById(id);

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
