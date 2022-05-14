package com.ludtek.micrometrics.metric;

import com.ludtek.micrometrics.model.Device;
import com.ludtek.micrometrics.model.State;
import com.ludtek.micrometrics.service.DeviceService;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceMetricReporter {
    private final DeviceService deviceService;
    private final Map<Integer, DeviceStateMonitor> monitors;


    @Autowired
    public DeviceMetricReporter(DeviceService deviceService,
                                MeterRegistry meterRegistry) {
        this.deviceService = deviceService;
        this.monitors = deviceService.listDevices().stream()
                .collect(Collectors.toMap(Device::getId,
                        device -> new DeviceStateMonitor(device, meterRegistry)));
    }

    @Scheduled(initialDelay = 1000l, fixedDelay = 5000l)
    public void refresh() {
        log.info("Refreshing Devices");
        deviceService.listDevices()
                .forEach(device -> monitors.get(device.getId()).update(device));
    }

    private static class DeviceStateMonitor {
        private static final String METRIC = "device.usage";

        private final int deviceId;
        private final MeterRegistry meterRegistry;
        private State currentState;
        private LongTaskTimer.Sample sample;

        public DeviceStateMonitor(Device device, MeterRegistry meterRegistry) {
            this.deviceId = device.getId();
            this.currentState = device.getState();
            this.meterRegistry = meterRegistry;

            sample = startTimer(device);
        }

        public synchronized void update(Device device) {
            if(device.getId() != deviceId) {
                throw new IllegalArgumentException("Unexpected device provided");
            }

            if (this.currentState != device.getState()) {
                var duration = sample.stop();
                sample = startTimer(device);
                log.info("Device {} state from: {} to: {} with duration: {}ms",
                        device.getId(), this.currentState, device.getState(), duration);
                this.currentState = device.getState();
            }
        }

        private LongTaskTimer.Sample startTimer(Device device) {
            return LongTaskTimer
                    .builder(METRIC)
                    .tags(createTags(device))
                    .register(meterRegistry)
                    .start();
        }

        private static final String ID_TAG = "DEVICE_ID";
        private static final String OS_TAG = "DEVICE_OS";
        private static final String STATE_TAG = "DEVICE_STATE";

        private static Iterable<Tag> createTags(Device device) {
            return Arrays.asList(Tag.of(ID_TAG, Integer.toString(device.getId())),
                    Tag.of(OS_TAG, device.getOs().name()),
                    Tag.of(STATE_TAG, device.getState().name()));
        }


    }

}
