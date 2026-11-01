package com.clean.domain.model.ground;

/** A complete ground; clients should depend on the narrowest role they need. */
public interface Ground extends GroundIdentity, Terrain, ObstacleMap, ExplorationMap, ExplorationStats {
}
