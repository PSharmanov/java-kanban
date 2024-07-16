import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();

        //Создаем две задачи
        Task task1 = new Task("Задача1", "Описание задачи 1", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.createTask(task1);

        Task task2 = new Task("Задача2", "Описание задачи 2", Status.IN_PROGRESS);
        task2.setStartTime(LocalDateTime.of(2024, 1, 2, 0, 0));
        taskManager.createTask(task2);

        //Создаем два Эпик
        Epic epic1 = new Epic("Эпик1", "Описание Эпика 1");
        taskManager.createEpic(epic1);

        Epic epic2 = new Epic("Эпик2", "Описание Эпика 2");
        taskManager.createEpic(epic2);

        //Создаем подзадачи для Эпиков
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1);
        subTask1.setStartTime(LocalDateTime.of(2024, 1, 3, 0, 0));
        subTask1.setDuration(Duration.ofMinutes(30));
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epic1);
        subTask2.setStartTime(LocalDateTime.of(2024, 1, 4, 0, 0));
        subTask2.setDuration(Duration.ofMinutes(30));
        taskManager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", Status.DONE, epic2);
        subTask3.setStartTime(LocalDateTime.of(2024, 1, 5, 0, 0));
        subTask3.setDuration(Duration.ofMinutes(30));
        taskManager.createSubTask(subTask3);

    }

    @Test
    void shouldCreateTask() {
        assertEquals(2, taskManager.getListAllTasks().size());

        Task task1 = new Task("Задача 1", "Описание задачи1", Status.NEW);
        taskManager.createTask(task1);

        assertEquals(3, taskManager.getListAllTasks().size());
    }

    @Test
    void shouldUpdateTask() {
        Task task1 = taskManager.getTaskById(1);
        assertEquals(Status.NEW, taskManager.getTaskById(1).getStatus());
        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        assertEquals(Status.DONE, taskManager.getTaskById(1).getStatus());

    }

    @Test
    void shouldNotUpdateTask() {
        String taskBefore = taskManager.getTaskById(1).toString();
        Task taskNew = new Task("Задача1", "Описание задачи 1", Status.NEW);
        taskNew.setId(7);
        taskManager.updateTask(taskNew);
        String taskAfter = taskManager.getTaskById(1).toString();
        assertEquals(taskBefore, taskAfter);

    }

    @Test
    void shouldGetListAllTasks() {
        assertEquals(2, taskManager.getListAllTasks().size());
    }

    @Test
    void shouldDeletingAllTasks() {
        assertEquals(2, taskManager.getListAllTasks().size());
        taskManager.deletingAllTasks();
        assertEquals(0, taskManager.getListAllTasks().size());
    }

    @Test
    void shouldDeletingTaskById() {
        assertNotNull(taskManager.getTaskById(1));
        taskManager.deletingTaskById(1);
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldNotCreateSubTaskUntilIsEpic() {
        assertEquals(2, taskManager.getListAllTasks().size());
        SubTask subTask1 = new SubTask("Подзадача1", "Описание подзадачи1", Status.NEW, null);
        taskManager.createSubTask(subTask1);
        assertEquals(2, taskManager.getListAllTasks().size());

    }

    @Test
    void shouldCreateSubTaskUntilIsEpic() {
        assertEquals(3, taskManager.getListAllSubTasks().size());
        Epic epic1 = new Epic("Эпик1", "Описание эпик1");
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача1", "Описание подзадачи1", Status.NEW, epic1);
        taskManager.createSubTask(subTask1);
        assertEquals(4, taskManager.getListAllSubTasks().size());

    }

    @Test
    void shouldNotUpdateSubTask() {
        int hasCodeBefore = taskManager.getSubTaskById(5).hashCode();
        SubTask subTaskNew = new SubTask(
                "Подзадача 1",
                "Описание подзадачи 1",
                Status.NEW, taskManager.getEpicById(5));
        subTaskNew.setId(10);
        taskManager.updateSubTask(subTaskNew);
        int hasCodeAfter = taskManager.getSubTaskById(5).hashCode();
        assertEquals(hasCodeBefore, hasCodeAfter);

    }

    @Test
    void shouldUpdateSubTask() {
        SubTask subTask1 = taskManager.getSubTaskById(5);
        assertEquals("Подзадача 1", subTask1.getName());
        Epic epic1 = taskManager.getEpicById(3);
        taskManager.createEpic(epic1);
        SubTask subTask2 = new SubTask("Подзадача Новая", "Описание подзадачи 1", Status.NEW, epic1);
        subTask2.setId(5);
        taskManager.updateSubTask(subTask2);
        assertEquals("Подзадача Новая", taskManager.getSubTaskById(5).getName());

    }

    @Test
    void shouldDeletingAllSubTasks() {
        assertEquals(3, taskManager.getListAllSubTasks().size());
        taskManager.deletingAllSubTasks();
        assertEquals(0, taskManager.getListAllSubTasks().size());

    }

    @Test
    void shouldDeletingSubTaskById() {
        assertNotNull(taskManager.getSubTaskById(5));
        taskManager.deletingSubTaskById(5);
        assertNull(taskManager.getSubTaskById(5));

    }

    @Test
    void shouldUpdateEpic() {
        assertEquals("Эпик1", taskManager.getEpicById(3).getName());
        Epic epicNew = new Epic("Эпик1 new", "Описание Эпика 1");
        epicNew.setId(3);
        taskManager.updateEpic(epicNew);
        assertEquals("Эпик1 new", taskManager.getEpicById(3).getName());

    }

    @Test
    void shouldGetListAllEpic() {
        assertNotNull(taskManager.getListAllEpic());
        assertEquals(2, taskManager.getListAllEpic().size());
    }

    @Test
    void shouldDeletingAllEpics() {
        assertNotNull(taskManager.getListAllEpic());
        taskManager.deletingAllEpics();
        assertEquals(0, taskManager.getListAllEpic().size());
    }

    @Test
    void shouldDeletingEpicById() {
        assertNotNull(taskManager.getEpicById(3));
        taskManager.deletingEpicById(3);
        assertNull(taskManager.getEpicById(3));
    }

    @Test
    void shouldGetHistory() {
        assertNotNull(taskManager.getHistory());
    }

    @Test
    void shouldGiveTheStatusEpicNEW() {
        Epic newEpic = new Epic("Эпик", "Описание эпика1");
        taskManager.createEpic(newEpic);

        assertEquals(Status.NEW, taskManager.getEpicById(8).getStatus());

        SubTask subTask1 = new SubTask("Подзадача 5", "Описание подзадачи 5", Status.NEW, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 6", "Описание подзадачи 6", Status.NEW, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask2);

        assertEquals(Status.NEW, taskManager.getEpicById(8).getStatus());
    }

    @Test
    void shouldGiveStatusEpicDoneElseSubtaskDoneDone() {
        Epic newEpic = new Epic("Эпик", "Описание эпика1");
        taskManager.createEpic(newEpic);

        assertEquals(Status.NEW, taskManager.getEpicById(8).getStatus());

        SubTask subTask1 = new SubTask("Подзадача 5", "Описание подзадачи 5", Status.DONE, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 6", "Описание подзадачи 6", Status.DONE, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask2);

        assertEquals(Status.DONE, taskManager.getEpicById(8).getStatus());
    }

    @Test
    void shouldGiveStatusEpicProgressElseSubtaskStatusNewDone() {
        Epic newEpic = new Epic("Эпик", "Описание эпика1");
        taskManager.createEpic(newEpic);

        assertEquals(Status.NEW, taskManager.getEpicById(8).getStatus());

        SubTask subTask1 = new SubTask("Подзадача 5", "Описание подзадачи 5", Status.NEW, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 6", "Описание подзадачи 6", Status.DONE, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(8).getStatus());
    }

    @Test
    void shouldGiveStatusEpicProgressElseSubtaskStatusProgressProgress() {
        Epic newEpic = new Epic("Эпик", "Описание эпика1");
        taskManager.createEpic(newEpic);

        assertEquals(Status.NEW, taskManager.getEpicById(8).getStatus());

        SubTask subTask1 = new SubTask("Подзадача 5", "Описание подзадачи 5", Status.IN_PROGRESS, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 6", "Описание подзадачи 6", Status.IN_PROGRESS, taskManager.getEpicById(8));
        taskManager.createSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(8).getStatus());
    }

    @Test
    void shouldReturnOneIfTimeTasksIntersect() {
        InMemoryTaskManager memoryTaskManager = new InMemoryTaskManager();

        Task firstTask = new Task("Задача1", "Описание задачи 1", Status.NEW);
        firstTask.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));
        firstTask.setDuration(Duration.ofMinutes(60));
        memoryTaskManager.createTask(firstTask);

        Task secondTask = new Task("Задача1", "Описание задачи 1", Status.NEW);
        secondTask.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));
        secondTask.setDuration(Duration.ofMinutes(60));
        memoryTaskManager.createTask(secondTask);

        assertEquals(1,memoryTaskManager.getListAllTasks().size());

    }

    @Test
    void shouldReturnTwoIfTimeTasksNotIntersect() {
        InMemoryTaskManager memoryTaskManager = new InMemoryTaskManager();

        Task firstTask = new Task("Задача1", "Описание задачи 1", Status.NEW);
        firstTask.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));
        firstTask.setDuration(Duration.ofMinutes(60));
        memoryTaskManager.createTask(firstTask);

        Task secondTask = new Task("Задача1", "Описание задачи 1", Status.NEW);
        secondTask.setStartTime(LocalDateTime.of(2024, 2, 1, 0, 0));
        secondTask.setDuration(Duration.ofMinutes(60));
        memoryTaskManager.createTask(secondTask);

        assertEquals(2,memoryTaskManager.getListAllTasks().size());

    }

}
