package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.InputRegisters;

/** Maps a 40-byte IR Block 3 response payload into a {@link BatteryPack}. */
public final class IrBlock3Mapper {

    private IrBlock3Mapper() {}

    public static void apply(byte[] data, BatteryPack pack) {
        if (data == null || data.length != InputRegisters.BLOCK3_BYTES) {
            throw new IllegalArgumentException("Block 3 payload must be " + InputRegisters.BLOCK3_BYTES + " bytes, got " + (data == null ? -1 : data.length));
        }
        for (int i = 0; i < 16; i++) {
            pack.cellVmV[i] = beU16(data, i * 2);
        }
        pack.maxCellmV = beU16(data, 36);
        pack.minCellmV = beU16(data, 38);
        pack.cellDiffmV = pack.maxCellmV - pack.minCellmV;
    }

    private static int beU16(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}
