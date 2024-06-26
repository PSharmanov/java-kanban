import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;

class FileBackedTaskManagerTest {

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
}