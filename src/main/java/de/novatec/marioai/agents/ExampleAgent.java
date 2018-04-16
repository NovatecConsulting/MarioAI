package de.novatec.marioai.agents;

import de.novatec.marioai.tools.LevelConfig;
import de.novatec.marioai.tools.MarioAiRunner;
import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioNtAgent;

public class ExampleAgent extends MarioNtAgent{
	
	@Override
	public String getName() {
		return "Test Agent";
	}

	@Override
	public void reset() {
		
	}

	@Override
	public MarioInput doAiLogic() {
		if(mayShoot()&&isEnemyAhead()) {
			shoot();
		}
		if(isEnemyAhead()) jump();
		if(isSlopeAhead()&&!isHoleAhead()&&!(getDeepCopyOfLevelScene().getMarioXA()<2)) return getMarioInput();
	
		moveRight();
		
		if(isHoleAhead()||isBrickAhead()||isQuestionbrickAbove()) jump();

		
		else if(isEnemyAhead()) jump();
		
		return getMarioInput();
	}
	
	public static void main(String [] args) {		
		MarioAiRunner.run(new HumanKeyboardAgent(), LevelConfig.HARD_ENEMY_TRAINING, 24, 3, true, true, false);
	}


}
