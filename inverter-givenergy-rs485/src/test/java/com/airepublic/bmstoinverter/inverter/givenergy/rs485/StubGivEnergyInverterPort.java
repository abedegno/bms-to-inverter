package com.airepublic.bmstoinverter.inverter.givenergy.rs485;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class StubGivEnergyInverterPort extends GivEnergyInverterPort {

    private byte[] nextRequest;
    public final List<byte[]> sent = new ArrayList<>();

    public StubGivEnergyInverterPort() {
        super("stub", 9600);
    }

    public void setNextRequest(byte[] req) { this.nextRequest = req; }

    @Override public void open() {}
    @Override public boolean isOpen() { return true; }
    @Override public void close() {}
    @Override public void clearBuffers() {}

    @Override
    public void sendFrame(ByteBuffer frame) {
        byte[] copy = new byte[frame.remaining()];
        int pos = frame.position();
        frame.get(copy);
        frame.position(pos);
        sent.add(copy);
    }

    @Override
    public ByteBuffer receiveFrame() {
        return nextRequest == null ? null : ByteBuffer.wrap(nextRequest);
    }
}
