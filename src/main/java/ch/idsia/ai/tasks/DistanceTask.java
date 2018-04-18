package ch.idsia.ai.tasks;

import ch.idsia.mario.engine.sprites.Mario.STATUS;

public class DistanceTask implements Task{

	@Override
	public double getScoreBasesOnValues(STATUS marioStatus, int timeLeft, double marioX, int killsTotal,int killsByStomp, int killsByShell, int killsByFire, int collectedCoins, int collectedMuhsrooms,int collectedFlowers, int timesHurt) {
		return marioX;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
