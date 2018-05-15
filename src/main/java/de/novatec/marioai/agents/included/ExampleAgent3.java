package de.novatec.marioai.agents.included;

import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioNtAgent;

public class ExampleAgent3 extends MarioNtAgent{
	
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
}
