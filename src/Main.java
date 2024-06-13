public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Manager.getDefaultTaskManager();

        printAllTasks(taskManager);

    }

    //выводит историю просмотров задач
    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getListAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getListAllEpic()) {
            System.out.println(epic);

            for (int task : taskManager.getListAllSubTaskByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getListAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }


}


