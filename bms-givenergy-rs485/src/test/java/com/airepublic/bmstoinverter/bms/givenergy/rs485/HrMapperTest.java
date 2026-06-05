package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class HrMapperTest {

    /** Build a 56-byte HR response payload from 28 BE16 register values. */
    private static byte[] hrBytes(int[] regs) {
        if (regs.length != 28) throw new IllegalArgumentException();
        byte[] out = new byte[56];
        for (int i = 0; i < 28; i++) {
            out[i * 2] = (byte) ((regs[i] >> 8) & 0xFF);
            out[i * 2 + 1] = (byte) (regs[i] & 0xFF);
        }
        return out;
    }

    @Test
    public void testMapsCoreElectricalFields() {
        int[] regs = new int[28];
        regs[0]  = 0x0065;
        regs[21] = 935;    // 93.5% SoC (wire %)
        regs[22] = 5320;   // 53.20 V (wire 0.01 V)
        regs[23] = 1490;   // 14.90 A (wire 0.01 A)
        regs[24] = 25;     // 25 C max cell temp (wire C)
        regs[25] = 6000;   // 60.00 A max charge limit (wire 0.01 A)
        regs[27] = 6000;   // 60.00 A discharge limit
        BatteryPack pack = new BatteryPack();
        HrMapper.apply(hrBytes(regs), pack);
        assertEquals(935 * 10, pack.packSOC);              // 9350 = 93.5% in 0.1% units
        assertEquals(532, pack.packVoltage);               // 53.2 V in 0.1 V units
        assertEquals(149, pack.packCurrent);               // 14.9 A in 0.1 A units
        assertEquals(250, pack.tempMax);                   // 25.0 C in 0.1 C units
        assertEquals(600, pack.maxPackChargeCurrent);      // 60.0 A in 0.1 A units
        assertEquals(-600, pack.maxPackDischargeCurrent);  // negative magnitude per framework
    }

    @Test
    public void testSignedCurrent_discharging() {
        int[] regs = new int[28];
        regs[0] = 0x0065;
        regs[23] = 0xFA24; // -1500 in s16 (negative current = discharge); /10 = -150 deci-A
        BatteryPack pack = new BatteryPack();
        HrMapper.apply(hrBytes(regs), pack);
        assertEquals(-150, pack.packCurrent);
    }

    @Test
    public void testHr0DeviceMarker_validated() {
        int[] regs = new int[28];
        regs[0] = 0x0065;
        BatteryPack pack = new BatteryPack();
        HrMapper.apply(hrBytes(regs), pack); // does not throw

        int[] bad = new int[28];
        bad[0] = 0x0000;
        BatteryPack pack2 = new BatteryPack();
        assertThrows(IllegalArgumentException.class, () -> HrMapper.apply(hrBytes(bad), pack2));
    }

    @Test
    public void testWrongLengthRejected() {
        assertThrows(IllegalArgumentException.class, () -> HrMapper.apply(new byte[10], new BatteryPack()));
    }
}
