package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import interfaces.TaskManager;
import managers.Manager;
import models.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Manager.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestMethod = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + requestMethod + " /task запроса от клиента.");

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
        } finally {
            httpExchange.close();
        }

    }


    //обработчик GET
    private void handleGetRequest(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();

        //обработчик GET /api/tasks
        if (Pattern.matches("^/api/tasks$", path)) {

            String response = gson.toJson(taskManager.getListAllTasks());

            sendText(httpExchange, response);

            return;

        }

        //обработчик GET /api/tasks/{id}
        if (Pattern.matches("^/api/tasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/tasks/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getTaskById(id) != null) {

                String response = gson.toJson(taskManager.getTaskById(id));

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
    private void handlePostRequest(HttpExchange httpExchange) throws IOException, NotFoundException {

        String path = httpExchange.getRequestURI().getPath();

        //обработчик POST /api/tasks
        if (Pattern.matches("^/api/tasks$", path)) {

            InputStream inputStream = httpExchange.getRequestBody();

            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Task task = gson.fromJson(body, Task.class);

            taskManager.createTask(task);

            System.out.println("Запрос обработан, задача создана :" + task);

            httpExchange.sendResponseHeaders(201, 0);

            return;

        }

        //обработчик POST /api/tasks/{id}
        if (Pattern.matches("^/api/tasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/tasks/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getTaskById(id) != null) {

                InputStream inputStream = httpExchange.getRequestBody();

                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                Task task = gson.fromJson(body, Task.class);

                taskManager.updateTask(task);

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
        if (Pattern.matches("^/api/tasks$", path)) {

            taskManager.deletingAllTasks();

            System.out.println("Запрос обработан, все задачи удалены");

            httpExchange.sendResponseHeaders(200, 0);

            return;

        }

        //обработчик DELETE /api/task/{id}
        if (Pattern.matches("^/api/tasks/\\d+$", path)) {

            String pathId = path.replaceFirst("/api/tasks/", "");

            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getTaskById(id) != null) {

                taskManager.deletingTaskById(id);

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
