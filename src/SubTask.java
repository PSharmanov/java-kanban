public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
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
