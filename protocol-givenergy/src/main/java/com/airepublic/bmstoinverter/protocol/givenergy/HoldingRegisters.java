package com.airepublic.bmstoinverter.protocol.givenergy;

/** HR(0..27) layout constants for the GivEnergy LV BMS. */
public final class HoldingRegisters {

    private HoldingRegisters() {}

    /** Number of HR registers in the full poll. */
    public static final int COUNT = 28;

    /** FC=3 full-table poll start address. */
    public static final int FULL_POLL_START = 0x0000;

    /** FC=3 full-table poll register count (28 -> 0x1C). */
    public static final int FULL_POLL_COUNT = 0x001C;

    /** Fixed device-marker constant emitted at HR(0). */
    public static final int HR0_DEVICE_MARKER = 0x0065;

    /** Hardware-rev constant emitted at HR(12). */
    public static final int HR12_HW_REV = 0x0030;

    /** Firmware version constant for the 3022 firmware. */
    public static final int HR13_FW_VERSION_3022 = 0x0BCE;
}
