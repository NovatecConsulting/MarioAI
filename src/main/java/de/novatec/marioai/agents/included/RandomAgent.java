package de.novatec.marioai.agents.included;

import java.util.Random;

import de.novatec.marioai.tools.MarioInput;
import de.novatec.marioai.tools.MarioNtAgent;

public class RandomAgent extends MarioNtAgent{

	@Override
	public String getName() {
		return "Random Agent";
	}

	@Override
	public MarioInput doAiLogic() {
		Random r=new Random();
		
		boolean tmp[]=new boolean[5];
		
		for(int i=1;i<tmp.length;i++) tmp[i]=r.nextBoolean(); 
		return MarioInput.arrayToMarioInput(tmp);
	}

}
