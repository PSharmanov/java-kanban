package interfaces;

import java.util.List;
import models.Task;


public interface HistoryManager {
    void addHistory(Task task);

    void remove(int id);

    List<Task> getHistory();
}
