package com.ludtek.micrometrics.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Device {
    int id;
    OS os;
    State state;
}
