package models;

import enums.Status;
import enums.TypeTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subTaskIdList;
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.subTaskIdList = new ArrayList<>();

    }

    public Epic(String name, String description, int id) {
        super(name, description, id, null);
        this.subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
    }

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    //метод возвращает список подзадач Эпика
    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    //метод устанавливает список подзадач Эпика
    public void setSubTaskArrayList(ArrayList<Integer> subTaskArrayList) {
        this.subTaskIdList = subTaskArrayList;
    }

    //возвращает тип задачи
    @Override
    public TypeTasks getTypeTasks() {
        return TypeTasks.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
