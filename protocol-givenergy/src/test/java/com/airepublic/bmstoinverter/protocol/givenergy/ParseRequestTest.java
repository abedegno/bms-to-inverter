package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParseRequestTest {

    @Test
    public void testParseHrPoll() {
        byte[] frame = { 0x01, 0x03, 0x00, 0x00, 0x00, 0x1C, 0x44, 0x03 };
        GivEnergyFrame f = GivEnergyModbus.parseRequest(frame);
        assertEquals(1, f.getDeviceAddress());
        assertEquals(3, f.getFunctionCode());
        assertEquals(0x0000, f.getAddress());
        assertEquals(0x001C, f.getCountOrValue());
        assertEquals(0x0344, f.getCrc());
    }

    @Test
    public void testParseIrBlock2Poll() {
        byte[] frame = { 0x01, 0x04, 0x00, 0x15, 0x00, 0x13, (byte) 0xA0, 0x03 };
        GivEnergyFrame f = GivEnergyModbus.parseRequest(frame);
        assertEquals(1, f.getDeviceAddress());
        assertEquals(4, f.getFunctionCode());
        assertEquals(0x0015, f.getAddress());
        assertEquals(0x0013, f.getCountOrValue());
    }

    @Test
    public void testParseFc6Write() {
        // FC=6: address=0x000C, value=0x00FF, CRC computed (placeholder; real CRC computed in helper)
        byte[] body = { 0x01, 0x06, 0x00, 0x0C, 0x00, (byte) 0xFF };
        int crc = GivEnergyModbus.crc16(body);
        byte[] frame = new byte[8];
        System.arraycopy(body, 0, frame, 0, 6);
        frame[6] = (byte) (crc & 0xFF);
        frame[7] = (byte) ((crc >> 8) & 0xFF);
        GivEnergyFrame f = GivEnergyModbus.parseRequest(frame);
        assertEquals(6, f.getFunctionCode());
        assertEquals(0x000C, f.getAddress());
        assertEquals(0x00FF, f.getCountOrValue());
    }

    @Test
    public void testParse_wrongLengthRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> GivEnergyModbus.parseRequest(new byte[] { 0x01, 0x03 }));
    }

    @Test
    public void testParse_badCrcRejected() {
        byte[] frame = { 0x01, 0x03, 0x00, 0x00, 0x00, 0x1C, 0x00, 0x00 };
        assertThrows(IllegalArgumentException.class,
            () -> GivEnergyModbus.parseRequest(frame));
    }
}
