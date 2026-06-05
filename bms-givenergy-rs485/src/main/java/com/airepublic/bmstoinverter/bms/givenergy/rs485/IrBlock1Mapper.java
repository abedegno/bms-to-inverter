package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.InputRegisters;

/** Maps a 42-byte IR Block 1 response payload into a {@link BatteryPack}. */
public final class IrBlock1Mapper {

    private IrBlock1Mapper() {}

    private static final int ABSENT_TEMP_SIGNED = -2730;

    public static void apply(byte[] data, BatteryPack pack) {
        if (data == null || data.length != InputRegisters.BLOCK1_BYTES) {
            throw new IllegalArgumentException("Block 1 payload must be " + InputRegisters.BLOCK1_BYTES + " bytes, got " + (data == null ? -1 : data.length));
        }

        // Serial: bytes 0..19 ASCII padded with spaces, NUL-terminated. Trim trailing spaces and NUL.
        int end = 0;
        while (end < 20 && data[end] != 0) end++;
        StringBuilder sb = new StringBuilder(end);
        for (int i = 0; i < end; i++) sb.append((char) (data[i] & 0xFF));
        pack.manufacturerCode = sb.toString().trim();

        // 5 temps at offsets 22..31, BE16 signed decidegC
        int[] temps = new int[5];
        int absentCount = 0;
        for (int i = 0; i < 5; i++) {
            temps[i] = beS16(data, 22 + i * 2);
            if (temps[i] == ABSENT_TEMP_SIGNED) absentCount++;
        }
        if (absentCount == 5) {
            pack.numberOfCells = 0; // absent slot; framework will filter
            return;
        }

        int min = Integer.MAX_VALUE, sum = 0;
        for (int t : temps) {
            if (t < min) min = t;
            sum += t;
        }
        pack.tempMin = min;
        // tempMax is owned by HrMapper (HR24) -- the BMS's authoritative max-cell-temp value.
        pack.tempAverage = sum / 5;
    }

    private static int beS16(byte[] data, int offset) {
        return (short) (((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF));
    }
}
