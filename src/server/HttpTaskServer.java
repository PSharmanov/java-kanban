package server;

import handlers.*;
import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import interfaces.TaskManager;
import managers.Manager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private static Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Manager.getDefaultTaskManager());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Manager.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/tasks", new TaskHandler(taskManager));
        server.createContext("/api/subtasks", new SubTaskHandler(taskManager));
        server.createContext("/api/epics", new EpicHandler(taskManager));
        server.createContext("/api/history", new HistoryHandler(taskManager));
        server.createContext("/api/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен!");
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
            server.stop();
        } catch (IOException e) {
            System.out.println("Ошибка старта HTTP-сервера" + e.getMessage());
        }
    }

    public static Gson getGson() {
        return gson;
    }
}
