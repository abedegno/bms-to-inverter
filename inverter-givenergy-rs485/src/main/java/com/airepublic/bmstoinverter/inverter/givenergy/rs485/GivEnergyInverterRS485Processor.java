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
import java.util.List;

import com.airepublic.bmstoinverter.core.Inverter;
import com.airepublic.bmstoinverter.core.Port;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;

/** Emulator: GivEnergy BMS toward an inverter. (Placeholder; Task 9 fills in.) */
public class GivEnergyInverterRS485Processor extends Inverter {

    @Override
    protected ByteBuffer readRequest(final Port port) throws IOException {
        throw new UnsupportedOperationException("Task 9 implements this");
    }

    @Override
    protected void sendFrame(final Port port, final ByteBuffer frame) throws IOException {
        throw new UnsupportedOperationException("Task 9 implements this");
    }

    @Override
    protected List<ByteBuffer> createSendFrames(final ByteBuffer requestFrame, final BatteryPack aggregatedPack) {
        throw new UnsupportedOperationException("Task 9 implements this");
    }
}
