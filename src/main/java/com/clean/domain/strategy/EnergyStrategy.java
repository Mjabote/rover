package com.clean.domain.strategy;

import com.clean.domain.model.CellPosition;
import com.clean.domain.model.EnergyCost;

public interface EnergyStrategy {
    EnergyCost costOfMove(CellPosition from, CellPosition to);
}
