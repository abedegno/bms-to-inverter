package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class HRBuilderTest {

    private static BatteryPack syntheticPack() {
        BatteryPack p = new BatteryPack();
        p.packSOC = 935;                    // 93.5%
        p.packVoltage = 532;                // 53.2 V (0.1 V)
        p.packCurrent = 149;                // 14.9 A charge
        p.tempMax = 250;                    // 25.0 C
        p.maxPackChargeCurrent = 600;       // 60.0 A
        p.maxPackDischargeCurrent = -600;   // -60.0 A (framework negative)
        p.ratedCapacitymAh = 186000;        // 186 Ah
        p.manufacturerCode = "BAT12345";
        return p;
    }

    @Test
    public void testFixedConstants() {
        int[] hr = HRBuilder.build(syntheticPack(), 0);
        assertEquals(28, hr.length);
        assertEquals(0x0065, hr[0]);
        assertEquals(0xFFFF, hr[1]);
        assertEquals(0xFFFF, hr[2]);
        assertEquals(0xFFFF, hr[3]);
        assertEquals(0xFFFF, hr[4]);
        assertEquals(0xFFFF, hr[10]);
        assertEquals(0x0030, hr[12]);
        assertEquals(0x0BCE, hr[13]);
    }

    @Test
    public void testCoreElectricalFields() {
        int[] hr = HRBuilder.build(syntheticPack(), 0);
        assertEquals(93, hr[21]);
        assertEquals(5320, hr[22]);
        assertEquals(1490, hr[23]);
        assertEquals(25, hr[24]);
        assertEquals(6000, hr[25]);
        assertEquals(6000, hr[27]);
    }

    @Test
    public void testDischarging_signedCurrent() {
        BatteryPack p = syntheticPack();
        p.packCurrent = -149;
        int[] hr = HRBuilder.build(p, 0);
        // -1490 in u16 BE = 0xFA2E
        assertEquals(0xFA2E, hr[23]);
    }

    @Test
    public void testTotalAh_fromRatedCapacity() {
        int[] hr = HRBuilder.build(syntheticPack(), 0);
        assertEquals(186, hr[11]);
    }

    @Test
    public void testTickCounter_storedAtHR17() {
        int[] hr1 = HRBuilder.build(syntheticPack(), 0x114B);
        int[] hr2 = HRBuilder.build(syntheticPack(), 0x114C);
        assertEquals(0x114B, hr1[17]);
        assertEquals(0x114C, hr2[17]);
    }

    @Test
    public void testHR18_stableHashOfManufacturerCode() {
        int[] hr1 = HRBuilder.build(syntheticPack(), 0);
        int[] hr2 = HRBuilder.build(syntheticPack(), 0);
        assertEquals(hr1[18], hr2[18]);
        BatteryPack other = syntheticPack();
        other.manufacturerCode = "DIFFERENT";
        int[] hr3 = HRBuilder.build(other, 0);
        assertNotEquals(hr1[18], hr3[18]);
    }

    @Test
    public void testHR15_bit0_setWhenSocAt100() {
        BatteryPack p = syntheticPack();
        p.packSOC = 1000;
        int[] hr = HRBuilder.build(p, 0);
        assertEquals(1, hr[15] & 1);
        p.packSOC = 999;
        int[] hr2 = HRBuilder.build(p, 0);
        assertEquals(0, hr2[15] & 1);
    }

    @Test
    public void testTempClamping_outOfRange() {
        BatteryPack p = syntheticPack();
        p.tempMax = 800;
        int[] hr = HRBuilder.build(p, 0);
        assertEquals(70, hr[24]);
    }

    @Test
    public void testPackCurrentClamping() {
        BatteryPack p = syntheticPack();
        p.packCurrent = 5000;
        int[] hr = HRBuilder.build(p, 0);
        assertEquals(32000, hr[23]);
    }
}
