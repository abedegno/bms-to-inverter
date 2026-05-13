package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyFrame;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyModbus;
import com.airepublic.bmstoinverter.protocol.givenergy.HoldingRegisters;
import com.airepublic.bmstoinverter.protocol.givenergy.InputRegisters;
import static org.junit.jupiter.api.Assertions.*;

public class GivEnergyInverterRS485ProcessorTest {

    private static BatteryPack syntheticPack() {
        BatteryPack p = new BatteryPack();
        p.packSOC = 935;
        p.packVoltage = 532;
        p.packCurrent = 149;
        p.tempMax = 250;
        p.tempMin = 230;
        p.maxPackChargeCurrent = 600;
        p.maxPackDischargeCurrent = -600;
        p.ratedCapacitymAh = 186000;
        p.remainingCapacitymAh = 175000;
        p.numberOfCells = 16;
        for (int i = 0; i < 16; i++) p.cellVmV[i] = 3300 + i;
        p.maxCellmV = 3315;
        p.minCellmV = 3300;
        p.manufacturerCode = "BAT12345";
        p.bmsCycles = 50;
        return p;
    }

    @Test
    public void testFc3HrPoll_buildsValidResponse() {
        GivEnergyInverterRS485Processor proc = new GivEnergyInverterRS485Processor();
        byte[] request = GivEnergyModbus.encodeFC3Request(1, HoldingRegisters.FULL_POLL_START, HoldingRegisters.FULL_POLL_COUNT);
        List<ByteBuffer> frames = proc.createSendFrames(ByteBuffer.wrap(request), syntheticPack());
        assertEquals(1, frames.size());
        byte[] resp = toBytes(frames.get(0));
        GivEnergyFrame parsed = GivEnergyModbus.parseResponse(resp, 3, 0x0000);
        assertEquals(56, parsed.getData().length);
        assertEquals(0x00, parsed.getData()[0]);
        assertEquals(0x65, parsed.getData()[1]);
    }

    @Test
    public void testFc4Block2Poll_buildsAddrEchoResponse() {
        GivEnergyInverterRS485Processor proc = new GivEnergyInverterRS485Processor();
        byte[] request = GivEnergyModbus.encodeFC4Request(1, InputRegisters.BLOCK2_START, InputRegisters.BLOCK2_COUNT);
        List<ByteBuffer> frames = proc.createSendFrames(ByteBuffer.wrap(request), syntheticPack());
        byte[] resp = toBytes(frames.get(0));
        GivEnergyFrame parsed = GivEnergyModbus.parseResponse(resp, 4, InputRegisters.BLOCK2_START);
        assertEquals(InputRegisters.BLOCK2_BYTES, parsed.getData().length);
        assertEquals(16, parsed.getData()[0] & 0xFF);
    }

    @Test
    public void testFc6_isEchoed() {
        GivEnergyInverterRS485Processor proc = new GivEnergyInverterRS485Processor();
        byte[] body = { 0x01, 0x06, 0x00, 0x10, 0x00, 0x01 };
        int crc = GivEnergyModbus.crc16(body);
        byte[] request = new byte[8];
        System.arraycopy(body, 0, request, 0, 6);
        request[6] = (byte) (crc & 0xFF);
        request[7] = (byte) ((crc >> 8) & 0xFF);
        List<ByteBuffer> frames = proc.createSendFrames(ByteBuffer.wrap(request), syntheticPack());
        assertEquals(1, frames.size());
        byte[] resp = toBytes(frames.get(0));
        assertArrayEquals(request, resp);
    }

    @Test
    public void testUnsupportedFc_returnsException() {
        GivEnergyInverterRS485Processor proc = new GivEnergyInverterRS485Processor();
        byte[] body = { 0x01, 0x05, 0x00, 0x00, 0x00, 0x00 };
        int crc = GivEnergyModbus.crc16(body);
        byte[] request = new byte[8];
        System.arraycopy(body, 0, request, 0, 6);
        request[6] = (byte) (crc & 0xFF);
        request[7] = (byte) ((crc >> 8) & 0xFF);
        List<ByteBuffer> frames = proc.createSendFrames(ByteBuffer.wrap(request), syntheticPack());
        byte[] resp = toBytes(frames.get(0));
        assertEquals(5, resp.length);
        assertEquals(0x85, resp[1] & 0xFF);
        assertEquals(0x01, resp[2] & 0xFF);
    }

    @Test
    public void testNullRequest_returnsEmptyList() {
        GivEnergyInverterRS485Processor proc = new GivEnergyInverterRS485Processor();
        List<ByteBuffer> frames = proc.createSendFrames(null, syntheticPack());
        assertTrue(frames.isEmpty());
    }

    private static byte[] toBytes(ByteBuffer buf) {
        byte[] out = new byte[buf.remaining()];
        int pos = buf.position();
        buf.get(out);
        buf.position(pos);
        return out;
    }
}
