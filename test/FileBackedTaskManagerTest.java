import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.createTempFile;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

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
}