package com.clean.domain.exception;

import com.clean.domain.model.geometry.CellPosition;

public class InvalidPlacementException extends RuntimeException{

    public InvalidPlacementException(CellPosition position, String reason) {
        super("Cannot place rover at " + position + ": " + reason);
    }
}
