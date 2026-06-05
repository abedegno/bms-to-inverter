package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import com.airepublic.bmstoinverter.protocol.givenergy.GivEnergyModbus;
import com.airepublic.bmstoinverter.protocol.givenergy.HoldingRegisters;
import static org.junit.jupiter.api.Assertions.*;

public class GivEnergyBmsRS485ProcessorTest {

    @Test
    public void testCollectData_hrPoll_populatesPack0() throws Exception {
        GivEnergyBmsRS485Processor proc = new GivEnergyBmsRS485Processor();
        StubGivEnergyModbusPort port = new StubGivEnergyModbusPort();

        int[] regs = new int[28];
        regs[0]  = HoldingRegisters.HR0_DEVICE_MARKER;
        regs[21] = 93;
        regs[22] = 5320;
        regs[23] = 1490;
        regs[24] = 25;
        regs[25] = 6000;
        regs[27] = 6000;
        byte[] hrResp = GivEnergyModbus.encodeFC3Response(1, regs);
        byte[] hrReq = GivEnergyModbus.encodeFC3Request(1, HoldingRegisters.FULL_POLL_START, HoldingRegisters.FULL_POLL_COUNT);
        port.register(hrReq, hrResp);

        proc.collectData(port);

        BatteryPack pack = proc.getBatteryPack(0);
        assertEquals(930, pack.packSOC);
        assertEquals(532, pack.packVoltage);
        assertEquals(149, pack.packCurrent);
        assertEquals(250, pack.tempMax);
        assertEquals(600, pack.maxPackChargeCurrent);
        assertEquals(-600, pack.maxPackDischargeCurrent);
    }
}
