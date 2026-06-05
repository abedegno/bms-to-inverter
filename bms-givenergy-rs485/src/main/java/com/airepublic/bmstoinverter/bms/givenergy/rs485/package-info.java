/**
 * BMS reader for GivEnergy LV batteries over RS485.
 *
 * <p>Polls a real GivEnergy LV BMS using the non-standard Modbus protocol
 * documented in {@code protocol-givenergy} and at https://github.com/open-giv/bms-analysis.
 * Populates the framework's {@link com.airepublic.bmstoinverter.core.bms.data.BatteryPack}
 * data model, which downstream {@code inverter-*} modules consume.
 *
 * <p>Supports up to 5 paralleled GivEnergy batteries (device addresses 1..5).
 * Read-only on the BMS side in v1 (no FC=06 writes issued to the battery).
 */
package com.airepublic.bmstoinverter.bms.givenergy.rs485;
