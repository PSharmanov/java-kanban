import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> history = new ArrayList<>();
    private final static int MAX_SIZE_HISTORY = 10;

    //метод добавления задач в список
    @Override
    public void addHistory(Task task) {
        if (history.size() >= MAX_SIZE_HISTORY) {
            history.remove(0);
        }

        history.add(task);

    }

    //метод возвращает список последник 10 просмотреных задач
    @Override
    public List<Task> getHistory() {
        return history;
    }
}


