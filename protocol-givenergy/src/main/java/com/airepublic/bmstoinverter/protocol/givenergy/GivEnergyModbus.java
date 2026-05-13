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

    private static byte[] encodeRequest(int deviceAddress, int functionCode, int startAddress, int count) {
        byte[] out = new byte[8];
        out[0] = (byte) deviceAddress;
        out[1] = (byte) functionCode;
        out[2] = (byte) ((startAddress >> 8) & 0xFF);
        out[3] = (byte) (startAddress & 0xFF);
        out[4] = (byte) ((count >> 8) & 0xFF);
        out[5] = (byte) (count & 0xFF);
        int crc = crc16(out, 0, 6);
        out[6] = (byte) (crc & 0xFF);
        out[7] = (byte) ((crc >> 8) & 0xFF);
        return out;
    }

    /** Encode an outgoing FC=3 (read holding registers) request. */
    public static byte[] encodeFC3Request(int deviceAddress, int startRegister, int count) {
        return encodeRequest(deviceAddress, 3, startRegister, count);
    }

    /** Encode an outgoing FC=4 (read input registers) request. */
    public static byte[] encodeFC4Request(int deviceAddress, int startRegister, int count) {
        return encodeRequest(deviceAddress, 4, startRegister, count);
    }

    /** Encode an FC=3 (read holding registers) response. Standard Modbus framing with byte_count. */
    public static byte[] encodeFC3Response(int deviceAddress, int[] registers) {
        int byteCount = registers.length * 2;
        byte[] out = new byte[3 + byteCount + 2];
        out[0] = (byte) deviceAddress;
        out[1] = (byte) 3;
        out[2] = (byte) byteCount;
        for (int i = 0; i < registers.length; i++) {
            out[3 + i * 2]     = (byte) ((registers[i] >> 8) & 0xFF);
            out[3 + i * 2 + 1] = (byte) (registers[i] & 0xFF);
        }
        int crc = crc16(out, 0, out.length - 2);
        out[out.length - 2] = (byte) (crc & 0xFF);
        out[out.length - 1] = (byte) ((crc >> 8) & 0xFF);
        return out;
    }
}
