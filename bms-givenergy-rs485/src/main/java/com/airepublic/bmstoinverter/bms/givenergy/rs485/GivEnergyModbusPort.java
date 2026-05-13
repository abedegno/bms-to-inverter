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

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airepublic.bmstoinverter.core.Port;
import com.airepublic.bmstoinverter.core.protocol.rs485.FrameDefinition;
import com.airepublic.bmstoinverter.protocol.rs485.JSerialCommPort;
import com.fazecast.jSerialComm.SerialPort;

/**
 * RS485 port specialised for GivEnergy's Modbus-RTU dialect.
 *
 * <p>
 * Modbus-RTU has no start byte; frames are silence-delimited. GivEnergy responses are
 * fixed-length, so the Processor sets the expected length before each send/receive cycle and
 * this wrapper reads exactly that many bytes (instead of searching for a start flag).
 * </p>
 *
 * <p>
 * Implementation note: {@link JSerialCommPort#readBytes(byte[], long)} polls the inherited
 * {@code ByteReaderWriter} queue until {@code expectedResponseLength} bytes are available or
 * the timeout elapses. 200 ms is generous for a 9600-baud, 61-byte frame (~64 ms on the wire).
 * </p>
 */
public class GivEnergyModbusPort extends JSerialCommPort {

    private static final Logger LOG = LoggerFactory.getLogger(GivEnergyModbusPort.class);

    /** Silence timeout used when draining a fixed-length Modbus-RTU response. */
    private static final long RESPONSE_TIMEOUT_MS = 200L;

    private int expectedResponseLength = 0;

    /**
     * Constructor.
     *
     * @param portname the serial port name (e.g. {@code /dev/ttyUSB0})
     * @param baudrate the baud rate (typically 9600 for GivEnergy BMS)
     */
    public GivEnergyModbusPort(final String portname, final int baudrate) {
        // Modbus-RTU has no start flag and no declarative frame definition; pass empty values.
        super(portname, baudrate, 8, 1, SerialPort.NO_PARITY, new byte[0], FrameDefinition.create(""));
    }

    /**
     * Sets the number of bytes the next {@link #receiveFrame()} call should read.
     *
     * <p>
     * The Processor must call this immediately before issuing each Modbus request so that
     * {@link #receiveFrame()} knows how many bytes to consume from the wire.
     * </p>
     *
     * @param n expected response length in bytes
     */
    public void setExpectedResponseLength(final int n) {
        this.expectedResponseLength = n;
    }

    /**
     * Reads exactly {@code expectedResponseLength} bytes from the serial port.
     *
     * <p>
     * Returns {@code null} when {@code expectedResponseLength} is zero (no request has been
     * set up yet). Resets {@code expectedResponseLength} to 0 after a successful read so that
     * accidental double-calls do not consume garbage bytes.
     * </p>
     *
     * @return a {@link ByteBuffer} containing the raw Modbus-RTU response frame, or {@code null}
     * @throws IOException if the read times out or the port is not open
     */
    @Override
    public ByteBuffer receiveFrame() {
        if (expectedResponseLength <= 0) {
            LOG.warn("receiveFrame() called with expectedResponseLength={}; returning null", expectedResponseLength);
            return null;
        }

        final byte[] buffer = new byte[expectedResponseLength];
        final int bytesRead = readBytes(buffer, RESPONSE_TIMEOUT_MS);

        if (bytesRead == -1) {
            LOG.error("Timed out waiting for {} bytes from GivEnergy BMS (timeout={}ms)",
                    expectedResponseLength, RESPONSE_TIMEOUT_MS);
            // Reset so callers do not retry with stale state.
            expectedResponseLength = 0;
            return null;
        }

        // Reset so a stale length cannot trigger a phantom read.
        expectedResponseLength = 0;

        final ByteBuffer frame = ByteBuffer.wrap(buffer);
        LOG.debug("receiveFrame: {}", Port.printBuffer(frame));
        return frame;
    }
}
