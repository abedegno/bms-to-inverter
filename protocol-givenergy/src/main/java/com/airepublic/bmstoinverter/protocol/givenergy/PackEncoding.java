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

    /**
     * Pad an ASCII string to a fixed byte width with spaces; the last byte is always NUL.
     * If the input is longer than {@code width - 1}, it is truncated.
     */
    public static byte[] padAsciiSerial(String serial, int width) {
        byte[] out = new byte[width];
        int copy = Math.min(serial.length(), width - 1);
        for (int i = 0; i < copy; i++) {
            char c = serial.charAt(i);
            out[i] = (byte) (c <= 0x7F ? c : '?');
        }
        for (int i = copy; i < width - 1; i++) {
            out[i] = ' ';
        }
        out[width - 1] = 0;
        return out;
    }
}
