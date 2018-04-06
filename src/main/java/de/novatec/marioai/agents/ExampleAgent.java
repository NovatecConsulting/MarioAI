package de.novatec.marioai.agents;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.Scene;
import ch.idsia.mario.engine.sprites.Mario;
import de.novatec.mario.engine.generalization.Coordinates;
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
	
		moveRight();

		if(isSlopeAhead()||isBrickAhead()) jump();

		if(mayShoot()&&isEnemyAhead()) {
			shoot();
		}
		else if(isEnemyAhead()) jump();
		
		return getMarioInput();
	}


}
