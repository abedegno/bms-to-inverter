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

import com.airepublic.bmstoinverter.core.BMS;
import com.airepublic.bmstoinverter.core.NoDataAvailableException;
import com.airepublic.bmstoinverter.core.Port;
import com.airepublic.bmstoinverter.core.TooManyInvalidFramesException;

/**
 * BMS reader for GivEnergy LV batteries communicating over RS485 (Modbus-RTU).
 *
 * <p>
 * <b>Placeholder body -- Task 8 fills in the full implementation.</b>
 * </p>
 */
public class GivEnergyBmsRS485Processor extends BMS {

    @Override
    protected void collectData(final Port port)
            throws TooManyInvalidFramesException, NoDataAvailableException, IOException {
        throw new UnsupportedOperationException("Processor body lands in Task 8");
    }
}
