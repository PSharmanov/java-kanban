import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String HEAD_FILE = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // дополнительное задание спринт 7, реализовано в main()
//    public static void main(String[] args) {
//        FileBackedTaskManager firstManager = Manager.getDefaultFileBackedTaskManager();
//
//        firstManager.createTask(new Task("Задача1", "Описание задачи1", Status.NEW));
//        firstManager.createTask(new Task("Задача2", "Описание задачи2", Status.IN_PROGRESS));
//        firstManager.createTask(new Task("Задача3", "Описание задачи3", Status.DONE));
//
//        firstManager.createEpic(new Epic("Эпик1", "Описание эпик1"));
//        firstManager.createEpic(new Epic("Эпик2", "Описание эпик2"));
//        firstManager.createEpic(new Epic("Эпик3", "Описание эпик3"));
//
//        firstManager.createSubTask(new SubTask("Подзадача1", "Описание подзадачи1", Status.NEW, firstManager.getEpicById(4)));
//        firstManager.createSubTask(new SubTask("Подзадача2", "Описание подзадачи2", Status.DONE, firstManager.getEpicById(4)));
//        firstManager.createSubTask(new SubTask("Подзадача3", "Описание подзадачи3", Status.NEW, firstManager.getEpicById(5)));
//
//        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(new File("src/FileBackedTaskManager.csv"));
//
//        System.out.println(newManager.getListAllTasks());
//        System.out.println(newManager.getListAllEpic());
//        System.out.println(newManager.getListAllSubTasks());
//
//    }

    // сохранение текущего состояния менеджера в файл
    private void save() {
        if (!file.exists()) {
            throw new ManagerSaveException("Файл для сохранения данных менеджера не найден!");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            bw.write(HEAD_FILE);
            bw.newLine();

            for (Task task : taskHashMap.values()) {
                bw.write(toString(task));
                bw.newLine();
            }

            for (Epic task : epicHashMap.values()) {
                bw.write(toString(task));
                bw.newLine();
            }

            for (SubTask task : subTaskHashMap.values()) {
                bw.write(toString(task));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    //преобразовывает строку в задачу
    private Task fromString(String value) {
        String[] lineArray = value.split(",");

        int id = Integer.parseInt(lineArray[0]);
        TypeTasks type = TypeTasks.valueOf(lineArray[1]);
        String name = lineArray[2];
        Status status = Status.valueOf(lineArray[3]);
        String description = lineArray[4];

        switch (type) {
            case TASK:
                Task newTask = new Task(name, description, status);
                newTask.setId(id);
                return newTask;
            case EPIC:
                Epic newEpic = new Epic(name, description);
                newEpic.setStatus(status);
                newEpic.setId(id);
                return newEpic;
            case SUBTASK:
                int epicId = Integer.parseInt(lineArray[5]);
                Epic epic = epicHashMap.get(epicId);
                SubTask newSubTask = new SubTask(name, description, status, epic);
                newSubTask.setId(id);
                return newSubTask;
            default:
                throw new ManagerSaveException("Тип задачи не определен!");
        }
    }

    // преобразование задачи в строку для сохранения в файл
    private String toString(Task task) {
        return task.getId() + "," +
                task.getTypeTasks() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ",";
    }

    // преобразование задачи в строку для сохранения в файл
    private String toString(Epic task) {
        return task.getId() + "," +
                task.getTypeTasks() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ",";
    }


    // преобразование задачи в строку для сохранения в файл
    private String toString(SubTask task) {
        return task.getId() + "," +
                task.getTypeTasks() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getEpic().getId();
    }

    //восстановление данных менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        if (!file.exists()) {
            throw new ManagerSaveException("Файл данных для менеджера не найден!");
        }

        int idCounter = 0;

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            while (br.ready()) {
                String line = br.readLine();

                //пропускаем заголовок
                if (line.equals(HEAD_FILE)) {
                    continue;
                }

                Task task = manager.fromString(line);

                //восстановление последнего значение счетчика id
                if (task.getId() > idCounter) {
                    manager.setIdCounter(task.getId());
                }

                switch (task.getTypeTasks()) {
                    case TASK:
                        manager.taskHashMap.put(task.getId(), task);
                        break;
                    case EPIC:
                        manager.epicHashMap.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        manager.subTaskHashMap.put(task.getId(), (SubTask) task);
                        break;
                }

            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        return manager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deletingAllTasks() {
        super.deletingAllTasks();
        save();
    }

    @Override
    public void deletingTaskById(int id) {
        super.deletingTaskById(id);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deletingAllSubTasks() {
        super.deletingAllSubTasks();
        save();
    }

    @Override
    public void deletingSubTaskById(Integer id) {
        super.deletingSubTaskById(id);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deletingAllEpics() {
        super.deletingAllEpics();
        save();
    }

    @Override
    public void deletingEpicById(int id) {
        super.deletingEpicById(id);
        save();
    }
}
