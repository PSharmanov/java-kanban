package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import managers.FileBackedTaskManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;

import java.io.File;

public class Manager {

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager() {
        return new FileBackedTaskManager(new File("src/managers.FileBackedTaskManager.csv"));
    }

}
