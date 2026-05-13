package com.airepublic.bmstoinverter.bms.givenergy.rs485;

/** Decides which (device, IR block) to poll next. HR is polled every call; IR is rotated. */
public final class PollScheduler {

    /** A scheduled IR poll: which device address and which block (1/2/3). */
    public static final class IrSlot {
        public final int deviceAddress;
        public final int blockNumber;

        public IrSlot(int deviceAddress, int blockNumber) {
            this.deviceAddress = deviceAddress;
            this.blockNumber = blockNumber;
        }
    }

    private final int deviceCount;
    private final int irPeriod;
    private int callCounter = 0;
    private int slotIndex = 0;

    /**
     * @param deviceCount number of paralleled device addresses (1..N)
     * @param irPeriod IR is fired every {@code irPeriod}-th call (1 = every call)
     */
    public PollScheduler(int deviceCount, int irPeriod) {
        if (deviceCount < 1) throw new IllegalArgumentException("deviceCount must be >= 1");
        if (irPeriod < 1) throw new IllegalArgumentException("irPeriod must be >= 1");
        this.deviceCount = deviceCount;
        this.irPeriod = irPeriod;
    }

    /**
     * Returns the next IR slot if this call is an IR tick; otherwise null.
     * Call once per BMS.collectData() invocation.
     */
    public IrSlot nextIrSlot() {
        callCounter++;
        if (callCounter < irPeriod) {
            return null;
        }
        callCounter = 0;
        int device = 1 + (slotIndex / 3) % deviceCount;
        int block = 1 + slotIndex % 3;
        slotIndex = (slotIndex + 1) % (deviceCount * 3);
        return new IrSlot(device, block);
    }
}
