package ch.idsia.benchmark.mario.engine.generalization;

public enum TileType {

	NOTHING,
	
	FIRE_FLOWER,
	
	MUSHROOM,
	
	SPIKES,
	
	FIREBALL,
	
	BULLET_BILL,
	
	GOOMBA,
	
	GOOMBA_WINGED,
	
	GREEN_KOOPA,
	
	GREEN_KOOPA_WINGED,
	
	RED_KOOPA,
	
	RED_KOOPA_WINGED,
	
	SHELL_STILL,
	
	SHELL_MOVING,
	
	WAVE_GOOMBA,
	
	SPIKY,
	
	ENEMY_FLOWER,
	
	SPIKY_WINGED,
	
	PRINCESS,
	
	MARIO,
	
	CANNON_MUZZLE,
	
	CANNON_TRUNK,
	
	COIN_ANIM,
	
	BREAKABLE_BRICK,
	
	QUESTION_BRICK,
	
	BRICK,
	
	FLOWER_POT,
	
	BORDER_CANNOT_PASS_THROUGH,
	
	BORDER_HILL,
	
	FLOWER_POT_OR_CANNON,
	
	LADDER,
	
	TOP_OF_LADDER;

	public boolean isEnemy() {
		//TODO complete list
		return this == GOOMBA || this == TileType.SPIKY;
	}

	public boolean isBrick() {
		System.out.println("BRICK TYPE: " + this);
		//TODO complete list
		return this == TileType.BRICK || this == TileType.BREAKABLE_BRICK || this == TileType.QUESTION_BRICK;
	}
}
