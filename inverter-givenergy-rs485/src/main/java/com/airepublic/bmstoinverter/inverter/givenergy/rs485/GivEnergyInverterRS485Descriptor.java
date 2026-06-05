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

import com.airepublic.bmstoinverter.core.Inverter;
import com.airepublic.bmstoinverter.core.InverterConfig;
import com.airepublic.bmstoinverter.core.InverterDescriptor;
import com.airepublic.bmstoinverter.core.Port;

/** {@link InverterDescriptor} for emulating a GivEnergy LV BMS to an inverter via RS485. */
public class GivEnergyInverterRS485Descriptor implements InverterDescriptor {

    @Override
    public String getName() {
        return "GIVENERGY_RS485";
    }

    @Override
    public int getDefaultBaudRate() {
        return 9600;
    }

    @Override
    public Class<? extends Inverter> getInverterClass() {
        return GivEnergyInverterRS485Processor.class;
    }

    @Override
    public Port createPort(final InverterConfig config) {
        return new GivEnergyInverterPort(config.getPortLocator(), config.getBaudRate());
    }
}
