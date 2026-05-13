package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.InputRegisters;

/** Maps a 38-byte IR Block 2 response payload into a {@link BatteryPack}. */
public final class IrBlock2Mapper {

    private IrBlock2Mapper() {}

    public static void apply(byte[] data, BatteryPack pack) {
        if (data == null || data.length != InputRegisters.BLOCK2_BYTES) {
            throw new IllegalArgumentException("Block 2 payload must be " + InputRegisters.BLOCK2_BYTES + " bytes, got " + (data == null ? -1 : data.length));
        }
        pack.numberOfCells = data[0] & 0xFF;
        pack.bmsCycles = beU16(data, 1);

        int rated_cAh = beU16(data, 15);
        int design_cAh = beU16(data, 19);
        int remain_cAh = beU16(data, 23);

        pack.ratedCapacitymAh = rated_cAh * 100;
        pack.remainingCapacitymAh = remain_cAh * 100;

        // SoC byte at offset 25 (% direct) is intentionally not mapped here -- HrMapper (HR21) is canonical.
        pack.softwareVersion = String.format("%04X", beU16(data, 35));

        if (design_cAh > 0) {
            pack.packSOH = (rated_cAh * 1000) / design_cAh;
            if (pack.packSOH > 1000) pack.packSOH = 1000;
        } else {
            pack.packSOH = 0;
        }
    }

    private static int beU16(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}
