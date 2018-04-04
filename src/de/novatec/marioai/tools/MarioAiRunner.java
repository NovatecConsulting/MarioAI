package de.novatec.marioai.tools;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.CheaterKeyboardAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.sprites.Mario.MODE;
import ch.idsia.tools.RunnerOptions;
import competition.cig.robinbaumgarten.AStarAgent;
import de.novatec.marioai.astar.AStar_RGU;

public class MarioAiRunner {

	public static void run(Agent agent,LevelConfig levelConfig,int fps, boolean randomize, boolean viewable) {
		if(agent==null){
			System.err.println("Agent can't be null!\nPlease choose a valid Agent!");
			return;
		}
		if(levelConfig==null&&!randomize) System.err.println("LevelConfig is null, Level will be randomized!");
		if(randomize||levelConfig==null) levelConfig=LevelConfig.randomize(levelConfig);
			
		RunnerOptions rOptions=new RunnerOptions(agent,levelConfig);

		rOptions.setViewable(viewable);
		rOptions.setFPS(fps);
		rOptions.setWindowHeigth(320*5);
		rOptions.setWindowWidth(240*5);

		Task task=new ProgressTask(rOptions);
		
		System.out.println(task.evaluteWithExtendedInfo()); 
	}
	
	public static void run(Agent agent,LevelConfig levelConfig,boolean randomize) {
		if(agent==null) {
			System.err.println("Agent can't be null!\nPlease choose a valid Agent!");
			return;
		} 
		if(levelConfig==null) {
			if(!randomize){
			System.err.println("LevelConfig can only be null if randomize==true!\n Please choose a valid LevelConfig or activate Random-Level-Generation");
			return;
			}
			else {
				run(agent, levelConfig, 24, true, true);
			}
		}
		else 
		
		run(agent, levelConfig, 24, randomize, true);
	}
	
	public static void main(String[]args) {
		//run(new AStar_RGU(),null,24,false,true);
		//run(new ExampleAgent(),LevelConfig.Level1,24,true,true);
		//run(new ExampleAgent(),LevelConfig.Level1,48,false,true);
		run(new ExampleAgent2(),LevelConfig.LevelCUSTOM3,24,true,true);
		//run(new HumanKeyboardAgent(), LevelConfig.LevelCUSTOM1,24,true,true);
		//run(new ExampleAgent(), null, true);
	    //run(new AStarAgent(), LevelConfig.ASTARKILLER,24,true,true);
	}
	
}


