import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter = 1;
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
        return new ArrayList<> (taskHashMap.values());
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

            ArrayList<Integer> newSubTaskList = newEpic.getSubTaskIdList();

            newSubTaskList.add(subTask.getId());

            newEpic.setStatus(chekingStatus(newSubTaskList));

        } else {
            System.out.println("ERROR: для Подзадачи не найден Эпик c id" + subTask.getEpic().getId());
        }

    }

    //обнавление Подзадачи
    public void updateSubTask(SubTask subTask) {

        if(subTaskHashMap.containsKey(subTask.getId())){

            subTaskHashMap.put(subTask.getId(), subTask);

            Epic newEpic = subTask.getEpic();

            ArrayList<Integer> newSubTaskList = newEpic.getSubTaskIdList();

            int index = newSubTaskList.indexOf(subTask.getId());

            newSubTaskList.set(index, subTask.getId());

            newEpic.setSubTaskArrayList(newSubTaskList);

            newEpic.setStatus(chekingStatus(newSubTaskList));

        } else {
            System.out.println("ERROR: Подзадача с именем: " + subTask.name + "  не обновлена, для подзадачи" +
                    " нет Эпика с id: " + subTask.getEpic().getId());
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

            ArrayList<Integer> list = epic.getSubTaskIdList();

            list.clear();

            epic.setStatus(chekingStatus(list));

        }

        subTaskHashMap.clear();
    }

    //удаление Подзадачи по Id
    public void deletingSubTaskById(int id) {

        SubTask subTask = getSubTaskById(id);

        Epic epic = getEpicById(subTask.getEpic().getId());

        ArrayList<Integer> listNew = epic.getSubTaskIdList();

        listNew.remove(subTask.getEpic().getId());

        epic.setStatus(chekingStatus(listNew));

        subTaskHashMap.remove(id);

    }

//////////////////////////////////////////////////////////////////////////////////
    // Epic
//////////////////////////////////////////////////////////////////////////////////

    //создание Эпик
    public void createEpic(Epic epic) {

        epic.setId(getIdGenerator());

        epic.setStatus(chekingStatus(epic.getSubTaskIdList()));

        epicHashMap.put(epic.getId(), epic);
    }

    //обновление Эпик
    public void updateEpic(Epic epic) {

        ArrayList <Integer> list = epic.getSubTaskIdList();

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

        for (Integer subTaskId : getListAllSubTaskByEpicId(id)){

            subTaskHashMap.remove(subTaskId);
        }

        epicHashMap.remove(id);

    }

    //получение списка подзадач по Id эпика
    public ArrayList<Integer> getListAllSubTaskByEpicId(int idEpic){

        Epic epic = getEpicById(idEpic);

        return epic.getSubTaskIdList();
    }

    //проверка статуса
    public Status chekingStatus(ArrayList<Integer> list){

        int Done = 0;
        int New = 0;

        if (list.isEmpty()){ return Status.NEW;}

        for (Integer subTaskId : list){

            if (getSubTaskById(subTaskId).getStatus().equals(Status.DONE)){
                Done++;
            }

            if (getSubTaskById(subTaskId).getStatus().equals(Status.NEW)){
                New++;
            }
        }

        if (Done == list.size()){return Status.DONE;}

        if (New == list.size()){return Status.NEW;}

        return Status.IN_PROGRESS;
    }

}
