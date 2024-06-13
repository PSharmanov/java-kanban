import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node<Task>> history = new HashMap<>();

    //метод добавления задач в список
    @Override
    public void addHistory(Task task) {
        if (history.containsKey(task.getId())) {
            linkLast(task);
            removeNode(history.get(task.getId()));
        } else {
            linkLast(task);
        }
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

    //класс объектов двусвязного списка истории
    static class Node<E> {
        public E task;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E task, Node<E> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;

        }
    }

    private Node tail;

    //метод добавления ноды в конец списка
    void linkLast(Task task) {

        final Node oldTail = tail;
        final Node newNode = new Node(tail, task, null);
        tail = newNode;

        if (oldTail == null) {
            tail = newNode;
        } else {
            oldTail.next = newNode;
        }

        history.put(task.getId(), newNode);
    }


    //метод удаления ноды
    void removeNode(Node node) {
        final Node prev = node.prev;
        final Node next = node.next;

        if (prev == null) {
            next.prev = null;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            prev.next = null;
        } else {
            next.prev = prev;
            node.next = null;
        }
    }

    //возвращает список задач из двусвязного списка
    public List<Task> getTask() {
        List<Task> list = new ArrayList<>();

        for (Node<Task> node : history.values()) {
            list.add(node.task);
        }

        return list;
    }
}


