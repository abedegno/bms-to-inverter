package com.airepublic.bmstoinverter.bms.givenergy.rs485;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Test stub that fakes the wire: maps recorded requests to canned responses. */
public class StubGivEnergyModbusPort extends GivEnergyModbusPort {

    private final Map<String, byte[]> cannedResponses = new HashMap<>();
    public final List<byte[]> sent = new ArrayList<>();
    private int lastExpectedResponseLength = 0;

    public StubGivEnergyModbusPort() {
        super("stub", 9600);
    }

    public void register(byte[] request, byte[] response) {
        cannedResponses.put(hex(request), response);
    }

    @Override
    public void setExpectedResponseLength(int n) {
        super.setExpectedResponseLength(n);
        this.lastExpectedResponseLength = n;
    }

    @Override
    public void open() {}

    @Override
    public boolean isOpen() { return true; }

    @Override
    public void close() {}

    @Override
    public void clearBuffers() {}

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
        if (sent.isEmpty()) return null;
        byte[] req = sent.get(sent.size() - 1);
        byte[] resp = cannedResponses.get(hex(req));
        return resp == null ? null : ByteBuffer.wrap(resp);
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x & 0xFF));
        return sb.toString();
    }
}
