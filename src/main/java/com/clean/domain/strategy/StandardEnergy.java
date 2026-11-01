package com.clean.domain.strategy;

import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.geometry.CellPosition;

public class StandardEnergy implements EnergyStrategy {

    public EnergyCost costOfMove(CellPosition from, CellPosition to) {
        return EnergyCost.NONE;
    }
}
