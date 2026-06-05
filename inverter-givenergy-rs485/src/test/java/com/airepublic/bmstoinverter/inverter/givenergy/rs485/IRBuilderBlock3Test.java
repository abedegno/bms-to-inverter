package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class IRBuilderBlock3Test {

    private static int beU16(byte[] b, int o) {
        return ((b[o] & 0xFF) << 8) | (b[o+1] & 0xFF);
    }
    private static int beS16(byte[] b, int o) {
        return (short) beU16(b, o);
    }

    private static BatteryPack packWithCells(int[] mv) {
        BatteryPack p = new BatteryPack();
        for (int i = 0; i < mv.length; i++) p.cellVmV[i] = mv[i];
        p.numberOfCells = mv.length;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int v : mv) { if (v < min) min = v; if (v > max) max = v; }
        p.minCellmV = min;
        p.maxCellmV = max;
        return p;
    }

    @Test
    public void testSixteenCells() {
        int[] mv = new int[16];
        for (int i = 0; i < 16; i++) mv[i] = 3300 + i;
        byte[] b = IRBuilder.buildBlock3(packWithCells(mv));
        assertEquals(40, b.length);
        for (int i = 0; i < 16; i++) {
            assertEquals(3300 + i, beU16(b, i * 2), "cell " + i);
        }
    }

    @Test
    public void testMaxMinAndEncoded() {
        int[] mv = new int[16];
        for (int i = 0; i < 16; i++) mv[i] = 3300 + i;
        byte[] b = IRBuilder.buildBlock3(packWithCells(mv));
        assertEquals(3315 - 2730, beS16(b, 32));
        assertEquals(3300 - 2730, beS16(b, 34));
        assertEquals(3315, beU16(b, 36));
        assertEquals(3300, beU16(b, 38));
    }

    @Test
    public void testFewerThan16Cells_unusedSlotsZero() {
        int[] mv = new int[8];
        for (int i = 0; i < 8; i++) mv[i] = 3300 + i;
        byte[] b = IRBuilder.buildBlock3(packWithCells(mv));
        for (int i = 0; i < 8; i++) assertEquals(3300 + i, beU16(b, i * 2));
        for (int i = 8; i < 16; i++) assertEquals(0, beU16(b, i * 2));
    }

    @Test
    public void testCellVoltageClamping() {
        int[] mv = new int[16];
        for (int i = 0; i < 16; i++) mv[i] = 3300;
        mv[0] = 5000;
        mv[15] = 1000;
        byte[] b = IRBuilder.buildBlock3(packWithCells(mv));
        assertEquals(3700, beU16(b, 0));
        assertEquals(2200, beU16(b, 30));
    }
}
