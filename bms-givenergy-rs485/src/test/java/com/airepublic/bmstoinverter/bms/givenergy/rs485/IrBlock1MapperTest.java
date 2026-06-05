package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import org.junit.jupiter.api.Test;
import com.airepublic.bmstoinverter.core.bms.data.BatteryPack;
import static org.junit.jupiter.api.Assertions.*;

public class IrBlock1MapperTest {

    private static byte[] block1(String serial, int[] temps_decideg) {
        byte[] out = new byte[42];
        // Serial: 20 bytes ASCII padded with spaces, NUL-terminated
        int copy = Math.min(serial.length(), 19);
        for (int i = 0; i < copy; i++) out[i] = (byte) serial.charAt(i);
        for (int i = copy; i < 19; i++) out[i] = ' ';
        out[19] = 0;
        // Temps at offsets 22..31 (5 BE16)
        for (int i = 0; i < 5; i++) {
            out[22 + i * 2] = (byte) ((temps_decideg[i] >> 8) & 0xFF);
            out[22 + i * 2 + 1] = (byte) (temps_decideg[i] & 0xFF);
        }
        return out;
    }

    @Test
    public void testMapsSerialAndTemps() {
        byte[] data = block1("BAT12345", new int[]{ 173, 178, 173, 165, 169 }); // decideg-C
        BatteryPack pack = new BatteryPack();
        pack.tempMax = 999; // sentinel; mapper must NOT overwrite (HR owns tempMax)
        IrBlock1Mapper.apply(data, pack);
        assertEquals("BAT12345", pack.manufacturerCode);
        assertEquals(165, pack.tempMin);          // min of {173,178,173,165,169}
        assertEquals(999, pack.tempMax, "tempMax owned by HrMapper; IrBlock1Mapper must not write");
        assertEquals((173 + 178 + 173 + 165 + 169) / 5, pack.tempAverage);
    }

    @Test
    public void testAbsentDeviceDetected() {
        // All five temps are 0xF556 = -2730 (signed)
        byte[] data = block1("", new int[]{ -2730, -2730, -2730, -2730, -2730 });
        BatteryPack pack = new BatteryPack();
        pack.numberOfCells = 16; // pre-populated; mapper must zero it for absent device
        IrBlock1Mapper.apply(data, pack);
        assertEquals(0, pack.numberOfCells, "absent device must set numberOfCells=0");
    }

    @Test
    public void testWrongLengthRejected() {
        assertThrows(IllegalArgumentException.class, () -> IrBlock1Mapper.apply(new byte[10], new BatteryPack()));
    }
}
