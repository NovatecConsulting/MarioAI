package ch.idsia.ai.tasks;

import ch.idsia.mario.engine.sprites.Mario.STATUS;

public class ChallengeTask implements Task{

	@Override
	public double getScoreBasesOnValues(STATUS marioStatus, int timeLeft, double marioX, int killsTotal,int killsByStomp, int killsByShell, int killsByFire, int collectedCoins, int collectedMuhsrooms,int collectedFlowers, int timesHurt) {
		double res=0;
		
		//---Positive 
		//--- Distance Score
		if(marioX>=0) res+=(int)marioX/16; //adding passed distance 
		
		//---Time Score
		if(timeLeft>=0)res+=timeLeft*8; // adding Points for time left
		
		//---Status Score
		if(marioStatus==STATUS.WIN) res+=1024; //one time bonus for winning
	
		//---Kill Score (violence is bad kids, don't be like mario)
		if(killsTotal>0) res+=killsTotal*42;
		if(killsByStomp>0) res+=killsByStomp*12;
		if(killsByShell>0) res+=killsByShell*17;
		if(killsByFire>0) res+=killsByFire*4;
	
		//---Collectible Score
		
		//---Coin Score 
		if(collectedCoins>=0) res+=collectedCoins*16; //money, money, money
	
		//---PowerUp Score
		if(collectedMuhsrooms>=0) res+=collectedMuhsrooms*42;
		if(collectedFlowers>=0) res+=collectedFlowers*42;

		//---Negative
		//--- Hurt Score
		if(timesHurt>=0) res-=timesHurt*42;
	
		return res;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
}
