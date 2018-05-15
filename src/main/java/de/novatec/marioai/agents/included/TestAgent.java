package de.novatec.marioai.agents.included;

import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioKey;
import de.novatec.marioai.tools.MarioNtAgent;

public class TestAgent extends MarioNtAgent{

	@Override
	public MarioInput doAiLogic() {
		MarioInput input=getMarioInput();
		if(mayShoot())input.press(MarioKey.SPEED);
		moveRight();
		return input;
	}

	@Override
	public String getName() {
		return "TEST-AGENT";
	}

}
