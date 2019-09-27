package de.novatec.marioai.tools;

import java.util.ArrayList;
import java.util.List;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ChallengeTask;
import ch.idsia.ai.tasks.Task;
/**
 * 
 * Builder pattern for MarioAiRunner
 * 
 */
public class MarioAiRunnerBuilder implements Builder<MarioAiRunner>{
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
	
	public MarioAiRunner construct() {
		return new MarioAiRunner(this);
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public MarioAiRunnerBuilder addAgents(List<Agent> agents) {
		
		this.agents.addAll(agents);
		return this;
	}

	public MarioAiRunnerBuilder addAgent(Agent agent) {
		
		this.agents.add(agent);
		return this;
	}
	
	public LevelConfig getLevelConfig() {
		return levelConfig;
	}

	public MarioAiRunnerBuilder setLevelConfig(LevelConfig levelConfig) {
		if(levelConfig != null) {
			this.levelConfig = levelConfig;
		}
		return this;
	}

	public Task getTask() {
		return task;
	}

	public MarioAiRunnerBuilder setTask(Task task) {
		if(task != null) {
			this.task = task;
		}
		return this;
	}

	public int getFps() {
		return fps;
	}

	public MarioAiRunnerBuilder setFps(int fps) {
		if(fps>0)this.fps = fps;
		return this;
	}

	public int getZoomFactor() {
		return zoomFactor;
	}

	public MarioAiRunnerBuilder setZoomFactor(int zoomFactor) {
		if(zoomFactor>0) {
			this.zoomFactor = zoomFactor;
		}
		return this;
	}

	public boolean isRandomize() {
		return randomize;
	}

	public MarioAiRunnerBuilder setRandomize(boolean randomize) {
		this.randomize = randomize;
		return this;
	}

	public boolean isViewable() {
		return viewable;
	}

	public MarioAiRunnerBuilder setViewable(boolean viewable) {
		this.viewable = viewable;
		return this;
	}

	public boolean isDebugView() {
		return debugView;
	}

	public MarioAiRunnerBuilder setDebugView(boolean debugView) {
		this.debugView = debugView;
		return this;
	}

	public boolean isExitOnFinish() {
		return exitOnFinish;
	}

	public MarioAiRunnerBuilder setExitOnFinish(boolean exitOnFinish) {
		this.exitOnFinish = exitOnFinish;
		return this;
	}

	public boolean isPushMetrics() {
		return pushMetrics;
	}

	public MarioAiRunnerBuilder setPushMetrics(boolean pushMetrics) {
		this.pushMetrics = pushMetrics;
		return this;
	}
	
	public MarioAiRunnerBuilder removeAgent(Agent agent) {
		agents.remove(agent);
		return this;
	}
	
	public MarioAiRunnerBuilder removeAgents(List<Agent> agents) {
		this.agents.removeAll(agents);
		return this;
	}
}
