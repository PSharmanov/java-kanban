import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void shouldBeEqualsSubTaskIfTheirIdEquals() {
        Epic epic1 = new Epic("Эпик1","Описание эпик1");
        epic1.setId(1);
        SubTask subTask1 = new SubTask("Подзадача1", "Описание подзадачи1",Status.NEW,epic1);
        subTask1.setId(1);
        SubTask subTask2 = new SubTask("Подзадача2", "Описание подзадачи2",Status.DONE,epic1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2);
    }


}