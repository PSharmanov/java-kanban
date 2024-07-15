import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    Map<Integer, Task> taskHashMap = new HashMap<>();
    Map<Integer, SubTask> subTaskHashMap = new HashMap<>();
    Map<Integer, Epic> epicHashMap = new HashMap<>();
    HistoryManager historyManager = Manager.getDefaultHistory();
    Set<Task> prioritizedTasksSet = new TreeSet<>(comparatorTaskSet);

    //генератор Id
    public int getIdGenerator() {
        return idCounter++;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    //////////////////////////////////////////////////////////////////////////////////
    // Task
//////////////////////////////////////////////////////////////////////////////////

    //создание Задачи
    @Override
    public void createTask(Task task) {

        if (checkingIntersectionTask(task)) {
            System.out.println("Задача не создана, задачи имеют пересечение по времени.");
        } else {
            task.setId(getIdGenerator());
            taskHashMap.put(task.getId(), task);
            prioritizedTasksSet.add(task);
        }

    }

    //обновление Задачи
    @Override
    public void updateTask(Task task) {

        if (checkingIntersectionTask(task) && taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
            prioritizedTasksSet.add(task);

        } else {
            System.out.println("ERROR: Задача с именем: " + task.getName() + "  не обновлена," +
                    " нет задачи с id: " + task.getId());
        }
    }

    //получение списка всех Задач
    @Override
    public List<Task> getListAllTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    //получение Задачи по Id
    @Override
    public Task getTaskById(int id) {

        //добавление в историю задач
        if (taskHashMap.get(id) != null) {
            historyManager.addHistory(taskHashMap.get(id));
        }

        return taskHashMap.get(id);
    }

    //удаление всех Задач
    @Override
    public void deletingAllTasks() {
        taskHashMap.clear();
    }

    //удаление Задачи по Id
    @Override
    public void deletingTaskById(int id) {
        taskHashMap.remove(id);
    }

//////////////////////////////////////////////////////////////////////////////////
    // SubTask
//////////////////////////////////////////////////////////////////////////////////

    //создание Подзадачи
    @Override
    public void createSubTask(SubTask subTask) {

        if (subTask.getEpic() == null) {
            System.out.println("ERROR: для Подзадачи не найден Эпик ");
            return;

        }

        if (!checkingIntersectionTask(subTask) && epicHashMap.containsKey(subTask.getEpic().getId())) {

            subTask.setId(getIdGenerator());

            subTaskHashMap.put(subTask.getId(), subTask);

            Epic newEpic = subTask.getEpic();

            ArrayList<Integer> newSubTaskList = newEpic.getSubTaskIdList();

            newSubTaskList.add(subTask.getId());

            newEpic.setStatus(chekingStatus(newSubTaskList)); //присвоение статуса эпика

            newEpic.setDuration(durationByEpic(newSubTaskList)); //присвоение длительности эпика

            newEpic.setEndTime(endTimeByEpic(newSubTaskList));//присвоение времени окончания эпика

            prioritizedTasksSet.add(subTask);

        }

    }

    //обнавление Подзадачи
    @Override
    public void updateSubTask(SubTask subTask) {

        if (!checkingIntersectionTask(subTask) && subTaskHashMap.containsKey(subTask.getId())) {

            subTaskHashMap.put(subTask.getId(), subTask);

            Epic newEpic = subTask.getEpic();

            ArrayList<Integer> newSubTaskList = newEpic.getSubTaskIdList();

            int index = newSubTaskList.indexOf(subTask.getId());

            newSubTaskList.set(index, subTask.getId());

            newEpic.setSubTaskArrayList(newSubTaskList);

            newEpic.setStatus(chekingStatus(newSubTaskList)); //присвоение статуса эпика

            newEpic.setDuration(durationByEpic(newSubTaskList)); //присвоение длительности эпика

            newEpic.setEndTime(endTimeByEpic(newSubTaskList));//присвоение времени окончания эпика

            prioritizedTasksSet.add(subTask);

        } else {
            System.out.println("ERROR: Подзадача с именем: " + subTask.name + "  не обновлена, для подзадачи" +
                    " нет Эпика.");
        }

    }

    //получение списка всех Подзадач
    @Override
    public List<SubTask> getListAllSubTasks() {
        return new ArrayList<>(subTaskHashMap.values());
    }

    //получение Подзадачи по Id
    @Override
    public SubTask getSubTaskById(int id) {

        if (subTaskHashMap.containsKey(id)) {
            //добавление в историю задач
            if (subTaskHashMap.get(id) != null) {
                historyManager.addHistory(subTaskHashMap.get(id));
            }

            return subTaskHashMap.get(id);
        }

        return null;
    }

    //удаление всех Подзадач
    @Override
    public void deletingAllSubTasks() {

        for (Epic epic : epicHashMap.values()) {

            ArrayList<Integer> list = epic.getSubTaskIdList();

            list.clear();

            epic.setStatus(chekingStatus(list));

        }

        subTaskHashMap.clear();
    }

    //удаление Подзадачи по Id
    @Override
    public void deletingSubTaskById(Integer id) {

        if (subTaskHashMap.containsKey(id)) {

            SubTask subTask = getSubTaskById(id);

            Epic epic = getEpicById(subTask.getEpic().getId());

            ArrayList<Integer> listNew = epic.getSubTaskIdList();

            listNew.remove(id);

            epic.setStatus(chekingStatus(listNew));

            subTaskHashMap.remove(id);

        }

    }

//////////////////////////////////////////////////////////////////////////////////
    // Epic
//////////////////////////////////////////////////////////////////////////////////

    //создание Эпик
    @Override
    public void createEpic(Epic epic) {

        epic.setId(getIdGenerator());

        epic.setStatus(chekingStatus(epic.getSubTaskIdList()));

        epicHashMap.put(epic.getId(), epic);
    }

    //обновление Эпик
    @Override
    public void updateEpic(Epic epic) {

        ArrayList<Integer> list = epic.getSubTaskIdList();

        Status newEpicStatus = chekingStatus(list);

        epic.setStatus(newEpicStatus);

        epicHashMap.put(epic.getId(), epic);

    }

    //получение списка всех Эпиков
    @Override
    public List<Epic> getListAllEpic() {

        return new ArrayList<>(epicHashMap.values());
    }

    //получение Эпика по Id
    @Override
    public Epic getEpicById(int id) {

        //добавление в историю задач
        if (epicHashMap.get(id) != null) {
            historyManager.addHistory(epicHashMap.get(id));
        }

        return epicHashMap.get(id);
    }

    //удаление всех Эпиков
    @Override
    public void deletingAllEpics() {

        epicHashMap.clear();

        subTaskHashMap.clear();
    }

    //удаление Эпика по Id
    @Override
    public void deletingEpicById(int id) {

        for (Integer subTaskId : getListAllSubTaskByEpicId(id)) {

            subTaskHashMap.remove(subTaskId);
        }

        epicHashMap.remove(id);

    }

    //получение списка подзадач по Id эпика
    @Override
    public List<Integer> getListAllSubTaskByEpicId(int idEpic) {

        Epic epic = getEpicById(idEpic);

        return epic.getSubTaskIdList();
    }

    //проверка статуса
    public Status chekingStatus(ArrayList<Integer> list) {

        int countDone = 0;
        int countNew = 0;

        if (list.isEmpty()) {
            return Status.NEW;
        }

        for (Integer subTaskId : list) {

            if (getSubTaskById(subTaskId).getStatus().equals(Status.DONE)) {
                countDone++;
            }

            if (getSubTaskById(subTaskId).getStatus().equals(Status.NEW)) {
                countNew++;
            }
        }

        if (countDone == list.size()) {
            return Status.DONE;
        }

        if (countNew == list.size()) {
            return Status.NEW;
        }

        return Status.IN_PROGRESS;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //компаратор для сортировки задач при добавлении в набор
    static final Comparator<Task> comparatorTaskSet = (task1, task2) -> {

        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime startTime2 = task2.getEndTime();

        if (startTime1 == null || startTime2 == null) {
            return 0;
        }

        if (startTime1.isBefore(startTime2)) {
            return -1;
        } else if (startTime1.isAfter(startTime2)) {
            return 1;
        } else {
            return 0;
        }

    };

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasksSet.stream().toList();
    }

    //проверка на пересечение двух задач
    public boolean checkingIntersectionTask(Task newTask) {
        LocalDateTime newTaskStartTime = newTask.getStartTime();
        LocalDateTime newTaskEndTime = newTask.getEndTime();

        if ((newTaskStartTime == null) || prioritizedTasksSet.isEmpty()) {
            return false;
        }

        return prioritizedTasksSet.stream()
                .anyMatch(existingTask -> {
                    LocalDateTime existingTaskStartTime = existingTask.getStartTime();
                    LocalDateTime existingTaskEndTime = existingTask.getEndTime();

                    if (existingTaskStartTime == null) {
                        return false;
                    }

                    // Проверка на пересечение
                    return (newTaskStartTime.isBefore(existingTaskEndTime) && newTaskEndTime.isAfter(existingTaskStartTime));
                });
    }

    //вычисление длительности Эпика
    public Duration durationByEpic(ArrayList<Integer> list) {
        return list.stream()
                .map(id -> subTaskHashMap.get(id))
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }


    //вычисление времени окончания эпика
    public LocalDateTime endTimeByEpic(ArrayList<Integer> list) {
        return list.stream()
                .map(id -> subTaskHashMap.get(id))
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

    }

}
