/**
 * This software is free to use and to distribute in its unchanged form for private use.
 * Commercial use is prohibited without an explicit license agreement of the copyright holder.
 * Any changes to this software must be made solely in the project repository at https://github.com/ai-republic/bms-to-inverter.
 * The copyright holder is not liable for any damages in whatever form that may occur by using this software.
 *
 * (c) Copyright 2022 and onwards - Torsten Oltmanns
 *
 * @author Torsten Oltmanns - bms-to-inverter''AT''gmail.com
 */
package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airepublic.bmstoinverter.core.BMS;
import com.airepublic.bmstoinverter.core.NoDataAvailableException;
import com.airepublic.bmstoinverter.core.Port;
import com.airepublic.bmstoinverter.core.TooManyInvalidFramesException;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyFrame;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyModbus;
import com.airepublic.bmstoinverter.protocol.givenergy.HoldingRegisters;
import com.airepublic.bmstoinverter.protocol.givenergy.InputRegisters;

/**
 * BMS reader for GivEnergy LV batteries over RS485.
 *
 * <p>Polls device 1 with FC=3 (full HR table) every call; rotates FC=4 IR Block 1/2/3 across
 * configured device addresses (default 5) every 10th call. Populates the framework's
 * {@link BatteryPack} via mapper helpers.
 */
public class GivEnergyBmsRS485Processor extends BMS {
    private final static Logger LOG = LoggerFactory.getLogger(GivEnergyBmsRS485Processor.class);

    /** Default: poll 5 paralleled devices, IR every 10th call. */
    private final PollScheduler scheduler = new PollScheduler(5, 10);

    /** Expected HR response length: 1 (device) + 1 (FC) + 1 (byteCount) + 56 (data) + 2 (CRC) = 61. */
    private static final int HR_RESPONSE_BYTES = 61;

    /** Expected FC=4 response length: 1 + 1 + 2 (addr_echo) + data + 2 (CRC). */
    private static int fc4ResponseBytes(int dataBytes) {
        return 4 + dataBytes + 2;
    }

    @Override
    protected void collectData(final Port port) throws TooManyInvalidFramesException, NoDataAvailableException, IOException {
        if (!(port instanceof GivEnergyModbusPort)) {
            throw new IllegalStateException("GivEnergyBmsRS485Processor requires a GivEnergyModbusPort; got " + port.getClass().getName());
        }
        GivEnergyModbusPort gePort = (GivEnergyModbusPort) port;

        doHrPoll(gePort);

        PollScheduler.IrSlot slot = scheduler.nextIrSlot();
        if (slot != null) {
            doIrPoll(gePort, slot);
        }
    }

    private void doHrPoll(GivEnergyModbusPort port) throws TooManyInvalidFramesException, NoDataAvailableException, IOException {
        byte[] request = GivEnergyModbus.encodeFC3Request(1, HoldingRegisters.FULL_POLL_START, HoldingRegisters.FULL_POLL_COUNT);
        port.setExpectedResponseLength(HR_RESPONSE_BYTES);
        port.sendFrame(ByteBuffer.wrap(request));
        ByteBuffer rx = port.receiveFrame();
        if (rx == null) {
            throw new NoDataAvailableException();
        }
        byte[] response = toBytes(rx);
        GivEnergyFrame parsed;
        try {
            parsed = GivEnergyModbus.parseResponse(response, 3, HoldingRegisters.FULL_POLL_START);
        } catch (IllegalArgumentException e) {
            LOG.warn("HR poll parse failed: {}", e.getMessage());
            throw new TooManyInvalidFramesException();
        }
        BatteryPack pack = getBatteryPack(0);
        try {
            HrMapper.apply(parsed.getData(), pack);
        } catch (IllegalArgumentException e) {
            LOG.warn("HR map failed: {}", e.getMessage());
            throw new TooManyInvalidFramesException();
        }
    }

    private void doIrPoll(GivEnergyModbusPort port, PollScheduler.IrSlot slot) throws IOException {
        int start, count, expectedDataBytes;
        switch (slot.blockNumber) {
            case 1: start = InputRegisters.BLOCK1_START; count = InputRegisters.BLOCK1_COUNT; expectedDataBytes = InputRegisters.BLOCK1_BYTES; break;
            case 2: start = InputRegisters.BLOCK2_START; count = InputRegisters.BLOCK2_COUNT; expectedDataBytes = InputRegisters.BLOCK2_BYTES; break;
            case 3: start = InputRegisters.BLOCK3_START; count = InputRegisters.BLOCK3_COUNT; expectedDataBytes = InputRegisters.BLOCK3_BYTES; break;
            default: throw new IllegalStateException("unknown block " + slot.blockNumber);
        }
        byte[] request = GivEnergyModbus.encodeFC4Request(slot.deviceAddress, start, count);
        port.setExpectedResponseLength(fc4ResponseBytes(expectedDataBytes));
        port.sendFrame(ByteBuffer.wrap(request));
        ByteBuffer rx = port.receiveFrame();
        if (rx == null) {
            // Absent device or transient -- tolerate
            LOG.debug("IR Block {} poll for device {} timed out (absent device?)", slot.blockNumber, slot.deviceAddress);
            return;
        }
        byte[] response = toBytes(rx);
        GivEnergyFrame parsed;
        try {
            parsed = GivEnergyModbus.parseResponse(response, 4, start);
        } catch (IllegalArgumentException e) {
            LOG.debug("IR Block {} poll for device {} parse failed: {}", slot.blockNumber, slot.deviceAddress, e.getMessage());
            return;
        }
        if (parsed.getData().length != expectedDataBytes) {
            LOG.warn("IR Block {} response length mismatch: expected {}, got {}", slot.blockNumber, expectedDataBytes, parsed.getData().length);
            return;
        }
        BatteryPack pack = getBatteryPack(slot.deviceAddress - 1);
        try {
            switch (slot.blockNumber) {
                case 1: IrBlock1Mapper.apply(parsed.getData(), pack); break;
                case 2: IrBlock2Mapper.apply(parsed.getData(), pack); break;
                case 3: IrBlock3Mapper.apply(parsed.getData(), pack); break;
                default: throw new IllegalStateException();
            }
        } catch (IllegalArgumentException e) {
            LOG.debug("IR Block {} mapper failed for device {}: {}", slot.blockNumber, slot.deviceAddress, e.getMessage());
            // tolerate single-block failure; pack stays in last-known-good state
        }
    }

    private static byte[] toBytes(ByteBuffer buf) {
        byte[] out = new byte[buf.remaining()];
        buf.get(out);
        return out;
    }
}
