package com.airepublic.bmstoinverter.protocol.givenergy;

/** IR Block 1/2/3 layout constants for the GivEnergy LV BMS. */
public final class InputRegisters {

    private InputRegisters() {}

    /** Block 1 start address (FC=4 read). */
    public static final int BLOCK1_START = 0x0000;
    /** Block 1 register count. */
    public static final int BLOCK1_COUNT = 21;
    /** Block 1 payload size in bytes (count * 2). */
    public static final int BLOCK1_BYTES = 42;

    /** Block 2 start address (FC=4 read). */
    public static final int BLOCK2_START = 0x0015;
    /** Block 2 register count. */
    public static final int BLOCK2_COUNT = 19;
    /** Block 2 payload size in bytes (count * 2). */
    public static final int BLOCK2_BYTES = 38;

    /** Block 3 start address (FC=4 read). */
    public static final int BLOCK3_START = 0x0028;
    /** Block 3 register count. */
    public static final int BLOCK3_COUNT = 20;
    /** Block 3 payload size in bytes (count * 2). */
    public static final int BLOCK3_BYTES = 40;
}
