package com.clean.domain.strategy;

import com.clean.domain.model.energy.EnergyCost;
import com.clean.domain.model.geometry.CellPosition;

public interface EnergyStrategy {
    EnergyCost costOfMove(CellPosition from, CellPosition to);
}
