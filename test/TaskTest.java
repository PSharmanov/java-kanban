import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldBeEqualsTaskIfTheirIdEquals() {
        Task task1 = new Task("Задача 1", "Описание задачи1", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание задачи2", Status.IN_PROGRESS);
        task2.setId(1);

        assertEquals(task1, task2);
    }
}