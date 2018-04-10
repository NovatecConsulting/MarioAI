package ch.idsia.tools;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Mario.STATUS;

import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 12, 2009
 * Time: 12:44:51 AM
 * Package: .Tools
 */
public class EvaluationInfo
{
    private static final int MagicNumberUndef = -42;
    public Level.LEVEL_TYPES levelType = Level.LEVEL_TYPES.UNKNOWN;
    public STATUS marioStatus = STATUS.UNKNOWN;
    public int livesLeft = MagicNumberUndef;
    public double lengthOfLevelPassedPhys = MagicNumberUndef;
    public int lengthOfLevelPassedCells = MagicNumberUndef;
    public int totalLengthOfLevelCells = MagicNumberUndef;
    public double totalLengthOfLevelPhys = MagicNumberUndef;
    public int levelXExit=MagicNumberUndef;
    public int timeSpentOnLevel = MagicNumberUndef;
    public int totalTimeGiven = MagicNumberUndef;
    public int numberOfGainedCoins = MagicNumberUndef;
//    public int totalNumberOfCoins = MagicNumberUndef;
    public int totalActionsPerfomed = MagicNumberUndef;
    public int totalFramesPerfomed = MagicNumberUndef;
    private int timesHurt=MagicNumberUndef;
    public String Memo = "";
    public int timeLeft = MagicNumberUndef;
    public String agentName = "undefinedAgentName";
    public String agentType = "undefinedAgentType";
    public int levelDifficulty = MagicNumberUndef;
    public int levelRandSeed = MagicNumberUndef;
    public Mario.MODE marioMode = null;
    
    private int killsTotal = MagicNumberUndef;
    private int killedCreaturesbyStomp=MagicNumberUndef;
    private int killedCreaturesbyShell=MagicNumberUndef;
    private int killedCreaturesbyFire=MagicNumberUndef;
    
    private int gainedMushrooms=MagicNumberUndef;
    private int gainedFlower=MagicNumberUndef;

    public double computeBasicFitness() {
    	return LevelScene.getScoreBasesOnValues(marioStatus, timeLeft, lengthOfLevelPassedPhys, killsTotal, killedCreaturesbyStomp, killedCreaturesbyShell, killedCreaturesbyFire, numberOfGainedCoins, gainedMushrooms, gainedFlower, timesHurt);
    }

    public double computeDistancePassed()
    {
        return lengthOfLevelPassedPhys;
    }

    public int computeKillsTotal()
    {
        return this.killsTotal;
    }

    private DecimalFormat df = new DecimalFormat("0.00");

    public int getKillsTotal() {
		return killsTotal;
	}

	public void setKillsTotal(int killsTotal) {
		this.killsTotal = killsTotal;
	}

	public int getKilledCreaturesbyStomp() {
		return killedCreaturesbyStomp;
	}

	public void setKilledCreaturesbyStomp(int killedCreaturesbyStomp) {
		this.killedCreaturesbyStomp = killedCreaturesbyStomp;
	}

	public int getKilledCreaturesbyShell() {
		return killedCreaturesbyShell;
	}

	public void setKilledCreaturesbyShell(int killedCreaturesbyShell) {
		this.killedCreaturesbyShell = killedCreaturesbyShell;
	}

	public int getKilledCreaturesbyFire() {
		return killedCreaturesbyFire;
	}

	public void setKilledCreaturesbyFire(int killedCreaturesbyFire) {
		this.killedCreaturesbyFire = killedCreaturesbyFire;
	}

	public int getGainedMushrooms() {
		return gainedMushrooms;
	}

	public void setGainedMushrooms(int gainedMushrooms) {
		this.gainedMushrooms = gainedMushrooms;
	}

	public int getGainedFlower() {
		return gainedFlower;
	}

	public void setGainedFlower(int gainedFlower) {
		this.gainedFlower = gainedFlower;
	}

	public int getTimesHurt() {
		return timesHurt;
	}

	public void setTimesHurt(int timesHurt) {
		this.timesHurt = timesHurt;
	}

	@Override
    public String toString()
    {

        String ret = "\n\n			   //////////////\n"; 
        ret +=	    "			   //Statistics//\n";
        ret +=     "			   //////////////";
        ret += "\n                  Player/Agent type : " + agentType;
        ret += "\n                  Player/Agent name : " + agentName;
        ret += "\n                       Mario Status : " + ((marioStatus == STATUS.WIN) ? "Won" : "Lost");
        ret += "\n                         Level Type : " + levelType;
        ret += "\n                   Level Difficulty : " + levelDifficulty;
        ret += "\n                    Level Rand Seed : " + levelRandSeed;
        ret += "\nTotal Length of Level (Phys, Cells) : " + "(" + levelXExit*16 + "," + levelXExit + ")";
        ret += "\n                      Passed (Phys) : " + df.format(lengthOfLevelPassedPhys / (levelXExit*16) *100) + "% (" + df.format(lengthOfLevelPassedPhys) + " of " + df.format(levelXExit*16) + ")";
        ret += "\n                     Passed (Cells) : " + df.format((double)lengthOfLevelPassedCells / levelXExit *100) + "% (" + lengthOfLevelPassedCells + " of " + levelXExit + ")";
        ret += "\n             Time Spent(Fractioned) : " + timeSpentOnLevel + " (" + df.format((double)timeSpentOnLevel/totalTimeGiven*100) + "%)";
        ret += "\n              Time Left(Fractioned) : " + timeLeft + " (" + df.format((double)timeLeft/totalTimeGiven*100) + "%)";
        ret += "\n                   Total time given : " + totalTimeGiven;
        ret += "\n                       Coins Gained : " + numberOfGainedCoins;
        ret += "\n                        Total Kills : " + killsTotal;
        if(killsTotal>0) {
        	ret += "\n                           by stomp : " + killedCreaturesbyStomp+" (" + df.format((double)killedCreaturesbyStomp/killsTotal*100) + "%)";
        	ret += "\n                           by shell : " + killedCreaturesbyShell+" (" + df.format((double)killedCreaturesbyShell/killsTotal*100) + "%)";
        	ret += "\n                           by  fire : " + killedCreaturesbyFire+" (" + df.format((double)killedCreaturesbyFire/killsTotal*100) + "%)";
        }
        else { 
        	ret += "\n                           by stomp : 0 (0.0%)";
        	ret += "\n                           by shell : 0 (0.0%)";
        	ret += "\n                           by  fire : 0 (0.0%)";
        }
        ret += "\n             Total Actions Perfomed : " + totalActionsPerfomed;
        ret += "\n              Total Frames Perfomed : " + totalFramesPerfomed;
        ret += "\n               Simple Basic Fitness : " + df.format(computeBasicFitness());
        
        ret += "\n\n";
        return ret;
    }
}
