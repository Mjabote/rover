package com.clean.domain.strategy;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.EnergyCost;

public class StandardEnergy implements EnergyStrategy {

    public EnergyCost costOfMove(CellPosition from, CellPosition to) {
        return EnergyCost.NONE;
    }
}
