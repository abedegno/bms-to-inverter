package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbsentDeviceTest {

    @Test
    public void testBlock1_size() {
        assertEquals(InputRegisters.BLOCK1_BYTES, AbsentDevice.block1().length);
    }

    @Test
    public void testBlock1_hasFiveF556AtTempPositions() {
        byte[] b = AbsentDevice.block1();
        // Bytes 22..31 are five BE 0xF556
        for (int i = 0; i < 5; i++) {
            int off = 22 + i * 2;
            int v = ((b[off] & 0xFF) << 8) | (b[off + 1] & 0xFF);
            assertEquals(0xF556, v, "temp slot " + i);
        }
    }

    @Test
    public void testBlock1_serialIsAllZero() {
        byte[] b = AbsentDevice.block1();
        for (int i = 0; i < 20; i++) {
            assertEquals(0, b[i], "serial byte " + i);
        }
    }

    @Test
    public void testBlock2_size() {
        assertEquals(InputRegisters.BLOCK2_BYTES, AbsentDevice.block2().length);
    }

    @Test
    public void testBlock2_allZero() {
        byte[] b = AbsentDevice.block2();
        for (int i = 0; i < b.length; i++) {
            assertEquals(0, b[i]);
        }
    }

    @Test
    public void testBlock3_size() {
        assertEquals(InputRegisters.BLOCK3_BYTES, AbsentDevice.block3().length);
    }

    @Test
    public void testBlock3_hasF556AtOffsets32And34() {
        byte[] b = AbsentDevice.block3();
        int v32 = ((b[32] & 0xFF) << 8) | (b[33] & 0xFF);
        int v34 = ((b[34] & 0xFF) << 8) | (b[35] & 0xFF);
        assertEquals(0xF556, v32);
        assertEquals(0xF556, v34);
    }

    @Test
    public void testBlock3_cellsAndMaxMinAreZero() {
        byte[] b = AbsentDevice.block3();
        // Cells 0..31
        for (int i = 0; i < 32; i++) assertEquals(0, b[i]);
        // Max / min at 36..39
        for (int i = 36; i < 40; i++) assertEquals(0, b[i]);
    }
}
