import java.util.ArrayList;

public class Epic extends Task{
    protected ArrayList<SubTask> subTaskArrayList;

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.subTaskArrayList = new ArrayList<>();

    }

    //метод возвращает список подзадач Эпика
    public ArrayList<SubTask> getSubTaskArrayList() {
        return subTaskArrayList;
    }

    //метод устанавливает список подзадач Эпика
    public void setSubTaskArrayList(ArrayList<SubTask> subTaskArrayList) {
        this.subTaskArrayList = subTaskArrayList;
    }
}
