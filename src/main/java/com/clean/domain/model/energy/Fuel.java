package com.clean.domain.model.energy;

public record Fuel(long millilitres) implements Comparable<Fuel> {

    public static final Fuel ZERO = new Fuel(0);

    public Fuel {
        if (millilitres < 0) {
            throw new IllegalArgumentException("Fuel must not be negative: " + millilitres + " mL");
        }
    }

    public static Fuel ofMillilitres(long millilitres) {
        return new Fuel(millilitres);
    }

    public Fuel plus(Fuel other) {
        return new Fuel(Math.addExact(millilitres, other.millilitres));
    }

    public Fuel minus(Fuel other) {
        return new Fuel(millilitres - other.millilitres);
    }

    public boolean isAtLeast(Fuel other) {
        return millilitres >= other.millilitres;
    }

    public boolean isZero() {
        return millilitres == 0;
    }

    public double percentOf(Fuel capacity) {
        return capacity.millilitres == 0 ? 0.0 : 100.0 * millilitres / capacity.millilitres;
    }

    @Override
    public int compareTo(Fuel other) {
        return Long.compare(millilitres, other.millilitres);
    }
}
