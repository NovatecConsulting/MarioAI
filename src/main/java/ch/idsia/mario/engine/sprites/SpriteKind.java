package ch.idsia.mario.engine.sprites;

public enum SpriteKind {
	KIND_NONE(0),
	KIND_MARIO(1),
	KIND_GOOMBA(2), 
	KIND_RED_KOOPA(3), 
	KIND_GREEN_KOOPA(4), 
	KIND_BULLET_BILL(5),
	KIND_SPIKY(6), 
	KIND_ENEMY_FLOWER(7),
	KIND_SHELL(8),
	KIND_MUSHROOM(9),
	KIND_FIRE_FLOWER(10),
	KIND_BRICK_ANIM(20),
	KIND_SPARCLE(21),
	KIND_COIN_ANIM(22),
	KIND_FIREBALL(23),
	KIND_UNDEF(-1);
	
	byte number;
	
	SpriteKind(int number) {
		this.number = (byte) number;
	}

	public byte getNumber() {
		// TODO Auto-generated method stub
		return number;
	}
}
