package de.novatec.marioai.tools;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.RunnerOptions;

/**
 * Simple helper class to start the evaluation of an agent. 
 * @author rgu 
 *
 */
public class MarioAiRunner {
	
	/**
	 * No instances needed.
	 */
	private MarioAiRunner() {
		
	}

	/**
	 * Starts the evaluation of an agent with the specified parameters.
	 * @param agent Agent to be evaluated
	 * @param levelConfig Configuration for the {@link}LevelGenerator. Either use preset levelConfigs or create your own.
	 * @param fps sets the amount of frames per seconds, bigger values make the game faster (Standard: 24 fps)
	 * @param randomize determines if the level should be randomized based on the levelConfig
	 * @param viewable determines if the evaluation should be viewable (if true fps will be overriden!)
	 * @param debugView determines if additional debug-views should be shown
	 * @param windowMultiplier  multiplier for window height/width (Standard: 320x240)
	 */
	public static void run(Agent agent,LevelConfig levelConfig,int fps,int windowMultiplier, boolean randomize, boolean viewable, boolean debugView) {
		if(agent==null){
			System.err.println("Agent can't be null!\nPlease choose a valid Agent!");
			return;
		}
		if(levelConfig==null&&!randomize) System.err.println("LevelConfig is null, Level will be randomized!");
		if(randomize||levelConfig==null) levelConfig=LevelConfig.randomize(levelConfig);
		if(windowMultiplier<1) windowMultiplier=1;	
		
		RunnerOptions rOptions=new RunnerOptions(agent,levelConfig);

		rOptions.setViewable(viewable);
		rOptions.setFPS(fps);
		rOptions.setWindowHeigth(320*windowMultiplier);
		rOptions.setWindowWidth(240*windowMultiplier);
		rOptions.setDebugView(debugView);

		Task task=new ProgressTask(rOptions);
		
		System.out.println(task.evaluteWithExtendedInfo()); 
	}
	
	/**
	 *  Equals the call of @link #run(Agent, LevelConfig, boolean, int)} with attributes run(agent, levelConfig, randomize, 3).
	 * @param agent Agent to be evaluated
	 * @param levelConfig Configuration for the {@link}LevelGenerator. Either use preset levelConfigs or create your own
	 * @param randomize determines if the level should be randomized based on the levelConfig
	 */
	public static void run(Agent agent,LevelConfig levelConfig,boolean randomize) {		
		run(agent, levelConfig, 24,3,randomize, true,false);
	}
	
	/**
	 *  Hides some attributes of @link #run(Agent, LevelConfig, int,int, boolean, boolean, boolean). Standard values will be used for missing attributes.
	 * @param agent Agent to be evaluated
	 * @param levelConfig Configuration for the {@link}LevelGenerator. Either use preset levelConfigs or create your own
	 * @param randomize determines if the level should be randomized based on the levelConfig
	 * @param windowMultiplier multiplier for window height/width (Standard: 320x240)
	 */
	public static void run(Agent agent,LevelConfig levelConfig,boolean randomize, int windowMultiplier) {		
		run(agent, levelConfig, 24,windowMultiplier,randomize, true,false);
	}
}


