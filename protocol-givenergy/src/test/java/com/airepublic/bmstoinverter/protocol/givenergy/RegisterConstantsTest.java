package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterConstantsTest {

    @Test
    public void testHrLayout() {
        assertEquals(28, HoldingRegisters.COUNT);
        assertEquals(0x0000, HoldingRegisters.FULL_POLL_START);
        assertEquals(0x001C, HoldingRegisters.FULL_POLL_COUNT);
        assertEquals(0x0065, HoldingRegisters.HR0_DEVICE_MARKER);
        assertEquals(0x0030, HoldingRegisters.HR12_HW_REV);
        assertEquals(0x0BCE, HoldingRegisters.HR13_FW_VERSION_3022);
    }

    @Test
    public void testIrBlockLayouts() {
        assertEquals(0x0000, InputRegisters.BLOCK1_START);
        assertEquals(21,     InputRegisters.BLOCK1_COUNT);
        assertEquals(42,     InputRegisters.BLOCK1_BYTES);

        assertEquals(0x0015, InputRegisters.BLOCK2_START);
        assertEquals(19,     InputRegisters.BLOCK2_COUNT);
        assertEquals(38,     InputRegisters.BLOCK2_BYTES);

        assertEquals(0x0028, InputRegisters.BLOCK3_START);
        assertEquals(20,     InputRegisters.BLOCK3_COUNT);
        assertEquals(40,     InputRegisters.BLOCK3_BYTES);
    }
}
