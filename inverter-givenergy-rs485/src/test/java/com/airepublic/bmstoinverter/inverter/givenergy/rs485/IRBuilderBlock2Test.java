package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class IRBuilderBlock2Test {

    private static int beU16(byte[] b, int o) {
        return ((b[o] & 0xFF) << 8) | (b[o+1] & 0xFF);
    }

    private static BatteryPack syntheticPack() {
        BatteryPack p = new BatteryPack();
        p.numberOfCells = 16;
        p.bmsCycles = 50;
        p.packVoltage = 530;
        p.ratedCapacitymAh = 186000;
        p.remainingCapacitymAh = 175000;
        p.packSOC = 940;
        return p;
    }

    @Test
    public void testHeaderFields() {
        byte[] b = IRBuilder.buildBlock2(syntheticPack());
        assertEquals(38, b.length);
        assertEquals(16, b[0] & 0xFF);
        assertEquals(50, beU16(b, 1));
    }

    @Test
    public void testVoltages() {
        byte[] b = IRBuilder.buildBlock2(syntheticPack());
        assertEquals(53000, beU16(b, 5));
        assertEquals(53000, beU16(b, 7));
    }

    @Test
    public void testCapacities() {
        byte[] b = IRBuilder.buildBlock2(syntheticPack());
        assertEquals(1860, beU16(b, 15));
        assertEquals(1860, beU16(b, 19));
        assertEquals(1750, beU16(b, 23));
    }

    @Test
    public void testSocAndFirmware() {
        byte[] b = IRBuilder.buildBlock2(syntheticPack());
        assertEquals(94, b[25] & 0xFF);
        assertEquals(0x0BCE, beU16(b, 35));
    }

    @Test
    public void testObservedPattern() {
        byte[] b = IRBuilder.buildBlock2(syntheticPack());
        assertEquals((byte) 0xFF, b[9]);
        assertEquals((byte) 0xFF, b[10]);
        assertEquals((byte) 0xFF, b[11]);
        assertEquals((byte) 0x35, b[12]);
        assertEquals(0, b[13]);
        assertEquals(0, b[14]);
        assertEquals(0x0E10, beU16(b, 28));
    }
}
