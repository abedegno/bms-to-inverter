package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CRC16Test {

    /** Canonical Modbus example: {0x02, 0x07} CRC = 0x1241 (low=0x41, high=0x12). */
    @Test
    public void testKnownModbusVector() {
        byte[] data = { 0x02, 0x07 };
        int crc = GivEnergyModbus.crc16(data);
        assertEquals(0x1241, crc);
    }

    /** Real GivEnergy HR poll request: {0x01, 0x03, 0x00, 0x00, 0x00, 0x1C} -> CRC 0x0344 (wire: 0x44 0x03). */
    @Test
    public void testRealHrPollRequest() {
        byte[] data = { 0x01, 0x03, 0x00, 0x00, 0x00, 0x1C };
        int crc = GivEnergyModbus.crc16(data);
        assertEquals(0x0344, crc);
    }

    /** Real GivEnergy IR Block 2 poll: {0x01, 0x04, 0x00, 0x15, 0x00, 0x13} -> CRC 0x03A0 (wire: 0xA0 0x03). */
    @Test
    public void testRealIrBlock2PollRequest() {
        byte[] data = { 0x01, 0x04, 0x00, 0x15, 0x00, 0x13 };
        int crc = GivEnergyModbus.crc16(data);
        assertEquals(0x03A0, crc);
    }

    @Test
    public void testEmptyInputProducesInitialValue() {
        assertEquals(0xFFFF, GivEnergyModbus.crc16(new byte[0]));
    }
}
