package handlers;

import adapters.TasksListTypeToken;
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

class PrioritizedHandlerTest {

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
        System.out.println(manager.getEpicById(1));

        //создаем подзадачу
        SubTask subTask = new SubTask("testSubtask", "DescriptionSubtask1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(1));
        taskJson = gson.toJson(subTask);
        url = URI.create("http://localhost:8080/api/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(manager.getSubTaskById(2));

        //Создаем задачу
        Task task = new Task("testTask", "DescripTestTask",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().minusMinutes(10));
        taskJson = gson.toJson(task);
        url = URI.create("http://localhost:8080/api/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(manager.getTaskById(3));
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException, NotFoundException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = gson.fromJson(response.body(), new TasksListTypeToken().getType());

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("testTask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals("testSubtask", tasksFromManager.get(1).getName(), "Некорректное имя задачи");

    }

    @Test
    public void shouldCode405ServerDoesNotSupportMethod() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());

    }

    @Test
    public void shouldCode404GetResourceIsMissing() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/prioritizedS");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

    }

}