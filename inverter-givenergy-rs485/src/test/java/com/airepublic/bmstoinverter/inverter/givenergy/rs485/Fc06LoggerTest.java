package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class Fc06LoggerTest {

    @Test
    public void testLogAppendsOneLine(@TempDir Path tmp) throws Exception {
        Path file = tmp.resolve("fc06.log");
        Fc06Logger logger = new Fc06Logger(file);
        logger.log(1, 0x000C, 0x00FF);
        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("device=1"), lines.get(0));
        assertTrue(lines.get(0).contains("reg=0x000C"), lines.get(0));
        assertTrue(lines.get(0).contains("val=0x00FF"), lines.get(0));
    }

    @Test
    public void testMultipleLogsAppendNotOverwrite(@TempDir Path tmp) throws Exception {
        Path file = tmp.resolve("fc06.log");
        Fc06Logger logger = new Fc06Logger(file);
        logger.log(1, 0x000C, 0x00FF);
        logger.log(1, 0x000D, 0x0001);
        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
    }
}
