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
package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airepublic.bmstoinverter.core.Inverter;
import com.airepublic.bmstoinverter.core.Port;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyFrame;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyModbus;
import com.airepublic.bmstoinverter.protocol.givenergy.InputRegisters;

/**
 * Emulator: responds to a real GivEnergy inverter's polls. Builds HR(0..27) and IR Block 1/2/3
 * responses from the framework's aggregated {@link BatteryPack}; echoes (and logs) FC=06 writes.
 */
public class GivEnergyInverterRS485Processor extends Inverter {
    private static final Logger LOG = LoggerFactory.getLogger(GivEnergyInverterRS485Processor.class);

    private int hr17Counter = 0;
    private final Fc06Logger fc06Logger = new Fc06Logger(Paths.get("givenergy-fc06.log"));

    @Override
    protected ByteBuffer readRequest(final Port port) throws IOException {
        return port.receiveFrame();
    }

    @Override
    protected void sendFrame(final Port port, final ByteBuffer frame) throws IOException {
        port.sendFrame(frame);
    }

    @Override
    protected List<ByteBuffer> createSendFrames(final ByteBuffer requestFrame, final BatteryPack aggregatedPack) {
        final List<ByteBuffer> result = new ArrayList<>();
        if (requestFrame == null) {
            return result; // no request -> no response
        }
        byte[] reqBytes = toBytes(requestFrame);

        GivEnergyFrame req;
        try {
            req = GivEnergyModbus.parseRequest(reqBytes);
        } catch (IllegalArgumentException e) {
            LOG.warn("Bad request frame: {}", e.getMessage());
            return result; // do not respond -- bus will time out
        }

        byte[] response;
        int deviceAddr = req.getDeviceAddress();
        int fc = req.getFunctionCode();
        try {
            switch (fc) {
                case 3:
                    response = GivEnergyModbus.encodeFC3Response(deviceAddr, HRBuilder.build(aggregatedPack, hr17Counter));
                    hr17Counter = (hr17Counter + 1) & 0xFFFF;
                    break;
                case 4:
                    response = buildIrResponse(deviceAddr, req.getAddress(), req.getCountOrValue(), aggregatedPack);
                    break;
                case 6:
                    try {
                        fc06Logger.log(deviceAddr, req.getAddress(), req.getCountOrValue());
                    } catch (IOException e) {
                        LOG.warn("Fc06 logger failed: {}", e.getMessage());
                    }
                    response = GivEnergyModbus.encodeFC6Echo(reqBytes);
                    break;
                default:
                    response = GivEnergyModbus.encodeException(deviceAddr, fc, 1); // illegal function
                    break;
            }
        } catch (RuntimeException e) {
            LOG.warn("Response build failed: {}", e.getMessage());
            response = GivEnergyModbus.encodeException(deviceAddr, fc, 4); // server failure
        }

        result.add(ByteBuffer.wrap(response));
        return result;
    }

    private byte[] buildIrResponse(int deviceAddr, int startAddr, int count, BatteryPack pack) {
        byte[] data;
        if (startAddr == InputRegisters.BLOCK1_START && count == InputRegisters.BLOCK1_COUNT) {
            data = IRBuilder.buildBlock1(pack);
        } else if (startAddr == InputRegisters.BLOCK2_START && count == InputRegisters.BLOCK2_COUNT) {
            data = IRBuilder.buildBlock2(pack);
        } else if (startAddr == InputRegisters.BLOCK3_START && count == InputRegisters.BLOCK3_COUNT) {
            data = IRBuilder.buildBlock3(pack);
        } else {
            return GivEnergyModbus.encodeException(deviceAddr, 4, 2); // illegal data address
        }
        return GivEnergyModbus.encodeFC4Response(deviceAddr, startAddr, data);
    }

    private static byte[] toBytes(ByteBuffer buf) {
        byte[] out = new byte[buf.remaining()];
        int pos = buf.position();
        buf.get(out);
        buf.position(pos);
        return out;
    }
}
