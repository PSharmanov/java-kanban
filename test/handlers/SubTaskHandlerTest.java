package handlers;

import adapters.SubTasksListTypeToken;
import com.google.gson.Gson;
import enums.Status;
import exceptions.NotFoundException;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import models.Epic;
import models.SubTask;
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

class SubTaskHandlerTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {

        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();

        manager.deletingAllTasks();
        manager.deletingAllSubTasks();
        manager.deletingAllEpics();
        taskServer.start();

        //создаем эпик
        Epic epic1 = new Epic("testEpic", "DescriptionEpic1");
        String taskJson = gson.toJson(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //создаем подзадачу
        SubTask subTask = new SubTask("testSubtask", "DescriptionSubtask1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(1));
        taskJson = gson.toJson(subTask);
        URI url2 = URI.create("http://localhost:8080/api/subtasks");
        request = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testCreateSubTask() throws IOException, InterruptedException {
        SubTask task = new SubTask("newSubtask", "DescriptionSubtask1",
                Status.NEW, null, null, manager.getEpicById(1));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getListAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("newSubtask", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException, NotFoundException {
        SubTask subTask = new SubTask("newSubtask", "DescriptionSubtask1", 2,
                Status.IN_PROGRESS, null, null, manager.getEpicById(1));
        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getListAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("newSubtask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldCode406CreateSubTaskIntersectsWithExisting() throws IOException, InterruptedException, NotFoundException {
        SubTask subTask = new SubTask("newSubtask", "DescriptionSubtask1",
                Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(1));
        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertNotNull(manager.getListAllSubTasks(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllSubTasks().size(), "Некорректное количество задач");
    }

    @Test
    public void shouldCode404UpdateSubTaskIncorrectId() throws IOException, InterruptedException, NotFoundException {
        SubTask subTask = new SubTask("newSubtask", "DescriptionSubtask1", 11,
                Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(1));
        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/11");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertNotNull(manager.getListAllTasks(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllSubTasks().size(), "Некорректное количество задач");
    }

    @Test
    public void shouldCode404UpdateSubTaskResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        SubTask subTask = new SubTask("newSubtask", "DescriptionSubtask1", 1,
                Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(1));
        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtaskss");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertNotNull(manager.getListAllTasks(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllSubTasks().size(), "Некорректное количество задач");
    }

    @Test
    public void shouldCode406UpdateSubTaskIntersectsWithExisting() throws IOException, InterruptedException, NotFoundException {
        SubTask subTask = new SubTask("newSubtask", "DescriptionSubtask1", 1,
                Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now().minusMinutes(3), manager.getEpicById(1));
        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertNotNull(manager.getListAllSubTasks(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllSubTasks().size(), "Некорректное количество задач");
        assertEquals("testSubtask", manager.getSubTaskById(2).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = manager.getListAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteSubTaskIncorrectId() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/11");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<SubTask> tasksFromManager = manager.getListAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTaskResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/1as");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<SubTask> tasksFromManager = manager.getListAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteAllSubTasks() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = manager.getListAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = gson.fromJson(response.body(), new SubTasksListTypeToken().getType());

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("testSubtask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task returnedTask = gson.fromJson(response.body(), SubTask.class);
        Task returnedTask2 = manager.getSubTaskById(2);

        assertEquals(returnedTask2, returnedTask, "Возвращаемый JSON не равен переданному объекту");
    }

    @Test
    public void shouldStatusCode404GetIncorrectId() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ресурс отсутствует");
    }

    @Test
    public void shouldStatusCode404GetResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ресурс отсутствует");
    }

    @Test
    public void testServerDoesNotSupportMethod() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("newSubtask", "DescriptionSubtask1", 2,
                Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), manager.getEpicById(1));
        String taskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode(), "Сервер не поддерживает метод");
    }

}