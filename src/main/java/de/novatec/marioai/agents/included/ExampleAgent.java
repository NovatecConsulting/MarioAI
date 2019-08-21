package de.novatec.marioai.agents.included;

import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.LevelConfig;
import de.novatec.marioai.tools.MarioAiAgent;
import de.novatec.marioai.tools.MarioAiRunner;

public class ExampleAgent extends MarioAiAgent {
	
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

		if(isSlopeAhead()&&!isHoleAhead()&&!(getAStarCopyOfLevelScene().getMarioXA()<2)) return getMarioInput();
	
		moveRight();
		
		if(isHoleAhead()||isBrickAhead()||isQuestionbrickAbove()) jump();
		
		return getMarioInput();
	}
	
	public static void main(String[]args) {
		MarioAiRunner.run(new HumanKeyboardAgent(), LevelConfig.LEVEL_6, 50, 3, false, true, false);
	}
}
