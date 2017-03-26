package de.novatec.marioai;

import ch.idsia.benchmark.mario.engine.generalization.EntityType;

public enum CollectibleType {
	FIRE_FLOWER,
	MUSHROOM,
	PRINCESS;
	
	public static CollectibleType parseEntityType(EntityType entityType) throws Exception {
		switch(entityType) {
			case FIRE_FLOWER:
				return CollectibleType.FIRE_FLOWER;
			case MUSHROOM:
				return CollectibleType.MUSHROOM;
			case PRINCESS:
				return CollectibleType.PRINCESS;
			default:
				throw new Exception("Illegal collectible");
		}
	}
}
