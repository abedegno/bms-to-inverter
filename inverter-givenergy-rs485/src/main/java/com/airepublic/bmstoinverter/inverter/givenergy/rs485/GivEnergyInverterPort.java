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

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airepublic.bmstoinverter.core.protocol.rs485.FrameDefinition;
import com.airepublic.bmstoinverter.protocol.rs485.JSerialCommPort;
import com.fazecast.jSerialComm.SerialPort;

/**
 * RS485 port for the inverter-side GivEnergy Modbus dialect.
 * Every inverter-to-BMS request is exactly 8 bytes (FC=3/4/6 all use the same format),
 * so {@link #receiveFrame()} reads 8 bytes with a 200 ms idle timeout. Returns null on timeout.
 */
public class GivEnergyInverterPort extends JSerialCommPort {
    private static final Logger LOG = LoggerFactory.getLogger(GivEnergyInverterPort.class);
    private static final int REQUEST_BYTES = 8;
    private static final long READ_TIMEOUT_MS = 200;

    /**
     * Constructor.
     *
     * @param portname the portname
     * @param baudrate the baudrate
     */
    public GivEnergyInverterPort(final String portname, final int baudrate) {
        super(portname, baudrate, 8, 1, SerialPort.NO_PARITY, new byte[0], FrameDefinition.create(""));
    }

    @Override
    public ByteBuffer receiveFrame() {
        final byte[] buf = new byte[REQUEST_BYTES];
        final int read = super.readBytes(buf, READ_TIMEOUT_MS);
        if (read < REQUEST_BYTES) {
            LOG.debug("receiveFrame timeout: got {} of {} bytes", read, REQUEST_BYTES);
            return null;
        }
        return ByteBuffer.wrap(buf);
    }
}
