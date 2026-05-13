package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PollSchedulerTest {

    @Test
    public void testFirstNCalls_returnHrOnly_irOnEveryNthCall() {
        PollScheduler s = new PollScheduler(2, 5); // 2 devices, IR every 5th call
        for (int i = 0; i < 4; i++) {
            assertNull(s.nextIrSlot(), "call " + i + " should not yield IR");
        }
        // 5th call yields IR
        PollScheduler.IrSlot slot = s.nextIrSlot();
        assertNotNull(slot);
        assertEquals(1, slot.deviceAddress); // first device
        assertEquals(1, slot.blockNumber);   // first block
    }

    @Test
    public void testIrSlotRotation_advancesAcrossDevicesAndBlocks() {
        PollScheduler s = new PollScheduler(3, 1); // 3 devices, IR every call
        PollScheduler.IrSlot[] expected = {
            new PollScheduler.IrSlot(1, 1), new PollScheduler.IrSlot(1, 2), new PollScheduler.IrSlot(1, 3),
            new PollScheduler.IrSlot(2, 1), new PollScheduler.IrSlot(2, 2), new PollScheduler.IrSlot(2, 3),
            new PollScheduler.IrSlot(3, 1), new PollScheduler.IrSlot(3, 2), new PollScheduler.IrSlot(3, 3),
            new PollScheduler.IrSlot(1, 1) // wraps
        };
        for (int i = 0; i < expected.length; i++) {
            PollScheduler.IrSlot got = s.nextIrSlot();
            assertEquals(expected[i].deviceAddress, got.deviceAddress, "step " + i);
            assertEquals(expected[i].blockNumber, got.blockNumber, "step " + i);
        }
    }

    @Test
    public void testInvalidConfig_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new PollScheduler(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new PollScheduler(2, 0));
    }
}
