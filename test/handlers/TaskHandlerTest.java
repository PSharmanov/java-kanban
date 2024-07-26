package handlers;

import adapters.TasksListTypeToken;
import com.google.gson.Gson;
import enums.Status;
import exceptions.NotFoundException;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    void setUp() throws IOException {

        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();

        manager.deletingAllTasks();
        manager.deletingAllSubTasks();
        manager.deletingAllEpics();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        Task task = new Task("newTest", "Testing task 2", 1,
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("newTest", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldCode406CreateTaskIntersectsWithExisting() throws IOException, InterruptedException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        Task task = new Task("newTest", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        assertNotNull(manager.getListAllTasks(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllTasks().size(), "Некорректное количество задач");

    }

    @Test
    public void shouldCode404UpdateTaskIncorrectId() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        Task task = new Task("newTest", "Testing task 2", 1,
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/11");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertNotNull(manager.getListAllTasks(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllTasks().size(), "Некорректное количество задач");

    }

    @Test
    public void shouldCode404UpdateTaskResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        Task task = new Task("newTest", "Testing task 2", 1,
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/taskss");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertNotNull(manager.getListAllTasks(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllTasks().size(), "Некорректное количество задач");

    }

    @Test
    public void shouldCode406UpdateTaskIntersectsWithExisting() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        manager.createTask(new Task("Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10)));
        Task task = new Task("newTest", "Testing task 2", 1,
                Status.NEW, Duration.ofMinutes(20), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertNotNull(manager.getListAllTasks(), "Задачи не возвращаются");
        assertEquals(2, manager.getListAllTasks().size(), "Некорректное количество задач");
        assertEquals("Test 2", manager.getTaskById(1).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testUDeleteTaskById() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testUDeleteTaskIncorrectId() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/11");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testUDeleteTaskResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/1as");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testUDeleteAllTasks() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        manager.createTask(new Task("Test 3", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = gson.fromJson(response.body(), new TasksListTypeToken().getType());
        ;

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Test 3", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task returnedTask = gson.fromJson(response.body(), Task.class);
        Task returnedTask2 = manager.getTaskById(1);

        assertEquals(returnedTask2, returnedTask, "Возвращаемый JSON не равен переданному объекту");
    }

    @Test
    public void shouldStatusCode404GetIncorrectId() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ресурс отсутствует");
    }

    @Test
    public void shouldStatusCode404GetResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        manager.createTask(new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ресурс отсутствует");
    }

    @Test
    public void testServerDoesNotSupportMethod() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode(), "Сервер не поддерживает метод");
    }

}