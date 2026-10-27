package com.clean.domain.exception;

import com.clean.domain.model.GroundId;
import com.clean.domain.model.RoverId;

public class PlateauMismatchException extends RuntimeException{

    public PlateauMismatchException(RoverId roverId, GroundId expected, GroundId actual) {
        super("Rover " + roverId + " belongs to ground " + expected + " but ground " + actual + " was supplied");
    }
}
