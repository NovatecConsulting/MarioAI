package ch.idsia.tools;

import java.awt.Point;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.level.Level.LEVEL_TYPES;
import ch.idsia.mario.engine.sprites.Mario.MODE;
import de.novatec.marioai.tools.LevelConfig;

public class RunnerOptions {

	private final Agent agent;
	
	private int windowHeigth=320*4,windowWidth=240*4;
	
	private Point viewLocation=new Point(0, 0);
	
	private int FPS=24; //Standard: 24 Frames per Second
	
	private boolean viewable=true; //Game should be viewable 
	
	private int timeLimit=200;
	
	private boolean paused=false;
	
	private boolean debugView=false;
	
	private MODE marioStartMode = MODE.MODE_FIRE;
	
	private boolean marioInvulnerable=false; //shouldn't be possible for competition
	
	private LevelConfig config;
	
	private final Task task;
	
	private int trials=0,maxTrials=1; //number of trials
	
	private int ZLevelEnemies=1, ZLevelMap=1;

	public RunnerOptions(Agent agent,LevelConfig config, Task task) {
		this.config=config;		
		this.agent=agent;
		this.task=task;
	}
	
	public RunnerOptions(RunnerOptions toCopy) {
		this.agent=toCopy.agent; //could be problematic (but till now it isn't)
		//agent.reset();
		this.windowHeigth=toCopy.getWindowHeigth();
		this.windowWidth=toCopy.getWindowWidth();
		this.FPS=toCopy.FPS;
		this.viewable=toCopy.isViewable();
		this.timeLimit=toCopy.getTimeLimit();
		this.paused=toCopy.isPaused();
		this.marioStartMode=toCopy.getMarioStartMode();
		this.marioInvulnerable=toCopy.isMarioInvulnerable();
		this.trials=toCopy.trials;
		this.maxTrials=toCopy.getMaxTrials();
		this.ZLevelEnemies=toCopy.getZLevelEnemies();
		this.ZLevelMap=toCopy.getZLevelMap();
		this.config=toCopy.config;
		this.debugView=toCopy.debugView;
		this.task=toCopy.task;
	}
	
	public RunnerOptions getCopyWithNewAgent(Agent agent) {
		RunnerOptions res= new RunnerOptions(agent, this.config, this.task);
		res.windowHeigth=this.getWindowHeigth();
		res.windowWidth=this.getWindowWidth();
		res.FPS=this.FPS;
		res.viewable=this.isViewable();
		res.timeLimit=this.getTimeLimit();
		res.paused=this.isPaused();
		res.marioStartMode=this.getMarioStartMode();
		res.marioInvulnerable=this.isMarioInvulnerable();
		res.trials=this.trials;
		res.maxTrials=this.getMaxTrials();
		res.ZLevelEnemies=this.getZLevelEnemies();
		res.ZLevelMap=this.getZLevelMap();
		res.debugView=this.debugView;
		
		return res;
	}
	
	public Agent getAgent() {
		return agent;
	}

	public int getFPS() {
		return FPS;
	}

	public void setFPS(int FPS) {
		this.FPS = FPS;
	}

	public int getWindowHeigth() {
		return windowHeigth;
	}

	public void setWindowHeigth(int windowHeigth) {
		this.windowHeigth = windowHeigth;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public boolean isViewable() {
		return viewable;
	}

	public void setViewable(boolean viewable) {
		this.viewable = viewable;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	@SuppressWarnings("unused")
	private void setTimeLimit(int timeLimit) { // shouldn't be changeable
		this.timeLimit = timeLimit;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public MODE getMarioStartMode() {
		return marioStartMode;
	}

	public void setMarioStartMode(MODE marioStartMode) {
		this.marioStartMode = marioStartMode;
	}

	public boolean isMarioInvulnerable() {
		return marioInvulnerable;
	}

	public void setMarioInvulnerable(boolean marioInvulnerable) { //TODO make private
		this.marioInvulnerable = marioInvulnerable;
	}

	public int getTrials() {
		return trials;
	}

	public void setTrials(int trials) {
		this.trials = trials;
	}

	public int getZLevelEnemies() {
		return ZLevelEnemies;
	}

	public void setZLevelEnemies(int zLevelEnemies) {
		ZLevelEnemies = zLevelEnemies;
	}

	public int getZLevelMap() {
		return ZLevelMap;
	}

	public void setZLevelMap(int zLevelMap) {
		ZLevelMap = zLevelMap;
	}

	public Point getViewLocation() {
		return viewLocation;
	}

	public void setViewLocation(Point viewLocation) {
		this.viewLocation = viewLocation;
	}

	public int incrementTrials() {
		return trials++;
	}

	public int getMaxTrials() {
		return maxTrials;
	}

	public void setMaxTrials(int maxTrials) {
		this.maxTrials = maxTrials;
	}

	public LEVEL_TYPES getLevelType() {
		return config.getType();
	}
	
	public int getDifficulty() {
		return config.getPresetDifficulty();
	}
	
	public int getLevelSeed() {
		return config.getSeed();
	}
	
	public int getLevelLength() {
		return config.getLength();
	}

	public LevelConfig getConfig() {
		return config;
	}

	public boolean isDebugView() {
		return debugView;
	}

	public void setDebugView(boolean debugView) {
		this.debugView = debugView;
	}

	public Task getTask() {
		return task;
	}
	
}
