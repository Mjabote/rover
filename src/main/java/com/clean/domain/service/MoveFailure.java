package com.clean.domain.service;

public enum MoveFailure {
    OUT_OF_BOUNDS("destination is outside the ground"),
    OBSTACLE("destination contains an obstacle"),
    INSUFFICIENT_FUEL("not enough fuel"),
    INSUFFICIENT_BATTERY("not enough battery");

    private final String description;

    MoveFailure(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}

