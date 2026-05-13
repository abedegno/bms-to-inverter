package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class IRBuilderBlock1Test {

    private static int beU16(byte[] b, int o) {
        return ((b[o] & 0xFF) << 8) | (b[o+1] & 0xFF);
    }
    private static int beS16(byte[] b, int o) {
        return (short) beU16(b, o);
    }

    private static BatteryPack syntheticPack() {
        BatteryPack p = new BatteryPack();
        p.manufacturerCode = "BAT12345";
        p.tempMax = 250;
        p.tempMin = 180;
        return p;
    }

    @Test
    public void testSizeAndSerialPadding() {
        byte[] b = IRBuilder.buildBlock1(syntheticPack());
        assertEquals(42, b.length);
        assertEquals('B', b[0]);
        assertEquals('5', b[7]);
        assertEquals(0, b[19]);
    }

    @Test
    public void testFiveTemperatureSlots() {
        byte[] b = IRBuilder.buildBlock1(syntheticPack());
        assertEquals(250, beS16(b, 22));
        assertEquals(250, beS16(b, 24));
        assertEquals(250, beS16(b, 26));
        assertEquals(180, beS16(b, 28));
        assertEquals(215, beS16(b, 30));
    }

    @Test
    public void testFixedTrailer() {
        byte[] b = IRBuilder.buildBlock1(syntheticPack());
        assertEquals(0x0001, beU16(b, 32));
        assertEquals(0x0008, beU16(b, 34));
        for (int i = 36; i < 42; i++) assertEquals(0, b[i]);
    }

    @Test
    public void testNegativeTemperature_wireSigned() {
        BatteryPack p = syntheticPack();
        p.tempMax = -50;
        p.tempMin = -100;
        byte[] b = IRBuilder.buildBlock1(p);
        assertEquals(-50, beS16(b, 22));
        assertEquals(-100, beS16(b, 28));
        assertEquals(-75, beS16(b, 30));
    }
}
