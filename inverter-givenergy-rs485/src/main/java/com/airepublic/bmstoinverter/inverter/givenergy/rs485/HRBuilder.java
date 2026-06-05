package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import java.nio.charset.StandardCharsets;

import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyModbus;
import com.airepublic.bmstoinverter.protocol.givenergy.HoldingRegisters;
import com.airepublic.bmstoinverter.protocol.givenergy.PackEncoding;

/** Builds the 28-register HR array from a {@link BatteryPack}. */
public final class HRBuilder {

    private HRBuilder() {}

    private static final int CURRENT_CLAMP = 32000; // 0.01 A wire
    private static final int TEMP_MIN_C = -30;
    private static final int TEMP_MAX_C = 70;

    public static int[] build(BatteryPack pack, int hr17Counter) {
        int[] hr = new int[HoldingRegisters.COUNT];

        hr[0] = HoldingRegisters.HR0_DEVICE_MARKER;
        hr[1] = hr[2] = hr[3] = hr[4] = 0xFFFF;

        byte[] serial = PackEncoding.padAsciiSerial(safeString(pack.manufacturerCode), 20);
        for (int i = 0; i < 5; i++) {
            hr[5 + i] = ((serial[i * 2] & 0xFF) << 8) | (serial[i * 2 + 1] & 0xFF);
        }

        hr[10] = 0xFFFF;
        hr[11] = pack.ratedCapacitymAh / 1000;
        hr[12] = HoldingRegisters.HR12_HW_REV;
        hr[13] = HoldingRegisters.HR13_FW_VERSION_3022;
        hr[14] = 0x0000;
        hr[15] = (pack.packSOC >= 1000) ? 1 : 0;
        hr[16] = 0x0000;
        hr[17] = hr17Counter & 0xFFFF;
        hr[18] = GivEnergyModbus.crc16(safeString(pack.manufacturerCode).getBytes(StandardCharsets.US_ASCII));

        int hr19 = 0;
        if (pack.packCurrent == 0)      hr19 |= 0x01;
        else if (pack.packCurrent > 0)  hr19 |= 0x02;
        else                             hr19 |= 0x03;
        hr19 |= 0x04; // not-inhibit
        hr19 |= 0x08; // no fault default
        hr[19] = hr19;

        hr[20] = 0x0000;

        hr[21] = pack.packSOC / 10;
        hr[22] = pack.packVoltage * 10;

        int current_cA = pack.packCurrent * 10;
        if (current_cA > CURRENT_CLAMP)  current_cA = CURRENT_CLAMP;
        if (current_cA < -CURRENT_CLAMP) current_cA = -CURRENT_CLAMP;
        hr[23] = current_cA & 0xFFFF;

        int tempMax_C = pack.tempMax / 10;
        if (tempMax_C < TEMP_MIN_C) tempMax_C = TEMP_MIN_C;
        if (tempMax_C > TEMP_MAX_C) tempMax_C = TEMP_MAX_C;
        hr[24] = tempMax_C & 0xFFFF;

        hr[25] = pack.maxPackChargeCurrent * 10;
        hr[26] = pack.maxPackChargeCurrent * 10;
        hr[27] = Math.abs(pack.maxPackDischargeCurrent) * 10;

        return hr;
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }
}
