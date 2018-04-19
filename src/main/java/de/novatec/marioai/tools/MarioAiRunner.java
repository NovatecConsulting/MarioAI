package de.novatec.marioai.tools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ChallengeTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.RunnerOptions;
import ch.idsia.tools.MainFrame;
import de.novatec.marioai.agents.ExampleAgent;
import de.novatec.marioai.agents.HumanKeyboardAgent;

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
	 * @param viewable determines if the evaluation should be viewable (if true fps will be overridden!)
	 * @param debugView determines if additional debug-views should be shown
	 * @param zoomFactor  multiplier for window height/width (Standard: 320x240)
	 */
	public static void run(Agent agent,LevelConfig levelConfig,int fps,int zoomFactor, boolean randomize, boolean viewable, boolean debugView) {
		List<Agent> tmp=new ArrayList<>();
		tmp.add(agent);
		multiAgentRun(tmp, levelConfig, new ChallengeTask(), fps, zoomFactor, randomize, viewable, debugView);
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
	 * @param zoomFactor multiplier for window height/width (Standard: 320x240)
	 */
	public static void run(Agent agent,LevelConfig levelConfig,boolean randomize, int zoomFactor) {		
		run(agent, levelConfig, 24,zoomFactor,randomize, true,false);
	}
	
	public static void multiAgentRun(List<Agent> agents, LevelConfig levelConfig,Task task,int fps,int zoomFactor, boolean randomize, boolean viewable, boolean debugView) {
		if(agents==null){
			System.err.println("Agents List can't be null!\nPlease use a proper List with agents!");
			return;
		}
		if(agents.isEmpty()) {
			System.err.println("Agents List is empty - no evaluation started");
			return;
		}
		if(levelConfig==null&&!randomize) System.err.println("LevelConfig is null, Level will be randomized!");
		if(randomize||levelConfig==null) levelConfig=LevelConfig.randomize(levelConfig);
		if(task==null) {
			System.err.println("Task can't be null");
			System.err.println("Exiting...");
			return;
		}
		if(zoomFactor<1) zoomFactor=1;	
		
		RunnerOptions baseOptions=new RunnerOptions(agents.get(0),levelConfig,task);

		baseOptions.setViewable(viewable);
		baseOptions.setFPS(fps);
		baseOptions.setWindowHeigth(240*zoomFactor);
		baseOptions.setWindowWidth(320*zoomFactor);
		baseOptions.setDebugView(debugView);
		
		try {
			ExecutorService runner = Executors.newWorkStealingPool();
			MainFrame configurator=new MainFrame(agents.size(), false, viewable, new Point());
			
			List<Future<EvaluationInfo>> results= new ArrayList<>();

			for(Agent next: agents) {
				Evaluator ev=new Evaluator(baseOptions.getCopyWithNewAgent(next),configurator);
				results.add(runner.submit(ev));
			}
			
			System.out.println("waiting for results...");
			
			for(Future<EvaluationInfo> next: results) {
				EvaluationInfo info=next.get();
				System.out.println("---Agent: "+info.agentName+" finished---");
				System.out.println(info);
			}
			
			runner.shutdown();
			if(!baseOptions.isViewable())System.exit(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void main (String[] args) {
		List<Agent> tmp=new ArrayList<>();
		tmp.add(new HumanKeyboardAgent());
		tmp.add(new ExampleAgent());
		tmp.add(new ExampleAgent());
		tmp.add(new ExampleAgent());
		multiAgentRun(tmp, LevelConfig.LEVEL_3, new ChallengeTask(), 24, 10, true, true, false);
	}
}


