package managers;

import adapters.DurationTypeAdapter;
import adapters.LocalDataTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        return gsonBuilder.create();
    }

}
