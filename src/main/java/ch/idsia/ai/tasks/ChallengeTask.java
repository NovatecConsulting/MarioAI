package ch.idsia.ai.tasks;

import ch.idsia.mario.engine.sprites.Mario.STATUS;

public class ChallengeTask implements Task{
	
	public static final int DISTANCE_PHYS_TO_CELLS = 16;
	public static final int TIME_WEIGHT = 8;
	public static final int WIN_WEIGHT = 1024;
	public static final int KILLS_TOTAL_WEIGHT = 42;
	public static final int KILLS_BY_STOMP_WEIGHT = 42;
	public static final int KILLS_BY_SHELL_WEIGHT = 42;
	public static final int KILLS_BY_FIRE_WEIGHT = 42;
	public static final int COLLECTED_COINS_WEIGHT = 16;
	public static final int COLLECTED_MUSHROOMS_WEIGHT = 42;
	public static final int COLLECTED_FLOWERS_WEIGHT = 42;
	public static final int TIMES_HURT_WEIGHT = 42;
	
	
	@Override
	public double getScoreBasesOnValues(STATUS marioStatus, int timeLeft, double marioX, int killsTotal,int killsByStomp, int killsByShell, int killsByFire, int collectedCoins, int collectedMuhsrooms,int collectedFlowers, int timesHurt) {
		double res=0;
		
		//---Positive 
		//--- Distance Score
		if(marioX>=0) res+=(int)marioX/DISTANCE_PHYS_TO_CELLS; //adding passed distance 
		
		//---Time Score
		if(timeLeft>=0)res+=timeLeft*TIME_WEIGHT; // adding Points for time left
		
		//---Status Score
		if(marioStatus==STATUS.WIN) res+=WIN_WEIGHT; //one time bonus for winning
	
		//---Kill Score (violence is bad kids, don't be like mario)
		if(killsTotal>0) res+=killsTotal*KILLS_TOTAL_WEIGHT;
		if(killsByStomp>0) res+=killsByStomp*KILLS_BY_STOMP_WEIGHT;
		if(killsByShell>0) res+=killsByShell*KILLS_BY_SHELL_WEIGHT;
		if(killsByFire>0) res+=killsByFire*KILLS_BY_SHELL_WEIGHT;
	
		//---Collectible Score
		
		//---Coin Score 
		if(collectedCoins>=0) res+=collectedCoins*COLLECTED_COINS_WEIGHT; //money, money, money
	
		//---PowerUp Score
		if(collectedMuhsrooms>=0) res+=collectedMuhsrooms*COLLECTED_MUSHROOMS_WEIGHT;
		if(collectedFlowers>=0) res+=collectedFlowers*COLLECTED_FLOWERS_WEIGHT;

		//---Negative
		//--- Hurt Score
		if(timesHurt>=0) res-=timesHurt*TIMES_HURT_WEIGHT;
	
		return res;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
}
