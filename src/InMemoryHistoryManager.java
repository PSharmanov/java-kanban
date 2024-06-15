import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node<Task>> history = new HashMap<>();

    //переменные для реализации двусвязного списка
    private Node head;
    private Node tail;

    //метод добавления задач в список
    @Override
    public void addHistory(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        linkLast(task);


    }

    //удаление истории по id задачи
    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    //метод возвращает список последник задач
    @Override
    public List<Task> getHistory() {
        return getTask();
    }

    //метод добавления ноды в конец списка
    private void linkLast(Task task) {

        final Node oldTail = tail;
        final Node newNode = new Node(tail, task, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        history.put(task.getId(), newNode);
    }

    //метод удаления ноды
    private void removeNode(Node node) {
        final Node prev = node.prev;
        final Node next = node.next;

        if (prev == null && next == null) {
            head = null;
            tail = null;
            return;
        }

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }

    }

    //возвращает список задач из двусвязного списка
    public List<Task> getTask() {
        List<Task> list = new ArrayList<>();

        for (Node<Task> node = head; node != null; node = node.next) {
            list.add(node.task);
        }

        return list;
    }

    //класс объектов двусвязного списка истории
    static class Node<E> {
        public Task task;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, Task task, Node<E> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

}


