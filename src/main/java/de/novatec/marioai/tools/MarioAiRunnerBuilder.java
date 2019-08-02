package de.novatec.marioai.tools;

import java.util.ArrayList;
import java.util.List;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ChallengeTask;
import ch.idsia.ai.tasks.Task;
/**
 * @author rgu
 * @author nmn
 * 
 * Builder pattern for MarioAiRunner
 * 
 */
public class MarioAiRunnerBuilder implements Builder{
	private List<Agent> agents = new ArrayList<Agent>();
	private LevelConfig levelConfig = LevelConfig.ASTAR_KILLER2;
	private Task task = new ChallengeTask(); 
	private int fps = 24; 
	private int zoomFactor = 3; 
	private boolean randomize = false; 
	private boolean viewable = true;
	private boolean debugView = false;
	private boolean exitOnFinish = false; 
	private boolean pushMetrics = false;
	
	/**
	 * @return creates and returns the MarioAiRunner instance using this class' variables
	 */
	public MarioAiRunner construct() {
		return new MarioAiRunner(this);
	}
/**
 * 
 * @return agents currently supposed to be built later
 */
	public List<Agent> getAgents() {
		return agents;
	}
/**
 * 
 * @param adds agents to the builder 
 */
	public void addAgents(List<Agent> agents) {
		
		this.agents.addAll(agents);
	}
	/**
	 * 
	 * @param adds an agent to the builder
	 */
	public void addAgent(Agent agent) {
		
		this.agents.add(agent);
	}
	
	/**
	 * 
	 * @return LevelConfig supposed to be built
	 */
	public LevelConfig getLevelConfig() {
		return levelConfig;
	}
	/**
	 * 
	 * @param set levelConfig from the builder
	 */
	public void setLevelConfig(LevelConfig levelConfig) {
		if(levelConfig != null)this.levelConfig = levelConfig;
	}
/**
 * 
 * @return gets the task from the builder
 */
	public Task getTask() {
		return task;
	}
/**
 * 
 * @param sets the task in the builder
 */
	public void setTask(Task task) {
		if(task != null)this.task = task;
	}
/**
 * 
 * @return fps from the builder
 */
	public int getFps() {
		return fps;
	}
/**
 * 
 * @param sets fps in the builder
 */
	public void setFps(int fps) {
		if(fps>0)this.fps = fps;
	}
/**
 * 
 * @return gets zoomfactor supposed to be built
 */
	public int getZoomFactor() {
		return zoomFactor;
	}
/**
 * 
 * @param sets zoomFactor in the builder
 */
	public void setZoomFactor(int zoomFactor) {
		if(zoomFactor>0)this.zoomFactor = zoomFactor;
	}
/**
 * 
 * @return randomize whether the LevelConfig will be randomized later
 */
	public boolean isRandomize() {
		return randomize;
	}
/**
 * 
 * @param sets randomize in the builder
 */
	public void setRandomize(boolean randomize) {
		this.randomize = randomize;
	}
/**
 * 
 * @return viewable: whether the runner is shown or not
 */
	public boolean isViewable() {
		return viewable;
	}
/**
 * 
 * @param sets viewable in the runner
 */
	public void setViewable(boolean viewable) {
		this.viewable = viewable;
	}
/**
 * 
 * @return gets the state of debugView
 */
	public boolean isDebugView() {
		return debugView;
	}
/**
 * 
 * @param debugView sets the state of debugView
 */
	public void setDebugView(boolean debugView) {
		this.debugView = debugView;
	}
/**
 * 
 * @return exitOnFinish 
 */
	public boolean isExitOnFinish() {
		return exitOnFinish;
	}
/**
 * 
 * @param exitOnFinish 
 */
	public void setExitOnFinish(boolean exitOnFinish) {
		this.exitOnFinish = exitOnFinish;
	}
/**
 * 
 * @return gets pushMetrics
 */
	public boolean isPushMetrics() {
		return pushMetrics;
	}
/**
 * 
 * @param sets pushMetrics
 */
	public void setPushMetrics(boolean pushMetrics) {
		this.pushMetrics = pushMetrics;
	}
	
}
