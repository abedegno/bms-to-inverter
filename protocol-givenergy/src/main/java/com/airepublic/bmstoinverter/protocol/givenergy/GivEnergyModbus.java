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

    /**
     * Parse an inverter-to-BMS request frame (always 8 bytes).
     * Throws {@link IllegalArgumentException} on bad length or bad CRC.
     */
    public static GivEnergyFrame parseRequest(byte[] frame) {
        if (frame == null || frame.length != 8) {
            throw new IllegalArgumentException("request frame must be 8 bytes, got " + (frame == null ? -1 : frame.length));
        }
        int wireCrc = ((frame[7] & 0xFF) << 8) | (frame[6] & 0xFF);
        int computed = crc16(frame, 0, 6);
        if (wireCrc != computed) {
            throw new IllegalArgumentException(String.format("bad CRC: wire=0x%04X computed=0x%04X", wireCrc, computed));
        }
        int device = frame[0] & 0xFF;
        int fc     = frame[1] & 0xFF;
        int addr   = ((frame[2] & 0xFF) << 8) | (frame[3] & 0xFF);
        int cv     = ((frame[4] & 0xFF) << 8) | (frame[5] & 0xFF);
        return new GivEnergyFrame(device, fc, addr, cv, null, wireCrc);
    }
}
