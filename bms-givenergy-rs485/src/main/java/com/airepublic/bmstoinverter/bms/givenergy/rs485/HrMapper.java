package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.HoldingRegisters;

/** Maps a 56-byte FC=3 holding-register response payload into a {@link BatteryPack}. */
public final class HrMapper {

    private HrMapper() {}

    public static void apply(byte[] data, BatteryPack pack) {
        if (data == null || data.length != 56) {
            throw new IllegalArgumentException("HR payload must be 56 bytes, got " + (data == null ? -1 : data.length));
        }
        int hr0 = beU16(data, 0);
        if (hr0 != HoldingRegisters.HR0_DEVICE_MARKER) {
            throw new IllegalArgumentException(String.format("HR0 device marker mismatch: expected 0x%04X, got 0x%04X",
                HoldingRegisters.HR0_DEVICE_MARKER, hr0));
        }
        int socPct       = beU16(data, 21 * 2);
        int voltage_cV   = beU16(data, 22 * 2);
        int current_cA   = beS16(data, 23 * 2);
        int tempMax_C    = beU16(data, 24 * 2);
        int maxCharge_cA = beU16(data, 25 * 2);
        int maxDisch_cA  = beU16(data, 27 * 2);

        pack.packSOC = socPct * 10;          // wire % -> 0.1%
        pack.packVoltage = voltage_cV / 10;  // 0.01 V -> 0.1 V
        pack.packCurrent = current_cA / 10;  // 0.01 A -> 0.1 A
        pack.tempMax = tempMax_C * 10;       // wire C -> 0.1 C
        pack.maxPackChargeCurrent = maxCharge_cA / 10;
        pack.maxPackDischargeCurrent = -(maxDisch_cA / 10); // framework expects negative magnitude
    }

    private static int beU16(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    private static int beS16(byte[] data, int offset) {
        return (short) beU16(data, offset);
    }
}
