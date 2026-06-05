package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncodeFC3Test {

    @Test
    public void testFc3Response_singleRegister() {
        // device=1, FC=3, one register = 0x1234
        // Expected: 01 03 02 12 34 [crc-lo crc-hi]
        int[] regs = { 0x1234 };
        byte[] frame = GivEnergyModbus.encodeFC3Response(1, regs);
        assertEquals(7, frame.length);
        assertEquals((byte) 0x01, frame[0]);
        assertEquals((byte) 0x03, frame[1]);
        assertEquals((byte) 0x02, frame[2]); // byte count
        assertEquals((byte) 0x12, frame[3]);
        assertEquals((byte) 0x34, frame[4]);
        // CRC is verifiable by re-running crc16 over the first 5 bytes:
        int expectedCrc = GivEnergyModbus.crc16(frame, 0, 5);
        int actualCrc = ((frame[6] & 0xFF) << 8) | (frame[5] & 0xFF);
        assertEquals(expectedCrc, actualCrc);
    }

    @Test
    public void testFc3Response_fullHrTable_sizeAndHeader() {
        int[] regs = new int[28];
        regs[0]  = 0x0065;
        regs[13] = 0x0BCE;
        byte[] frame = GivEnergyModbus.encodeFC3Response(1, regs);
        // 1 + 1 + 1 + 56 + 2 = 61 bytes total
        assertEquals(61, frame.length);
        assertEquals(0x38, frame[2] & 0xFF); // byte count = 56
        assertEquals(0x00, frame[3]); // HR0 high byte
        assertEquals(0x65, frame[4]); // HR0 low byte
        assertEquals(0x0B, frame[3 + 13 * 2] & 0xFF); // HR13 high byte
        assertEquals(0xCE, frame[3 + 13 * 2 + 1] & 0xFF); // HR13 low byte
    }

    @Test
    public void testFc3Response_crcRoundTrip() {
        int[] regs = new int[28];
        regs[0] = 0x0065;
        byte[] frame = GivEnergyModbus.encodeFC3Response(1, regs);
        int expectedCrc = GivEnergyModbus.crc16(frame, 0, frame.length - 2);
        int actualCrc = ((frame[frame.length - 1] & 0xFF) << 8) | (frame[frame.length - 2] & 0xFF);
        assertEquals(expectedCrc, actualCrc);
    }
}
