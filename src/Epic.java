import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subTaskIdList;

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.subTaskIdList = new ArrayList<>();

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
}
