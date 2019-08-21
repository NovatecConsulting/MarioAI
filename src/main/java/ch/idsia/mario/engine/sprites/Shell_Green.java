package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.LevelScene;

public class Shell_Green extends Shell {
	private static final int yPicPreset = 1;
	public Shell_Green(LevelScene world, float x, float y) {
		super(world, x, y);
		yPic = yPicPreset;
	}
	
	public Shell_Green(LevelScene alreadyCopied, Shell_Green toCopy) {
		super(alreadyCopied, toCopy);
		yPic = yPicPreset;
	}
	
}
