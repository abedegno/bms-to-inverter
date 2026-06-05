package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class EncodeRequestTest {

    @Test
    public void testEncodeFc3Request_hrPoll() {
        byte[] expected = { 0x01, 0x03, 0x00, 0x00, 0x00, 0x1C, 0x44, 0x03 };
        byte[] actual = GivEnergyModbus.encodeFC3Request(1, 0x0000, 0x001C);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testEncodeFc4Request_block2Poll() {
        byte[] expected = { 0x01, 0x04, 0x00, 0x15, 0x00, 0x13, (byte) 0xA0, 0x03 };
        byte[] actual = GivEnergyModbus.encodeFC4Request(1, 0x0015, 0x0013);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testEncodeFc4Request_block1Poll() {
        // From docs/06 captures: 01 04 00 00 00 15
        byte[] expected = { 0x01, 0x04, 0x00, 0x00, 0x00, 0x15, 0x31, (byte) 0xC5 };
        byte[] actual = GivEnergyModbus.encodeFC4Request(1, 0x0000, 0x0015);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testEncodeFc4Request_block3Poll() {
        // From docs/06 captures: 01 04 00 28 00 14
        byte[] expected = { 0x01, 0x04, 0x00, 0x28, 0x00, 0x14, 0x70, 0x0D };
        byte[] actual = GivEnergyModbus.encodeFC4Request(1, 0x0028, 0x0014);
        assertArrayEquals(expected, actual);
    }
}
