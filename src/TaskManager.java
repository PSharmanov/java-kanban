import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //создание Задачи
    void createTask(Task task);

    //обновление Задачи
    int updateTask(Task task);

    //получение списка всех Задач
    ArrayList<Task> getListAllTasks();

    //получение Задачи по Id
    Task getTaskById(int id);

    //удаление всех Задач
    void deletingAllTasks();

    //удаление Задачи по Id
    void deletingTaskById(int id);

    //создание Подзадачи
    int createSubTask(SubTask subTask);

    //обнавление Подзадачи
    int updateSubTask(SubTask subTask);

    //получение списка всех Подзадач
    ArrayList<SubTask> getListAllSubTasks();

    //получение Подзадачи по Id
    SubTask getSubTaskById(int id);

    //удаление всех Подзадач
    void deletingAllSubTasks();

    //удаление Подзадачи по Id
   int deletingSubTaskById(Integer id);

    //создание Эпик
    void createEpic(Epic epic);

    //обновление Эпик
    void updateEpic(Epic epic);

    //получение списка всех Эпиков
    ArrayList<Epic> getListAllEpic();

    //получение Эпика по Id
    Epic getEpicById(int id);

    //удаление всех Эпиков
    void deletingAllEpics();

    //удаление Эпика по Id
    void deletingEpicById(int id);

    //получение списка подзадач по Id эпика
    ArrayList<Integer> getListAllSubTaskByEpicId(int idEpic);

    //получение списка последних 10 просмотренных задач
    List<Task> getHistory ();
}
