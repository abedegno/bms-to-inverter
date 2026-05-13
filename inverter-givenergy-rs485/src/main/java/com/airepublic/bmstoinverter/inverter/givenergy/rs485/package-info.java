/**
 * Emulator: responds to a real GivEnergy inverter's RS485 polls.
 *
 * <p>The inverter (controller) polls with FC=3 (read HR), FC=4 (read IR Block 1/2/3),
 * and occasionally FC=6 (write single HR). This module reads each request, builds the
 * appropriate response from the framework's aggregated {@link com.airepublic.bmstoinverter.core.bms.data.BatteryPack},
 * and sends it back using the codec in {@code protocol-givenergy}.
 *
 * <p>FC=6 writes from the inverter are echoed (per protocol requirement) and logged to a
 * configurable file for later analysis; the emulator does NOT translate FC=6 commands into
 * upstream actions on the source BMS in v1.
 */
package com.airepublic.bmstoinverter.inverter.givenergy.rs485;
