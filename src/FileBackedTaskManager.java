import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String HEAD_FILE = "id,type,name,status,description,start,duration,end,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // сохранение текущего состояния менеджера в файл
    void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            bw.write(HEAD_FILE);
            bw.newLine();

            for (Task task : taskHashMap.values()) {
                bw.write(converterTasktoString(task));
                bw.newLine();
            }

            for (Epic task : epicHashMap.values()) {
                bw.write(converterTasktoString(task));
                bw.newLine();
            }

            for (SubTask task : subTaskHashMap.values()) {
                bw.write(converterTasktoString(task));
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
                int epicId = Integer.parseInt(lineArray[8]);
                Epic epic = epicHashMap.get(epicId);
                SubTask newSubTask = new SubTask(name, description, status, epic);
                newSubTask.setId(id);
                return newSubTask;
            default:
                throw new ManagerSaveException("Тип задачи не определен!");
        }
    }

    // преобразование задачи в строку для сохранения в файл
    private String converterTasktoString(Task task) {
        StringBuilder sb = new StringBuilder();

        sb.append(task.getId()).append(",");
        sb.append(task.getTypeTasks()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        sb.append(task.getStartTime()).append(",");
        sb.append(task.getDuration()).append(",");
        sb.append(task.getEndTime() == null ? "null" : task.getEndTime()).append(",");

        if (task instanceof SubTask) {
            sb.append(((SubTask) task).getEpic().getId());
        } else {
            sb.append(",");
        }

        return sb.toString();
    }

    //восстановление данных менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {

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
