package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class IrBlock3MapperTest {

    private static byte[] block3(int[] cellsMv, int maxMv, int minMv) {
        byte[] out = new byte[40];
        for (int i = 0; i < 16; i++) {
            out[i * 2] = (byte) ((cellsMv[i] >> 8) & 0xFF);
            out[i * 2 + 1] = (byte) (cellsMv[i] & 0xFF);
        }
        out[36] = (byte) ((maxMv >> 8) & 0xFF); out[37] = (byte) (maxMv & 0xFF);
        out[38] = (byte) ((minMv >> 8) & 0xFF); out[39] = (byte) (minMv & 0xFF);
        return out;
    }

    @Test
    public void testMapsCellVoltagesAndMinMax() {
        int[] cells = new int[16];
        for (int i = 0; i < 16; i++) cells[i] = 3300 + i; // 3300..3315
        byte[] data = block3(cells, 3315, 3300);
        BatteryPack pack = new BatteryPack();
        IrBlock3Mapper.apply(data, pack);
        for (int i = 0; i < 16; i++) {
            assertEquals(3300 + i, pack.cellVmV[i], "cell " + i);
        }
        assertEquals(3315, pack.maxCellmV);
        assertEquals(3300, pack.minCellmV);
        assertEquals(15, pack.cellDiffmV);
    }

    @Test
    public void testWrongLengthRejected() {
        assertThrows(IllegalArgumentException.class, () -> IrBlock3Mapper.apply(new byte[10], new BatteryPack()));
    }
}
