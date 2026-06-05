/**
 * Codec for the GivEnergy LV BMS Modbus protocol.
 *
 * <p>Provides frame encode/decode, CRC-16, the non-standard FC=4 response framing
 * (address echo in place of byte_count), the {@code (value - 2730)} encoding for
 * temperature halfwords, and constant tables for the HR(0..27) and IR Block 1/2/3
 * layouts.
 *
 * <p>No I/O. Consumed by {@code bms-givenergy-rs485} and {@code inverter-givenergy-rs485}.
 */
package com.airepublic.bmstoinverter.protocol.givenergy;
