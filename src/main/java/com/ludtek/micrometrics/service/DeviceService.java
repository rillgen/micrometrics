package com.ludtek.micrometrics.service;

import com.ludtek.micrometrics.exception.DeviceNotFoundException;
import com.ludtek.micrometrics.model.Device;
import com.ludtek.micrometrics.model.OS;
import com.ludtek.micrometrics.model.State;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class DeviceService {
    private static final int NUM_DEVICES = 10;
    private final ConcurrentMap<Integer, Device> devices = new ConcurrentHashMap<>();

    public DeviceService() {
        devices.putAll(createDevices().collect(Collectors.toMap(Device::getId, Function.identity())));
    }

    public Collection<Device> listDevices() {
        return devices.values();
    }

    public void updateState(int deviceId, State state) {
        var updated = devices.computeIfPresent(deviceId, (key, oldDevice) -> oldDevice.toBuilder()
                .state(state)
                .build());

        if (updated == null) {
            throw new DeviceNotFoundException();
        }
    }

    private static Stream<Device> createDevices() {
        return IntStream.range(1, NUM_DEVICES + 1).mapToObj(i ->
                Device.builder()
                        .id(i)
                        .os(select(OS.values()))
                        .state(select(State.values()))
                        .build());
    }

    private static Random random = new Random();

    private static <T> T select(T... e) {
        return e[random.nextInt(e.length)];
    }

}
