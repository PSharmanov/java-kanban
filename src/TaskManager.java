import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    int idCounter = 1;
    HashMap<Integer, Task> taskHashMap = new HashMap<>();
    HashMap<Integer, SubTask> subTaskHashMap = new HashMap<>();
    HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    //генератор Id
    public int getIdGenerator() {
        return idCounter++;
    }

//////////////////////////////////////////////////////////////////////////////////
    // Task
//////////////////////////////////////////////////////////////////////////////////

    //создание Задачи
    public void createTask(Task task) {
        task.setId(getIdGenerator());

        taskHashMap.put(task.getId(), task);
    }

    //обновление Задачи
    public void updateTask(Task task) {

        if (taskHashMap.containsKey(task.getId())){
            taskHashMap.put(task.getId(), task);

        } else {
            System.out.println("ERROR: Задача с именем: " + task.getName() + "  не обновлена," +
                    " нет задачи с id: " + task.getId());
        }

    }

    //получение списка всех Задач
    public ArrayList<Task> getListAllTasks() {
        ArrayList<Task> list;
        list = new ArrayList<>(taskHashMap.values());
        return list;
    }

    //получение Задачи по Id
    public Task getTaskById(int id) {
        return taskHashMap.get(id);
    }

    //удаление всех Задач
    public void deletingAllTasks() {
        taskHashMap.clear();
    }

    //удаление Задачи по Id
    public void deletingTaskById(int id) {
        taskHashMap.remove(id);
    }

//////////////////////////////////////////////////////////////////////////////////
    // SubTask
//////////////////////////////////////////////////////////////////////////////////

    //создание Подзадачи
    public void createSubTask(SubTask subTask) {

        if(epicHashMap.containsKey(subTask.getEpic().getId())){

            subTask.setId(getIdGenerator());

            subTaskHashMap.put(subTask.getId(), subTask);

            Epic newEpic = subTask.getEpic();

            ArrayList<SubTask> newSubTaskList = newEpic.getSubTaskArrayList();

            newSubTaskList.add(subTask);

            newEpic.setSubTaskArrayList(newSubTaskList);

            newEpic.setStatus(chekingStatus(newSubTaskList));

            updateEpic(newEpic);

        } else {
            System.out.println("ERROR: для Подзадачи не найден Эпик c id" + subTask.epic.getId());
        }

    }

    //обнавление Подзадачи
    public void updateSubTask(SubTask subTask) {

        if(subTaskHashMap.containsKey(subTask.getId())){

            subTaskHashMap.put(subTask.getId(), subTask);

            Epic newEpic = subTask.getEpic();

            ArrayList<SubTask> newSubTaskList = newEpic.getSubTaskArrayList();

            int index = newSubTaskList.indexOf(subTask);

            newSubTaskList.set(index, subTask);

            newEpic.setSubTaskArrayList(newSubTaskList);

            newEpic.setStatus(chekingStatus(newSubTaskList));

            updateEpic(newEpic);

        } else {
            System.out.println("ERROR: Подзадача с именем: " + subTask.name + "  не обновлена, для подзадачи" +
                    " нет Эпика с id: " + subTask.epic.id);
        }


    }

    //получение списка всех Подзадач
    public ArrayList<SubTask> getListAllSubTasks() {
        return new ArrayList<>(subTaskHashMap.values());
    }

    //получение Подзадачи по Id
    public SubTask getSubTaskById(int id) {
        return subTaskHashMap.get(id);
    }

    //удаление всех Подзадач
    public void deletingAllSubTasks() {

        for (Epic epic : epicHashMap.values()){

            ArrayList<SubTask> list = epic.getSubTaskArrayList();

            list.clear();

            epic.setStatus(chekingStatus(list));

            updateEpic(epic);
        }

        subTaskHashMap.clear();
    }

    //удаление Подзадачи по Id
    public void deletingSubTaskById(int id) {

        SubTask subTask = getSubTaskById(id);

        Epic epic = getEpicById(subTask.getEpic().getId());

        ArrayList<SubTask> listNew = epic.getSubTaskArrayList();

        listNew.remove(subTask);

        epic.setSubTaskArrayList(listNew);

        epic.setStatus(chekingStatus(listNew));

        updateEpic(epic);

        subTaskHashMap.remove(id);


    }

//////////////////////////////////////////////////////////////////////////////////
    // Epic
//////////////////////////////////////////////////////////////////////////////////

    //создание Эпик
    public void createEpic(Epic epic) {

        epic.setId(getIdGenerator());

        epic.setStatus(chekingStatus(epic.getSubTaskArrayList()));

        epicHashMap.put(epic.getId(), epic);
    }

    //обновление Эпик
    public void updateEpic(Epic epic) {

        ArrayList <SubTask> list = epic.getSubTaskArrayList();

        Status newEpicStatus = chekingStatus(list);

        epic.setStatus(newEpicStatus);

        epicHashMap.put(epic.getId(), epic);

    }

    //получение списка всех Эпиков
    public ArrayList<Epic> getListAllEpic() {

        return new ArrayList<>(epicHashMap.values());
    }

    //получение Эпика по Id
    public Epic getEpicById(int id) {
        return epicHashMap.get(id);
    }

    //удаление всех Эпиков
    public void deletingAllEpics() {

        epicHashMap.clear();

        subTaskHashMap.clear();
    }

    //удаление Эпика по Id
    public void deletingEpicById(int id) {

        for (SubTask subTask : getListAllSubTaskByEpicId(id)){

            subTaskHashMap.remove(subTask.getId());
        }

        epicHashMap.remove(id);

    }

    //получение списка подзадач по Id эпика
    public ArrayList<SubTask> getListAllSubTaskByEpicId(int idEpic){

        Epic epic = getEpicById(idEpic);

        return epic.getSubTaskArrayList();
    }

    //проверка статуса
    public Status chekingStatus(ArrayList<SubTask> list){

        int Done = 0;
        int New = 0;

        if (list.isEmpty()){ return Status.NEW;}

        for (SubTask subTask : list){

            if (subTask.getStatus().equals(Status.DONE)){
                Done++;
            }

            if (subTask.getStatus().equals(Status.NEW)){
                New++;
            }
        }

        if (Done == list.size()){return Status.DONE;}

        if (New == list.size()){return Status.NEW;}

        return Status.IN_PROGRESS;
    }

}
