package com.clean.domain.model.rover;

/** A complete rover; clients should depend on the narrowest role they need. */
public interface Rover extends RoverIdentity, Steerable, Mobile {
}
