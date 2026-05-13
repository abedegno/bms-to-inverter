package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.InputRegisters;
import com.airepublic.bmstoinverter.protocol.givenergy.PackEncoding;

/** Builds IR Block 1/2/3 byte arrays from a {@link BatteryPack}. */
public final class IRBuilder {

    private IRBuilder() {}

    private static final int DESIGN_CAPACITY_AH = 186;

    /** Block 1: 42 bytes. Serial + 5 raw decidegC temperatures + fixed trailer. */
    public static byte[] buildBlock1(BatteryPack pack) {
        byte[] out = new byte[InputRegisters.BLOCK1_BYTES];

        byte[] serial = PackEncoding.padAsciiSerial(safeString(pack.manufacturerCode), 20);
        System.arraycopy(serial, 0, out, 0, 20);

        int tempMax = pack.tempMax;
        int tempMin = pack.tempMin;
        int tempAvg = (tempMax + tempMin) / 2;

        putBE16Signed(out, 22, tempMax);
        putBE16Signed(out, 24, tempMax);
        putBE16Signed(out, 26, tempMax);
        putBE16Signed(out, 28, tempMin);
        putBE16Signed(out, 30, tempAvg);

        out[32] = 0x00; out[33] = 0x01;
        out[34] = 0x00; out[35] = 0x08;

        return out;
    }

    /** Block 2: 38 bytes. */
    public static byte[] buildBlock2(BatteryPack pack) {
        byte[] out = new byte[InputRegisters.BLOCK2_BYTES];

        out[0] = (byte) (pack.numberOfCells & 0xFF);
        putBE16(out, 1, pack.bmsCycles);

        int packV_mV = pack.packVoltage * 100;
        putBE16(out, 5, packV_mV);
        putBE16(out, 7, packV_mV);

        out[9] = (byte) 0xFF;
        out[10] = (byte) 0xFF;
        out[11] = (byte) 0xFF;
        out[12] = (byte) 0x35;

        putBE16(out, 15, pack.ratedCapacitymAh / 100);
        putBE16(out, 19, DESIGN_CAPACITY_AH * 10);
        putBE16(out, 23, pack.remainingCapacitymAh / 100);

        out[25] = (byte) (pack.packSOC / 10);

        putBE16(out, 28, 0x0E10);
        putBE16(out, 35, 0x0BCE);

        return out;
    }

    private static void putBE16Signed(byte[] out, int offset, int value) {
        out[offset]     = (byte) ((value >> 8) & 0xFF);
        out[offset + 1] = (byte) (value & 0xFF);
    }

    private static void putBE16(byte[] out, int offset, int value) {
        out[offset]     = (byte) ((value >> 8) & 0xFF);
        out[offset + 1] = (byte) (value & 0xFF);
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }
}
