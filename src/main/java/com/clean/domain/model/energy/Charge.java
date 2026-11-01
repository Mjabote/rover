package com.clean.domain.model.energy;

public record Charge(long milliampHours) implements Comparable<Charge> {

    public static final Charge ZERO = new Charge(0);

    public Charge {
        if (milliampHours < 0) {
            throw new IllegalArgumentException("Charge must not be negative: " + milliampHours + " mAh");
        }
    }

    public static Charge ofMilliampHours(long milliampHours) {
        return new Charge(milliampHours);
    }

    public Charge plus(Charge other) {
        return new Charge(Math.addExact(milliampHours, other.milliampHours));}

    public Charge minus(Charge other) {
        return new Charge(milliampHours - other.milliampHours);}

    public boolean isAtLeast(Charge other) {
        return milliampHours >= other.milliampHours;
    }

    public boolean isZero() {
        return milliampHours == 0;
    }

    public double percentOf(Charge capacity) {
        return capacity.milliampHours == 0 ? 0.0 : 100.0 * milliampHours / capacity.milliampHours;
    }

    @Override
    public int compareTo(Charge other) {
        return Long.compare(milliampHours, other.milliampHours);
    }
}
