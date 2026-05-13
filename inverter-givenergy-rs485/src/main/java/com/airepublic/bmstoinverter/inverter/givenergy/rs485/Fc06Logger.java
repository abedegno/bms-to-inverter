package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

/** Appends one line per FC=06 write the emulator receives from the inverter. */
public final class Fc06Logger {

    private final Path file;

    public Fc06Logger(Path file) {
        this.file = file;
    }

    public void log(int deviceAddress, int register, int value) throws IOException {
        String line = String.format("%s\tdevice=%d\treg=0x%04X\tval=0x%04X%n",
            Instant.now().toString(), deviceAddress, register, value);
        Files.write(file, line.getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
