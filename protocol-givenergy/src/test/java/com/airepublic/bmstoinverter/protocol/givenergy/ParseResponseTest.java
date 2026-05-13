package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParseResponseTest {

    @Test
    public void testParseFc3Response_extractsRegisters() {
        // Build a response and parse it back.
        int[] regs = new int[28];
        regs[0] = 0x0065;
        regs[13] = 0x0BCE;
        byte[] frame = GivEnergyModbus.encodeFC3Response(1, regs);
        GivEnergyFrame parsed = GivEnergyModbus.parseResponse(frame, 3, 0x0000);
        assertEquals(1, parsed.getDeviceAddress());
        assertEquals(3, parsed.getFunctionCode());
        assertEquals(56, parsed.getData().length);
        // HR0 BE
        assertEquals(0x00, parsed.getData()[0] & 0xFF);
        assertEquals(0x65, parsed.getData()[1] & 0xFF);
        // HR13 BE
        assertEquals(0x0B, parsed.getData()[26] & 0xFF);
        assertEquals(0xCE, parsed.getData()[27] & 0xFF);
    }

    @Test
    public void testParseFc4Response_addrEchoValidated() {
        byte[] data = new byte[38];
        data[0] = 0x10;
        byte[] frame = GivEnergyModbus.encodeFC4Response(1, 0x0015, data);
        GivEnergyFrame parsed = GivEnergyModbus.parseResponse(frame, 4, 0x0015);
        assertEquals(4, parsed.getFunctionCode());
        assertEquals(0x0015, parsed.getAddress());
        assertEquals(38, parsed.getData().length);
        assertEquals(0x10, parsed.getData()[0] & 0xFF);
    }

    @Test
    public void testParseFc4Response_wrongAddrRejected() {
        byte[] data = new byte[38];
        byte[] frame = GivEnergyModbus.encodeFC4Response(1, 0x0015, data);
        assertThrows(IllegalArgumentException.class,
            () -> GivEnergyModbus.parseResponse(frame, 4, 0x0000));
    }

    @Test
    public void testParseResponse_wrongFcRejected() {
        int[] regs = new int[28];
        byte[] frame = GivEnergyModbus.encodeFC3Response(1, regs);
        assertThrows(IllegalArgumentException.class,
            () -> GivEnergyModbus.parseResponse(frame, 4, 0x0000));
    }

    @Test
    public void testParseResponse_badCrcRejected() {
        int[] regs = new int[28];
        byte[] frame = GivEnergyModbus.encodeFC3Response(1, regs);
        frame[frame.length - 1] ^= 0xFF; // corrupt CRC
        assertThrows(IllegalArgumentException.class,
            () -> GivEnergyModbus.parseResponse(frame, 3, 0x0000));
    }

    @Test
    public void testParseResponse_modbusExceptionDetected() {
        // FC=4 exception: [device=1, 0x84, code=2, crc-lo, crc-hi]
        byte[] body = { 0x01, (byte) 0x84, 0x02 };
        int crc = GivEnergyModbus.crc16(body);
        byte[] frame = new byte[5];
        System.arraycopy(body, 0, frame, 0, 3);
        frame[3] = (byte) (crc & 0xFF);
        frame[4] = (byte) ((crc >> 8) & 0xFF);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> GivEnergyModbus.parseResponse(frame, 4, 0x0000));
        assertTrue(ex.getMessage().contains("exception"), ex.getMessage());
    }
}
