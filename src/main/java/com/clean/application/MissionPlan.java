package com.clean.application;

import java.util.List;

public record MissionPlan(int maxX, int maxY, List<RoverPlan> rovers) {

    public MissionPlan {
        rovers = List.copyOf(rovers);
    }
}
