import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> taskHashMap;
    private final Map<Integer, SubTask> subTaskHashMap = new HashMap<>();
    private final Map<Integer, Epic> epicHashMap = new HashMap<>();
    private final HistoryManager historyManager = Manager.getDefaultHistory();
    private final Set<Task> prioritizedTasksSet;

    public InMemoryTaskManager() {
        //компаратор для сортировки задач при добавлении в набор
        Comparator<Task> comparatorTaskSet = (task1, task2) -> {

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
        prioritizedTasksSet = new TreeSet<>(comparatorTaskSet);
        taskHashMap = new HashMap<>();
    }

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

        if (!checkingIntersectionTask(task) && taskHashMap.containsKey(task.getId())) {
            prioritizedTasksSet.remove(getTaskById(task.getId()));
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
        prioritizedTasksSet.removeAll(getListAllTasks());
        taskHashMap.clear();
    }

    //удаление Задачи по Id
    @Override
    public void deletingTaskById(int id) {
        prioritizedTasksSet.remove(getTaskById(id));
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

            updateEpicParameters(newEpic, newSubTaskList);

            prioritizedTasksSet.add(subTask);

        }

    }

    //обнавление Подзадачи
    @Override
    public void updateSubTask(SubTask subTask) {

        if ((!checkingIntersectionTask(subTask) && subTaskHashMap.containsKey(subTask.getId()))) {

            prioritizedTasksSet.remove(getSubTaskById(subTask.getId()));

            subTaskHashMap.put(subTask.getId(), subTask);

            prioritizedTasksSet.add(subTask);

            Epic newEpic = subTask.getEpic();

            ArrayList<Integer> newSubTaskList = newEpic.getSubTaskIdList();

            int index = newSubTaskList.indexOf(subTask.getId());

            newSubTaskList.set(index, subTask.getId());

            newEpic.setSubTaskArrayList(newSubTaskList);

            updateEpicParameters(newEpic, newSubTaskList);

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

            updateEpicParameters(epic, list);

        }

        prioritizedTasksSet.removeAll(getListAllSubTasks());

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

            updateEpicParameters(epic, listNew);

            prioritizedTasksSet.remove(subTask);

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
        epicHashMap.put(epic.getId(), epic);
    }

    //обновление Эпик
    @Override
    public void updateEpic(Epic epic) {

        final Epic savedEpic = epicHashMap.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
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

        prioritizedTasksSet.removeAll(getListAllSubTasks());

        epicHashMap.clear();

        subTaskHashMap.clear();
    }

    //удаление Эпика по Id
    @Override
    public void deletingEpicById(int id) {

        for (Integer subTaskId : getListAllSubTaskByEpicId(id)) {
            prioritizedTasksSet.remove(getSubTaskById(subTaskId));
            subTaskHashMap.remove(subTaskId);
        }

        epicHashMap.remove(id);

    }

    //получение списка подзадач по Id эпика
    @Override
    public List<Integer> getListAllSubTaskByEpicId(int idEpic) {
        Map<Integer, SubTask> subTaskMap = getSubTaskHashMap();

        List<Integer> list = subTaskMap.values().stream()
                .filter(task -> task.getEpic().getId() == idEpic)
                .map(Task::getId)
                .toList();

        return list;
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasksSet.stream().toList();
    }

    //проверка на пересечение двух задач
    private boolean checkingIntersectionTask(Task newTask) {
        LocalDateTime start1 = newTask.getStartTime();
        LocalDateTime end1 = newTask.getEndTime();

        if ((start1 == null) || prioritizedTasksSet.isEmpty()) {
            return false;
        }

        return prioritizedTasksSet.stream()
                .anyMatch(existingTask -> {
                    LocalDateTime start2 = existingTask.getStartTime();
                    LocalDateTime end2 = existingTask.getEndTime();

                    if (start2 == null) {
                        return false;
                    }

                    // Проверка на пересечение
                    return ((start1.isEqual(start2) || start1.isBefore(start2)) && end1.isAfter(start2)
                            || (start2.isEqual(start1) || start2.isBefore(start1)) && end2.isAfter(start1));
                });
    }

    //обновление параметров эпика
    private void updateEpicParameters(Epic newEpic, ArrayList<Integer> newSubTaskList) {

        Duration duration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Integer subTaskId : newSubTaskList) {
            SubTask subTask = getSubTaskById(subTaskId);

            if (subTask.getDuration() != null) {
                duration = duration.plus(subTask.getDuration());
            }
            if (subTask.getStartTime() != null) {
                if (startTime == null || subTask.getStartTime().isBefore(startTime)) {
                    startTime = subTask.getStartTime();
                }
                if (endTime == null || subTask.getEndTime().isAfter(endTime)) {
                    endTime = subTask.getEndTime();
                }
            }
        }

        newEpic.setStatus(chekingStatus(newSubTaskList));
        newEpic.setStartTime(startTime);
        newEpic.setDuration(duration);
        newEpic.setEndTime(endTime);
    }

    Map<Integer, Task> getTaskHashMap() {
        return taskHashMap;
    }

    Map<Integer, SubTask> getSubTaskHashMap() {
        return subTaskHashMap;
    }

    Map<Integer, Epic> getEpicHashMap() {
        return epicHashMap;
    }

    Set<Task> getPrioritizedTasksSet() {
        return prioritizedTasksSet;
    }
}





