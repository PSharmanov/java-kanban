package models;

import enums.Status;
import enums.TypeTasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public SubTask(String name, String description, int id, Status status, Epic epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public SubTask(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime, Epic epic) {
        super(name, description, id, status, duration, startTime);
        this.epic = epic;
    }

    public SubTask(String name, String description, Status status, Duration duration, LocalDateTime startTime, Epic epic) {
        super(name, description, status, duration, startTime);
        this.epic = epic;
    }


    public Epic getEpic() {
        return epic;
    }

    @Override
    public TypeTasks getTypeTasks() {
        return TypeTasks.SUBTASK;
    }
}
