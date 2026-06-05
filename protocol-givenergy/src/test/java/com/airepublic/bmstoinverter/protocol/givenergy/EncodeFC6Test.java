package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class EncodeFC6Test {

    @Test
    public void testFc6Echo_isExactCopyOfRequest() {
        byte[] body = { 0x01, 0x06, 0x00, 0x0C, 0x00, (byte) 0xFF };
        int crc = GivEnergyModbus.crc16(body);
        byte[] request = new byte[8];
        System.arraycopy(body, 0, request, 0, 6);
        request[6] = (byte) (crc & 0xFF);
        request[7] = (byte) ((crc >> 8) & 0xFF);

        byte[] echo = GivEnergyModbus.encodeFC6Echo(request);
        assertArrayEquals(request, echo);
    }

    @Test
    public void testFc6Echo_returnsCopyNotSameArray() {
        byte[] body = { 0x01, 0x06, 0x00, 0x00, 0x00, 0x01 };
        int crc = GivEnergyModbus.crc16(body);
        byte[] request = new byte[8];
        System.arraycopy(body, 0, request, 0, 6);
        request[6] = (byte) (crc & 0xFF);
        request[7] = (byte) ((crc >> 8) & 0xFF);

        byte[] echo = GivEnergyModbus.encodeFC6Echo(request);
        request[0] = (byte) 0xFF; // mutate original
        org.junit.jupiter.api.Assertions.assertEquals((byte) 0x01, echo[0]); // echo unaffected
    }
}
