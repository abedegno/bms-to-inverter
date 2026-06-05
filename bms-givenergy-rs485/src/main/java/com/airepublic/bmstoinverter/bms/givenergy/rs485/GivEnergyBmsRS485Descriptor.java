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

import com.airepublic.bmstoinverter.core.BMS;
import com.airepublic.bmstoinverter.core.BMSConfig;
import com.airepublic.bmstoinverter.core.BMSDescriptor;
import com.airepublic.bmstoinverter.core.Port;

/**
 * {@link BMSDescriptor} for the GivEnergy LV BMS communicating over RS485 (Modbus-RTU).
 */
public class GivEnergyBmsRS485Descriptor implements BMSDescriptor {

    @Override
    public String getName() {
        return "GIVENERGY_RS485";
    }

    @Override
    public int getDefaultBaudRate() {
        return 9600;
    }

    @Override
    public Class<? extends BMS> getBMSClass() {
        return GivEnergyBmsRS485Processor.class;
    }

    @Override
    public Port createPort(final BMSConfig config) {
        return new GivEnergyModbusPort(config.getPortLocator(), config.getBaudRate());
    }
}
