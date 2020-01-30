package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.LevelScene;

public class Shell_Red extends Shell {
	private static final int yPicPreset = 0;
	public Shell_Red(LevelScene world, float x, float y) {
		super(world, x, y);
		yPic = yPicPreset;
	}
	
	public Shell_Red(LevelScene alreadyCopied, Shell_Red toCopy) {
		super(alreadyCopied, toCopy);
		yPic = yPicPreset;
	}
	
}
