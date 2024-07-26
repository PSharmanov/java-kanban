package handlers;

import adapters.EpicListTypeToken;
import com.google.gson.Gson;
import enums.Status;
import exceptions.NotFoundException;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {

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
        Epic epic1 = new Epic("testEpic", "DescriptionTestEpic");
        String taskJson = gson.toJson(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "DescriptionEpic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getListAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic1", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }


    @Test
    void testUpdateEpicTask() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic1", "DescriptionEpic", 1);
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getListAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }


    @Test
    void shouldCode404UpdateEpicIncorrectId() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic1", "DescriptionEpic", 1);
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/11");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertNotNull(manager.getListAllEpic(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllEpic().size(), "Некорректное количество задач");
    }

    @Test
    void shouldCode404UpdateEpicResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic1", "DescriptionEpic", 1);
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epicss");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertNotNull(manager.getListAllEpic(), "Задачи не возвращаются");
        assertEquals(1, manager.getListAllEpic().size(), "Некорректное количество задач");
    }

    @Test
    void testDeleteEpicById() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getListAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testDeleteEpicIncorrectId() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/11");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        List<Epic> tasksFromManager = manager.getListAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testDeleteEpicResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1as");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<Epic> tasksFromManager = manager.getListAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testDeleteAllEpic() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getListAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testGetAllEpics() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        ;

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("testEpic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task returnedTask = gson.fromJson(response.body(), Epic.class);
        Task returnedTask2 = manager.getEpicById(1);

        assertEquals(returnedTask2, returnedTask, "Возвращаемый JSON не равен переданному объекту");
    }

    @Test
    void shouldListSubtasksByEpicId() throws NotFoundException, IOException, InterruptedException {
        manager.createSubTask(new SubTask("Subtask1", "Discript1", Status.NEW, manager.getEpicById(1)));
        manager.createSubTask(new SubTask("Subtask2", "Discript2", Status.DONE, manager.getEpicById(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldCode404GetListSubtasksByEpicIdIncorrectidId() throws NotFoundException, IOException, InterruptedException {
        manager.createSubTask(new SubTask("Subtask1", "Discript1", Status.NEW, manager.getEpicById(1)));
        manager.createSubTask(new SubTask("Subtask2", "Discript2", Status.DONE, manager.getEpicById(1)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/2/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldStatusCode404GetIncorrectId() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ресурс отсутствует");
    }

    @Test
    void shouldStatusCode404GetResourceIsMissing() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ресурс отсутствует");
    }

    @Test
    void testServerDoesNotSupportMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "DescriptionEpic", 1);
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode(), "Сервер не поддерживает метод");
    }

}