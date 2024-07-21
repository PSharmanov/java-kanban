package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldBeEqualsEpicIfTheirIdEquals() {
        Epic epic1 = new Epic("Эпик1", "Описание эпик1");
        epic1.setId(1);
        Epic epic2 = new Epic("Эпик2", "Описание эпик2");
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }


}