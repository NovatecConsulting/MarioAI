package de.novatec.marioai.agents.included;

import de.novatec.marioai.tools.MarioAiRunner;
import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioKey;
import de.novatec.marioai.tools.LevelConfig;
import de.novatec.marioai.tools.MarioAiAgent;

import java.util.ArrayList;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ChallengeTask;
import ch.idsia.ai.tasks.TestTask;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario.STATUS;

public class NiMa_Agent extends MarioAiAgent {
	
	private int bestinput;
	private int currinput;
	private static MarioInput inputs[] = new MarioInput[2];
	private int bestscore = 0;
	private int frames = 8;
	private boolean won = false;
	
	static {
		inputs[1] = new MarioInput();
		inputs[1].set(MarioKey.JUMP, true);
		inputs[1].set(MarioKey.RIGHT, true);
		inputs[1].set(MarioKey.SPEED, true);
		inputs[0] = new MarioInput();
		inputs[0].set(MarioKey.LEFT, true);
	}
	
	public String getName() {return "NiMa Agent";}
	
	public int getExactScore(LevelScene copy) {
		int res=0;
		
		if(copy.getMarioStatus() == STATUS.LOSE) res-=10000;
		
		res += (copy.getMarioX()/16)*2; //adding passed distance 
		
		res+=copy.getKilledCreaturesTotal()*42;
		res+=copy.getKilledCreaturesByStomp()*12;
		res+=copy.getKilledCreaturesByShell()*17;
		res+=copy.getKilledCreaturesByFireBall()*10;
		
		res+=copy.getMarioCoins()*24; //money, money, money
	
		res+= copy.getMarioGainedMushrooms()*42;
		res +=copy.getMarioGainedFowers()*50;

		res -= copy.getTimesMarioHurt()*1000;
		 
		return res;
	}
	
	public MarioInput doAiLogic() {
		
		LevelScene copy = getAStarCopyOfLevelScene();
		bestscore = 0;
		bestinputrek(copy, frames);
		return inputs[bestinput];
		
	}
	
	private void bestinputrek(LevelScene copy, int a) {
		//MarioInput input = new MarioInput();
		if(!won) {
			for(int i=0;i<=1;i++) {
				
				currinput = i;
				LevelScene testscene = copy.getAStarCopy();	// levelscene kopieren
				testscene.setMarioInput(inputs[i]);	// input in levelscene einfügen
				testscene.tick();	// input passieren lassen
				bestinputrekstep(testscene, a-1);
			}	// for
		}
		else {
			return;
		}
	}	// bestinputrek
	
	private void bestinputrekstep(LevelScene copy, int a) {
		
		if(!won) {
		if(a>0) {
			
			for(int i=0;i<=1;i++) {
					
				LevelScene testscene = copy.getAStarCopy();	// levelscene kopieren
				testscene.setMarioInput(inputs[i]);	// input in levelscene einfügen
				testscene.tick();	// input passieren lassen
				if(testscene.getMarioStatus() == STATUS.WIN) {
					bestinput = 1;
					won = true;
				}
				else bestinputrekstep(testscene, a-1);
				
			}	// for
		}	// if
		else {
			int currscore = getExactScore(copy);
			if(currscore>bestscore) {
				if(copy.isMarioFalling()) {
					if(!fallInHole(copy.levelSceneObservation(1))) {
						bestinput = currinput;
						bestscore = currscore;
					}
				}
				else {
					bestinput = currinput;
					bestscore = currscore;
				}
			}	// if		
		}	// else 
	}	// if (!won)
		else {
			return;
		}
	}	// bestinputrekstep
	
	private boolean fallInHole(byte[][] bs) {

		for(int i=11;i<22;i++) {
			if(bs[i][11] != 0) return false;
		}
		return true;
	}

	public static void main(String[]args) {

//		MarioAiRunner.run(new NiMa_Agent(), LevelConfig.LAST_RESORT, false, 3);
		ArrayList<Agent> test = new ArrayList<Agent>();
		test.add(new NiMa_Agent());
		MarioAiRunner.multiAgentRun(test, LevelConfig.WHAT_HAVE_I_DONE, new ChallengeTask(), 50, 3, false, true, false, true, false);
		
	}
}