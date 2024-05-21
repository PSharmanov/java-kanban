import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addHistory() {
        HistoryManager historyManager = Manager.getDefaultHistory();
        historyManager.addHistory(new Task("Задача 1 ", "Задача 1", Status.NEW));
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

    }

    @Test
    void maxSizeHistory() {
        HistoryManager manager = Manager.getDefaultHistory();

        for (int i = 0; i < 15; i++) {
            manager.addHistory(new Task());
        }

        assertEquals(10, manager.getHistory().size());
    }


}