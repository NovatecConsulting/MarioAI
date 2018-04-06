package de.novatec.marioai.tools;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.Scene;
import ch.idsia.mario.engine.sprites.Mario;
import de.novatec.mario.engine.generalization.Coordinates;

public class ExampleAgent extends MarioNtAgent{
	int c=0;
	
	@Override
	public String getName() {
		return "Test Agent";
	}

	@Override
	public void reset() {
		
	}

	@Override
	public MarioInput doAiLogic() {
	
		
		//showMarioViewAsAscii();
		if(c==24*5-1) {
		//System.out.println("original:");
			//System.out.println(getLevelScene());
//			System.out.println("copy ahead:");
//			LevelScene test=getDeepCopyOfLevelScene();
//			System.out.println(test);
//			
//	
//				 boolean[] t= Scene.keys;
//				 t[Mario.KEY_JUMP]=true;
//				 t[Mario.KEY_RIGHT]=true;
//				System.err.println(t.length);
//				int i=0;
//				while(i<100) {
//					test.setMarioKeys(t);
//					test.tick();
//					System.out.println(test);
//					i++;
//				}
//				
//				
//				
//				
//				System.out.println(test);
//		

			
		}
		LevelScene scene=getDeepCopyOfLevelScene();
		addCoordToDraw(new Coordinates((scene.getMarioX())+15, scene.getMarioY()-10));
		addCoordToDraw(new Coordinates((scene.getMarioX())+30, scene.getMarioY()-25));
		addCoordToDraw(new Coordinates((scene.getMarioX())+45, scene.getMarioY()-10));
		
		c=(++c)%(24*5);
		
		moveRight();
//
		if(isSlopeAhead()||isBrickAhead()) { jump();

		}
		//else sprint();
		if(mayShoot()&&isEnemyAhead()) {
			shoot();
		}
		else if(isEnemyAhead()) jump();
		//System.err.println(System.currentTimeMillis()-millis);
		return getMarioInput();
	}


}
