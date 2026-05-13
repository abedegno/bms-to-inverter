package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class IrBlock2MapperTest {

    private static byte[] block2(int cellCount, int cycles, int packV_mV, int ratedCap_cAh, int designCap_cAh, int remainCap_cAh, int soc_pct, int fwVersion) {
        byte[] out = new byte[38];
        out[0] = (byte) cellCount;
        out[1] = (byte) ((cycles >> 8) & 0xFF); out[2] = (byte) (cycles & 0xFF);
        out[5] = (byte) ((packV_mV >> 8) & 0xFF); out[6] = (byte) (packV_mV & 0xFF);
        out[7] = (byte) ((packV_mV >> 8) & 0xFF); out[8] = (byte) (packV_mV & 0xFF);
        out[15] = (byte) ((ratedCap_cAh >> 8) & 0xFF); out[16] = (byte) (ratedCap_cAh & 0xFF);
        out[19] = (byte) ((designCap_cAh >> 8) & 0xFF); out[20] = (byte) (designCap_cAh & 0xFF);
        out[23] = (byte) ((remainCap_cAh >> 8) & 0xFF); out[24] = (byte) (remainCap_cAh & 0xFF);
        out[25] = (byte) soc_pct;
        out[35] = (byte) ((fwVersion >> 8) & 0xFF); out[36] = (byte) (fwVersion & 0xFF);
        return out;
    }

    @Test
    public void testMapsAllFields() {
        byte[] data = block2(16, 50, 53000, 1860, 1860, 1750, 94, 0x0BCE);
        BatteryPack pack = new BatteryPack();
        IrBlock2Mapper.apply(data, pack);
        assertEquals(16, pack.numberOfCells);
        assertEquals(50, pack.bmsCycles);
        assertEquals(186000, pack.ratedCapacitymAh);
        assertEquals(175000, pack.remainingCapacitymAh);
        // packSOC intentionally not asserted -- it is owned by HrMapper (HR21), not IrBlock2Mapper.
        assertEquals("0BCE", pack.softwareVersion);
        assertEquals(1000, pack.packSOH);
    }

    @Test
    public void testSohBelow100_whenRatedLessThanDesign() {
        byte[] data = block2(16, 50, 53000, 1830, 1860, 1700, 91, 0x0BCE);
        BatteryPack pack = new BatteryPack();
        IrBlock2Mapper.apply(data, pack);
        assertEquals("0BCE", pack.softwareVersion);
        assertEquals(983, pack.packSOH);
    }

    @Test
    public void testZeroDesignCapacity_safelyHandled() {
        byte[] data = block2(16, 50, 53000, 1860, 0, 1750, 94, 0x0BCE);
        BatteryPack pack = new BatteryPack();
        IrBlock2Mapper.apply(data, pack);
        assertEquals("0BCE", pack.softwareVersion);
        assertEquals(0, pack.packSOH);
    }

    @Test
    public void testWrongLengthRejected() {
        assertThrows(IllegalArgumentException.class, () -> IrBlock2Mapper.apply(new byte[10], new BatteryPack()));
    }
}
