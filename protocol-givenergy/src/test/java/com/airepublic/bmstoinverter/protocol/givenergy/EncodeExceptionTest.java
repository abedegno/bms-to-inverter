package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncodeExceptionTest {

    @Test
    public void testException_illegalFunction() {
        // device=1, FC=5 (unsupported), code=1 (illegal function)
        byte[] frame = GivEnergyModbus.encodeException(1, 5, 1);
        assertEquals(5, frame.length);
        assertEquals((byte) 0x01, frame[0]);
        assertEquals(0x85, frame[1] & 0xFF);
        assertEquals((byte) 0x01, frame[2]);
        int crc = GivEnergyModbus.crc16(frame, 0, 3);
        assertEquals((byte) (crc & 0xFF),        frame[3]);
        assertEquals((byte) ((crc >> 8) & 0xFF), frame[4]);
    }

    @Test
    public void testException_fc3IllegalDataAddress() {
        byte[] frame = GivEnergyModbus.encodeException(1, 3, 2);
        assertEquals(0x83, frame[1] & 0xFF);
        assertEquals((byte) 0x02, frame[2]);
    }
}
