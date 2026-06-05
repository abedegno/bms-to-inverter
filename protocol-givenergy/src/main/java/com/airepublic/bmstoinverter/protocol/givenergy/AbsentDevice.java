package com.airepublic.bmstoinverter.protocol.givenergy;

/** Block 1/2/3 absent-device response sentinels (see docs/03 in giv-bms-analysis). */
public final class AbsentDevice {

    private AbsentDevice() {}

    private static final byte SENT_HI = (byte) 0xF5;
    private static final byte SENT_LO = (byte) 0x56;

    /** Block 1 absent-device pattern: zero serial, five F5 56 at the temperature positions, zero trailer. */
    public static byte[] block1() {
        byte[] b = new byte[InputRegisters.BLOCK1_BYTES];
        for (int i = 0; i < 5; i++) {
            int off = 22 + i * 2;
            b[off]     = SENT_HI;
            b[off + 1] = SENT_LO;
        }
        return b;
    }

    /** Block 2 absent-device pattern: all zero. */
    public static byte[] block2() {
        return new byte[InputRegisters.BLOCK2_BYTES];
    }

    /** Block 3 absent-device pattern: cells zero, F5 56 at offsets 32 and 34, max/min zero. */
    public static byte[] block3() {
        byte[] b = new byte[InputRegisters.BLOCK3_BYTES];
        b[32] = SENT_HI; b[33] = SENT_LO;
        b[34] = SENT_HI; b[35] = SENT_LO;
        return b;
    }
}
