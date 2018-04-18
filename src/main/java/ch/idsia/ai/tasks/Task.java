package ch.idsia.ai.tasks;

import ch.idsia.mario.engine.sprites.Mario.STATUS;

public interface Task {
	
	public final static Task COMPETITIONTASK=new ChallengeTask();
	
	public double getScoreBasesOnValues(STATUS marioStatus, int timeLeft, double marioX, int killsTotal, int killsByStomp,int killsByShell,int killsByFire, int collectedCoins, int collectedMuhsrooms,int collectedFlowers,int timesHurt);
	public String getName();
}
