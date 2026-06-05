package com.airepublic.bmstoinverter.protocol.givenergy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackEncodingTest {

    @Test
    public void testEncode2730_subtracts2730() {
        // Block 3 example from docs/03: decoded value 2880 mV -> wire 0x00 0x96 (= 150)
        assertEquals(150, PackEncoding.encode2730(2880));
    }

    @Test
    public void testDecode2730_adds2730() {
        // Wire 0x00 0xB3 (= 179) -> decoded value 2909
        assertEquals(2909, PackEncoding.decode2730(179));
    }

    @Test
    public void testEncode2730_roundTrip() {
        for (int v = -1000; v <= 5000; v += 137) {
            assertEquals(v, PackEncoding.decode2730(PackEncoding.encode2730(v)));
        }
    }

    @Test
    public void testEncode2730_zeroInputProducesAbsentDeviceSentinel() {
        // Internal 0 -> wire (0 - 2730) = -2730 = 0xF556 in u16 two's-complement.
        // This is exactly the absent-device sentinel value seen on the wire (see docs/03).
        int encoded = PackEncoding.encode2730(0) & 0xFFFF;
        assertEquals(0xF556, encoded);
    }

    @Test
    public void testPadAsciiSerial_shorterThanWidth_padsWithSpacesAndNul() {
        byte[] result = PackEncoding.padAsciiSerial("ABC", 8);
        // "ABC" + space*4 + NUL
        byte[] expected = { 'A', 'B', 'C', ' ', ' ', ' ', ' ', 0 };
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, result);
    }

    @Test
    public void testPadAsciiSerial_exactWidth_truncatesToWidth() {
        byte[] result = PackEncoding.padAsciiSerial("ABCDEFGH", 8);
        // The last byte must be NUL to terminate, so the string is truncated to 7 chars.
        byte[] expected = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 0 };
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, result);
    }

    @Test
    public void testPadAsciiSerial_emptyInput() {
        byte[] result = PackEncoding.padAsciiSerial("", 4);
        byte[] expected = { ' ', ' ', ' ', 0 };
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, result);
    }
}
