package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import managers.Manager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Manager.getGson();
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestMethod = httpExchange.getRequestMethod();
            System.out.println("Началась обработка " + requestMethod + " /history запроса от клиента.");

            switch (requestMethod) {

                case "GET":
                    String path = httpExchange.getRequestURI().getPath();

                    if (Pattern.matches("^/api/history$", path)) {

                        String response = gson.toJson(taskManager.getHistory());

                        sendText(httpExchange, response);

                    } else {

                        sendNotFound(httpExchange);

                    }
                    break;

                default:

                    System.out.println("Сервер не поддерживает метод, указанный в запросе: " + requestMethod);

                    httpExchange.sendResponseHeaders(405, 0);
            }


        } catch (Exception exception) {

            String response = exception.getLocalizedMessage();

            sendInternalServerError(httpExchange, response);


        } finally {

            httpExchange.close();
        }

    }
}
