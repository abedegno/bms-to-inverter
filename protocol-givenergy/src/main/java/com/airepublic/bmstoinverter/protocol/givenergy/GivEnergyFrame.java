package com.airepublic.bmstoinverter.protocol.givenergy;

import java.util.Arrays;

/** Immutable representation of a parsed/built GivEnergy Modbus frame. */
public final class GivEnergyFrame {

    private final int deviceAddress;
    private final int functionCode;
    private final int address;
    private final int countOrValue;
    private final byte[] data;     // may be null
    private final int crc;

    public GivEnergyFrame(int deviceAddress, int functionCode, int address, int countOrValue, byte[] data, int crc) {
        this.deviceAddress = deviceAddress;
        this.functionCode = functionCode;
        this.address = address;
        this.countOrValue = countOrValue;
        this.data = data == null ? null : data.clone();
        this.crc = crc;
    }

    public int getDeviceAddress() { return deviceAddress; }
    public int getFunctionCode() { return functionCode; }
    public int getAddress() { return address; }
    public int getCountOrValue() { return countOrValue; }
    public byte[] getData() { return data == null ? null : data.clone(); }
    public int getCrc() { return crc; }

    @Override
    public String toString() {
        return String.format("GivEnergyFrame{device=%d, fc=%d, addr=0x%04X, count=0x%04X, dataLen=%d, crc=0x%04X}",
            deviceAddress, functionCode, address, countOrValue,
            data == null ? 0 : data.length, crc);
    }
}
