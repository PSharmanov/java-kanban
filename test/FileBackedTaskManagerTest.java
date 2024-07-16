import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.io.File.createTempFile;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("test/FileBackedTaskManagerTest.csv"));
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(0, manager.getListAllTasks().size());
        tmpFile.deleteOnExit();
    }

    @Test
    void shouldSaveAndLoadTaskFromFile() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(0, manager.getListAllTasks().size());
        manager.createTask(new Task("Задача1", "Описание задачи1", Status.NEW));
        manager.createEpic(new Epic("Эпик1", "Описание эпик1"));
        manager.createSubTask(new SubTask("Подзадача1", "Описание подзадачи1", Status.NEW, manager.getEpicById(2)));
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(1, manager2.getListAllTasks().size());
        assertEquals(1, manager2.getListAllEpic().size());
        assertEquals(1, manager2.getListAllSubTasks().size());
        tmpFile.deleteOnExit();
    }

    @TempDir
    Path tempDir = Paths.get("C://");

    @Test
    void testLoadFromFileThrowsExceptionWhenFileNotFound() {
        File nonExistentFile = new File(tempDir.toFile(), "nonExistentFile.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile));
    }

    @Test
    public void testLoadFromFileDoesNotThrowExceptionWhenFileExists() throws IOException {
        File existingFile = new File(tempDir.toFile(), "existingFile.csv");
        Files.createFile(existingFile.toPath());
        assertDoesNotThrow(() -> {
            FileBackedTaskManager.loadFromFile(existingFile);
        });
    }

    @Test
    void testSaveDoesNotThrowExceptionWhenFileWritable() throws IOException {
        File writableFile = new File(tempDir.toFile(), "writableFile.csv");
        Files.createFile(writableFile.toPath());
        FileBackedTaskManager manager = new FileBackedTaskManager(writableFile);
        assertDoesNotThrow(manager::save);

    }

    @Test
    void testSaveThrowsExceptionWhenFileNotWritable() throws IOException {
        File readOnlyFile = new File(tempDir.toFile(), "readOnlyFile.csv");
        Files.createFile(readOnlyFile.toPath());
        assertTrue(readOnlyFile.setWritable(false));
        FileBackedTaskManager manager = new FileBackedTaskManager(readOnlyFile);
        assertThrows(ManagerSaveException.class, manager::save);

    }

    @Test
    void shouldRecoverPrioritizedTasksSet() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(0, manager.getPrioritizedTasks().size());

        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        task1.setStartTime(LocalDateTime.parse("10:00 01.01.2024", task1.dateTimeFormatter));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        manager.createEpic(new Epic("Эпик1", "Описание эпик1"));

        SubTask subTask1 = new SubTask("Подзадача1", "Описание подзадачи1", Status.NEW, manager.getEpicById(2));
        subTask1.setStartTime(LocalDateTime.parse("11:00 01.01.2024", task1.dateTimeFormatter));
        subTask1.setDuration(Duration.ofMinutes(30));
        manager.createSubTask(subTask1);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tmpFile);

        assertEquals(2, manager2.getPrioritizedTasks().size());
        tmpFile.deleteOnExit();
    }

    @Test
    void shouldDelTaskPrioritizedTasksSet() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);

        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        task1.setStartTime(LocalDateTime.parse("10:00 01.01.2024", task1.dateTimeFormatter));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Задача1", "Описание задачи1", Status.NEW);
        task2.setStartTime(LocalDateTime.parse("10:00 02.01.2024", task1.dateTimeFormatter));
        task2.setDuration(Duration.ofMinutes(30));
        manager.createTask(task2);

        assertEquals(2, manager.getPrioritizedTasks().size());

        manager.deletingAllTasks();

        assertEquals(0, manager.getPrioritizedTasks().size());

        tmpFile.deleteOnExit();
    }

    @Test
    void shouldRecoverTaskEqualsSaveTasks() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(0, manager.getPrioritizedTasks().size());

        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        task1.setStartTime(LocalDateTime.parse("10:00 01.01.2024", task1.dateTimeFormatter));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tmpFile);

        assertEquals(task1, manager2.getTaskById(1));
        tmpFile.deleteOnExit();
    }

    @Test
    void shouldRecoverTaskContentEqualsSaveTasksContent() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(0, manager.getPrioritizedTasks().size());

        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        task1.setStartTime(LocalDateTime.parse("10:00 01.01.2024", task1.dateTimeFormatter));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tmpFile);

        assertEquals(task1.getId(), manager2.getTaskById(1).getId());
        assertEquals(task1.getTypeTasks(), manager2.getTaskById(1).getTypeTasks());
        assertEquals(task1.getName(), manager2.getTaskById(1).getName());
        assertEquals(task1.getStatus(), manager2.getTaskById(1).getStatus());
        assertEquals(task1.getDescription(), manager2.getTaskById(1).getDescription());
        assertEquals(task1.getStartTime(), manager2.getTaskById(1).getStartTime());
        assertEquals(task1.getDuration(), manager2.getTaskById(1).getDuration());
        assertEquals(task1.getEndTime(), manager2.getTaskById(1).getEndTime());

        tmpFile.deleteOnExit();
    }

    @Test
    void shouldRecoverSubTaskContentEqualsSaveSubTasksContent() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(0, manager.getPrioritizedTasks().size());

        manager.createEpic(new Epic("Эпик1", "Описание эпик1"));

        SubTask subTask1 = new SubTask("Подзадача1", "Описание подзадачи1", Status.NEW, manager.getEpicById(1));
        subTask1.setStartTime(LocalDateTime.parse("11:00 01.01.2024", subTask1.dateTimeFormatter));
        subTask1.setDuration(Duration.ofMinutes(30));
        manager.createSubTask(subTask1);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tmpFile);

        assertEquals(subTask1.getId(), manager2.getSubTaskById(2).getId());
        assertEquals(subTask1.getTypeTasks(), manager2.getSubTaskById(2).getTypeTasks());
        assertEquals(subTask1.getName(), manager2.getSubTaskById(2).getName());
        assertEquals(subTask1.getStatus(), manager2.getSubTaskById(2).getStatus());
        assertEquals(subTask1.getDescription(), manager2.getSubTaskById(2).getDescription());
        assertEquals(subTask1.getStartTime(), manager2.getSubTaskById(2).getStartTime());
        assertEquals(subTask1.getDuration(), manager2.getSubTaskById(2).getDuration());
        assertEquals(subTask1.getEndTime(), manager2.getSubTaskById(2).getEndTime());
        assertEquals(subTask1.getEpic(), manager2.getSubTaskById(2).getEpic());

        tmpFile.deleteOnExit();
    }

    @Test
    void shouldRecoverEpicContentEqualsSaveEpicContent() throws IOException {
        File tmpFile = createTempFile("testFile", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(0, manager.getPrioritizedTasks().size());

        Epic epic1 = new Epic("Эпик1", "Описание эпик1");
        manager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Подзадача1", "Описание подзадачи1", Status.NEW, manager.getEpicById(1));
        subTask1.setStartTime(LocalDateTime.parse("11:00 01.01.2024", subTask1.dateTimeFormatter));
        subTask1.setDuration(Duration.ofMinutes(30));
        manager.createSubTask(subTask1);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tmpFile);

        assertEquals(epic1.getId(), manager2.getEpicById(1).getId());
        assertEquals(epic1.getTypeTasks(), manager2.getEpicById(1).getTypeTasks());
        assertEquals(epic1.getName(), manager2.getEpicById(1).getName());
        assertEquals(epic1.getStatus(), manager2.getEpicById(1).getStatus());
        assertEquals(epic1.getDescription(), manager2.getEpicById(1).getDescription());
        assertEquals(epic1.getStartTime(), manager2.getEpicById(1).getStartTime());
        assertEquals(epic1.getDuration(), manager2.getEpicById(1).getDuration());
        assertEquals(epic1.getEndTime(), manager2.getEpicById(1).getEndTime());
        assertEquals(epic1.getSubTaskIdList(), manager2.getEpicById(1).getSubTaskIdList());

        tmpFile.deleteOnExit();
    }


}