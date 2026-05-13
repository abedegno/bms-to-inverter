package com.airepublic.bmstoinverter.protocol.givenergy;

/** Encoding helpers for the GivEnergy LV BMS wire format. */
public final class PackEncoding {

    /** Bias used by the firmware's {@code subw r1, r1, #0xAAA} encoding. */
    public static final int BIAS_2730 = 2730;

    private PackEncoding() {}

    /** Subtract 2730 from a value. The result is what the firmware writes on the wire (signed). */
    public static int encode2730(int value) {
        return value - BIAS_2730;
    }

    /** Add 2730 to a (signed) wire value. The result is the decoded semantic value. */
    public static int decode2730(int wireValue) {
        return wireValue + BIAS_2730;
    }
}
