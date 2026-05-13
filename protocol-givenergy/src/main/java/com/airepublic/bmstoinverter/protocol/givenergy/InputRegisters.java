package com.airepublic.bmstoinverter.protocol.givenergy;

/** IR Block 1/2/3 layout constants for the GivEnergy LV BMS. */
public final class InputRegisters {

    private InputRegisters() {}

    public static final int BLOCK1_START = 0x0000;
    public static final int BLOCK1_COUNT = 21;
    public static final int BLOCK1_BYTES = 42;

    public static final int BLOCK2_START = 0x0015;
    public static final int BLOCK2_COUNT = 19;
    public static final int BLOCK2_BYTES = 38;

    public static final int BLOCK3_START = 0x0028;
    public static final int BLOCK3_COUNT = 20;
    public static final int BLOCK3_BYTES = 40;
}
