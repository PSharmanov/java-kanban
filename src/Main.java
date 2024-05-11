public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        ////////////////// BEGINNING TEST 1 ////////////////////////////

        //Создаем две задачи
        Task task1 = new Task("Задача1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание задачи 2", Status.IN_PROGRESS);

        //добавляем задачи в HashMap
        manager.createTask(task1);
        manager.createTask(task2);

        //Создаем два Эпик
        Epic epic1 = new Epic("Эпик1", "Описание Эпика 1");
        Epic epic2 = new Epic("Эпик2", "Описание Эпика 2");

        //Добавляем эпики в списки
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        //Создаем подзадачи для Эпиков
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epic1);
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", Status.DONE, epic2);

        //Добавляем подзадачи в списки
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        ////////////////// END TEST 1 ////////////////////////////


        ////////////////// BEGINNING TEST 2 ////////////////////////////

        System.out.println("*******************Списки задач, подзадач, эпиков: *******************");

        //выводим список всех задач
        System.out.println("Список всех задач: ");
        System.out.println(manager.getListAllTasks().toString());

        //Вывод списка всех эпиков
        System.out.println("Список всех эпиков: ");
        System.out.println(manager.getListAllEpic().toString());

        //Вывод списка всех подзадач
        System.out.println("Список всех позадач: ");
        System.out.println(manager.getListAllSubTasks().toString());

        ////////////////// END TEST 2 ////////////////////////////


        ////////////////// BEGINNING TEST 3 ////////////////////////////

        //изменяем статусы задач
        task1.setStatus(Status.DONE);
        task1.setStatus(Status.DONE);

        //обнавляем задачи
        manager.updateTask(task1);
        manager.updateTask(task2);

        //изменяем статусы подзадач
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.IN_PROGRESS);

        //обновляем подзадачи
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        //выводим список всех задач
        System.out.println("***************Списки задач, подзадач, эпиков, после изменений объектов: ****************");

        System.out.println("Список всех задач: ");
        System.out.println(manager.getListAllTasks().toString());

        //Вывод списка всех эпиков
        System.out.println("Список всех эпиков: ");
        System.out.println(manager.getListAllEpic().toString());

        //Вывод списка всех подзадач
        System.out.println("Список позадач: ");
        System.out.println(manager.getListAllSubTasks().toString());

        ////////////////// END TEST 3 ////////////////////////////


        ////////////////// BEGINNING TEST 4 ////////////////////////////

        //Удаляем одну задачу
        manager.deletingTaskById(1);

        //Удалем Эпик1
        manager.deletingEpicById(3);

        System.out.println("*******************Списки задач, подзадач, эпиков, после удаления: **********************");

        System.out.println("Список всех задач: ");
        System.out.println(manager.getListAllTasks().toString());

        //Вывод списка всех эпиков
        System.out.println("Список всех эпиков: ");
        System.out.println(manager.getListAllEpic().toString());

        //Вывод списка всех подзадач
        System.out.println("Список подзадач: ");
        System.out.println(manager.getListAllSubTasks().toString());

        ////////////////// END TEST 4 ////////////////////////////

    }
}


