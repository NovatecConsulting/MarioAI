package de.novatec.marioai.tools;

public class ExampleAgent2 extends MarioNtAgent {

	@Override
	public MarioInput doAiLogic() {
		
		
		if(isHoleAhead()) {
			System.out.println("HOLE");
		}
		
		moveRight();
		
		if(isEnemyAhead()) {
			shoot();
		}
		
		if((isDeepSlopeAhead()||isBrickAhead())||isQuestionbrickAbove()) jump();
		return getMarioInput();
	}

	@Override
	public String getName() {
		return "ExampleAgent2";
	}

}
