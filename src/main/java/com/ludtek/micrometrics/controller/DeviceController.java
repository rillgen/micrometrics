package com.ludtek.micrometrics.controller;

import com.ludtek.micrometrics.exception.DeviceNotFoundException;
import com.ludtek.micrometrics.model.Device;
import com.ludtek.micrometrics.model.State;
import com.ludtek.micrometrics.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/device")
    public Collection<Device> getDevices() {
        return deviceService.listDevices();
    }

    @PutMapping("/device/{deviceId}/state/{state}")
    public ResponseEntity updateState(@PathVariable("deviceId") int deviceId, @PathVariable("state") State state) {
        try {
            deviceService.updateState(deviceId, state);
            return ResponseEntity.ok().build();
        } catch (DeviceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
