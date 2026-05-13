package com.airepublic.bmstoinverter.protocol.givenergy;

/** GivEnergy LV BMS Modbus codec. Static utility class. */
public final class GivEnergyModbus {

    private GivEnergyModbus() {}

    /** Modbus CRC-16, polynomial 0xA001, init 0xFFFF. Returns 16-bit value (low byte goes first on the wire). */
    public static int crc16(byte[] data) {
        return crc16(data, 0, data.length);
    }

    public static int crc16(byte[] data, int offset, int length) {
        int crc = 0xFFFF;
        for (int i = offset; i < offset + length; i++) {
            crc ^= (data[i] & 0xFF);
            for (int b = 0; b < 8; b++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ 0xA001;
                } else {
                    crc = crc >>> 1;
                }
            }
        }
        return crc & 0xFFFF;
    }
}
