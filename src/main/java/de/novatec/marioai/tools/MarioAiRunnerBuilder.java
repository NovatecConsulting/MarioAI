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
	 * @return MarioAiRunner using this builder
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
 * @param agents to be added to the builder
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder addAgents(List<Agent> agents) {
		
		this.agents.addAll(agents);
		return this;
	}
	/**
	 * 
	 * @param agent to be added to the builder
	 * @return MarioAiRunnerBuilder, faster use
	 */
	public MarioAiRunnerBuilder addAgent(Agent agent) {
		
		this.agents.add(agent);
		return this;
	}
	
	/**
	 * @return levelConfig currently added to the builder
	 */
	public LevelConfig getLevelConfig() {
		return levelConfig;
	}
	/**
	 * 
	 * @param levelConfig change in the builder
	 * @return MarioAiRunnerBuilder, faster use
	 */
	public MarioAiRunnerBuilder setLevelConfig(LevelConfig levelConfig) {
		if(levelConfig != null)this.levelConfig = levelConfig;
		return this;
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
 * @param task to be set in the builder
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setTask(Task task) {
		if(task != null)this.task = task;
		return this;
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
 * @param fps to be set
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setFps(int fps) {
		if(fps>0)this.fps = fps;
		return this;
	}
/**
 * 
 * @return zoomFactor currently used
 */
	public int getZoomFactor() {
		return zoomFactor;
	}
/**
 * 
 * @param zoomFactor to be set
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setZoomFactor(int zoomFactor) {
		if(zoomFactor>0)this.zoomFactor = zoomFactor;
		return this;
	}
/**
 * 
 * @return randomize in the builder
 */
	public boolean isRandomize() {
		return randomize;
	}
/**
 * 
 * @param randomize to be set
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setRandomize(boolean randomize) {
		this.randomize = randomize;
		return this;
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
 * @param viewable to be set
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setViewable(boolean viewable) {
		this.viewable = viewable;
		return this;
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
 * @param debugView to be set
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setDebugView(boolean debugView) {
		this.debugView = debugView;
		return this;
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
 * @param exitOnFinish to be set
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setExitOnFinish(boolean exitOnFinish) {
		this.exitOnFinish = exitOnFinish;
		return this;
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
 * @param pushMetrics to be set
 * @return MarioAiRunnerBuilder, faster use
 */
	public MarioAiRunnerBuilder setPushMetrics(boolean pushMetrics) {
		this.pushMetrics = pushMetrics;
		return this;
	}
	
}
