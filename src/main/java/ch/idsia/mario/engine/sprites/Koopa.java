package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.LevelScene;

public abstract class Koopa extends Enemy  {

	public Koopa(LevelScene alreadyCopied, Enemy toCopy) {
		super(alreadyCopied, toCopy);
		// TODO Auto-generated constructor stub
	}

	public Koopa(LevelScene world, int x, int y, int dir, boolean winged, int mapX, int mapY) {
		super(world, x, y, dir, winged, mapX, mapY);
		// TODO Auto-generated constructor stub
	}

}
