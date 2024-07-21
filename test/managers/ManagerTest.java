package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import managers.Manager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    void shouldReturnNotNullGetDefaultTaskManager () {
        TaskManager manager = Manager.getDefaultTaskManager();
        assertNotNull(manager);
    }

    @Test
    void shouldReturnNotNullGetDefaultHistory() {
        HistoryManager manager = Manager.getDefaultHistory();
        assertNotNull(manager);
    }
}