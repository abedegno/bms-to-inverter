package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncodeFC4Test {

    @Test
    public void testFc4Response_block2_addrEchoNotByteCount() {
        // Block 2: start=0x0015, count=19 -> data 38 bytes
        byte[] data = new byte[38];
        data[0] = 0x10; // cellCount
        byte[] frame = GivEnergyModbus.encodeFC4Response(1, 0x0015, data);
        // Total: 1 + 1 + 2 + 38 + 2 = 44 bytes
        assertEquals(44, frame.length);
        assertEquals((byte) 0x01, frame[0]);
        assertEquals((byte) 0x04, frame[1]);
        // KEY: byte 2 is the high byte of the addr echo (0x00), NOT 0x26 (byte count = 38)
        assertEquals((byte) 0x00, frame[2]);
        assertEquals((byte) 0x15, frame[3]);
        assertEquals((byte) 0x10, frame[4]);
    }

    @Test
    public void testFc4Response_block1() {
        byte[] data = new byte[42];
        byte[] frame = GivEnergyModbus.encodeFC4Response(1, 0x0000, data);
        assertEquals(48, frame.length); // 1+1+2+42+2
        assertEquals((byte) 0x00, frame[2]);
        assertEquals((byte) 0x00, frame[3]);
    }

    @Test
    public void testFc4Response_block3() {
        byte[] data = new byte[40];
        byte[] frame = GivEnergyModbus.encodeFC4Response(1, 0x0028, data);
        assertEquals(46, frame.length); // 1+1+2+40+2
        assertEquals((byte) 0x00, frame[2]);
        assertEquals((byte) 0x28, frame[3]);
    }

    @Test
    public void testFc4Response_crcCoversAllPrecedingBytes() {
        byte[] data = new byte[40];
        byte[] frame = GivEnergyModbus.encodeFC4Response(1, 0x0028, data);
        int expectedCrc = GivEnergyModbus.crc16(frame, 0, frame.length - 2);
        int actualCrc = ((frame[frame.length - 1] & 0xFF) << 8) | (frame[frame.length - 2] & 0xFF);
        assertEquals(expectedCrc, actualCrc);
    }
}
