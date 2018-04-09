package de.novatec.marioai.agents;

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
		
		if(isSlopeAhead()&&!isHoleAhead()) return getMarioInput();
	
		moveRight();
		
		if(isHoleAhead()||isBrickAhead()) jump();

		if(mayShoot()&&isEnemyAhead()) {
			shoot();
		}
		else if(isEnemyAhead()) jump();
		
		return getMarioInput();
	}


}
