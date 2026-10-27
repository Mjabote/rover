package com.clean.domain.model;

public record CellSize(double metres) {

    public CellSize{
        if(!(metres>0) || Double.isInfinite(metres)){
            throw new IllegalArgumentException("Cell size must be a positive finite number of metres: " + metres);
        }
    }
    public static CellSize ofMetre(double metres){
        return new CellSize(metres);
    }
}
