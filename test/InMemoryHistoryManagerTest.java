import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    static HistoryManager historyManager;
    static List<Task> listOne;

    @BeforeEach
    void setUp() {
        historyManager = Manager.getDefaultHistory();
        Task task1 = new Task("Задача 1 ", "Задача 1", Status.NEW);
        Task task2 = new Task("Задача 2 ", "Задача 2", Status.NEW);
        Task task3 = new Task("Задача 3 ", "Задача 3", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        listOne = List.of(task1, task2, task3);
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
    }

    @Test
    void addHistory() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");

    }


    @Test
    void removeHistoryByIdTask() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая.");

    }

    @Test
    void shouldGetHistory() {
        assertEquals(listOne.get(0), historyManager.getHistory().get(0));
        assertEquals(listOne.get(1), historyManager.getHistory().get(1));
        assertEquals(listOne.get(2), historyManager.getHistory().get(2));

    }

    @Test
    void shouldTrueHistoryIsEmpty() {
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);
        assertTrue(historyManager.getHistory().isEmpty());
    }
}