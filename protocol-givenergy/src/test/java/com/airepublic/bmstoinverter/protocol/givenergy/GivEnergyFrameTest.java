package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GivEnergyFrameTest {

    @Test
    public void testConstructor_populatesAllFields() {
        byte[] data = { 0x01, 0x02 };
        GivEnergyFrame f = new GivEnergyFrame(1, 3, 0x0000, 0x001C, data, 0x0344);
        assertEquals(1, f.getDeviceAddress());
        assertEquals(3, f.getFunctionCode());
        assertEquals(0x0000, f.getAddress());
        assertEquals(0x001C, f.getCountOrValue());
        assertArrayEquals(data, f.getData());
        assertEquals(0x0344, f.getCrc());
    }

    @Test
    public void testToString_isHumanReadable() {
        GivEnergyFrame f = new GivEnergyFrame(1, 3, 0x0000, 0x001C, null, 0x0344);
        String s = f.toString();
        assertTrue(s.contains("device=1"), s);
        assertTrue(s.contains("fc=3"), s);
        assertTrue(s.contains("addr=0x0000"), s);
    }

    @Test
    public void testGetData_returnsCopy() {
        byte[] original = { 0x01, 0x02, 0x03 };
        GivEnergyFrame f = new GivEnergyFrame(1, 3, 0, 0, original, 0);
        byte[] returned = f.getData();
        returned[0] = (byte) 0xFF;
        assertEquals(0x01, f.getData()[0]); // internal data not mutated
    }
}
