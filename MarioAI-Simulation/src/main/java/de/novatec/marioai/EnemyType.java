package de.novatec.marioai;

import ch.idsia.benchmark.mario.engine.generalization.EntityType;

public enum EnemyType {
	SPIKES,
	GOOMBA,
	GOOMBA_WINGED,
	GREEN_KOOPA,
	GREEN_KOOPA_WINGED,
	RED_KOOPA,
	RED_KOOPA_WINGED,
	SHELL_MOVING,
	WAVE_GOOMBA,
	DANGER,
	SPIKY,
	ENEMY_FLOWER,
	SPIKY_WINGED;
	
	public static EnemyType parseEntityType(EntityType entityType) {
		switch(entityType) {
			case SPIKES:
				return SPIKES;
			case GOOMBA:
				return GOOMBA;
			case GOOMBA_WINGED:
				return GOOMBA_WINGED;
			case GREEN_KOOPA:
				return GREEN_KOOPA;
			case GREEN_KOOPA_WINGED:
				return GREEN_KOOPA_WINGED;
			case RED_KOOPA:
				return RED_KOOPA;
			case RED_KOOPA_WINGED:
				return RED_KOOPA_WINGED;
			case SHELL_MOVING:
				return SHELL_MOVING;
			case WAVE_GOOMBA:
				return WAVE_GOOMBA;
			// Checked by default
			//case DANGER:
			//return DANGER;
			case SPIKY:
				return SPIKY;
			case ENEMY_FLOWER:
				return ENEMY_FLOWER;
			case SPIKY_WINGED:
				return SPIKY_WINGED;
			// DANGER or something unknown, which is dangerous by convention
			default:
				return DANGER;
		}
	}
}
