package ch.idsia.benchmark.mario.engine;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

/**
 * This class should only be called by {@link Mario} to append bonus points to
 * his score.
 * 
 * @author cso NovaTec GmbH
 *
 */
public class BonusPointsAppender {
	private static LevelScene levelScene;

	public BonusPointsAppender(LevelScene levelScene) {
		BonusPointsAppender.levelScene = levelScene;
	}

	/**
	 * Calls {@link LevelScene#appendBonusPoints(int)} to append the bonus
	 * points.
	 * 
	 * @param bonusPoints
	 *            points to append.
	 */
	public void appendBonusPoints(final int bonusPoints) {
		levelScene.appendBonusPoints(bonusPoints);
	}
}
