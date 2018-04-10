package ch.idsia.tools;

import java.awt.Point;
import java.util.Random;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.Level.LEVEL_TYPES;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Mario.MODE;
import de.novatec.marioai.tools.LevelConfig;

public class RunnerOptions {

	private Agent agent;

	private static final int infiniteFps=100;
	
	private int windowHeigth=320*4,windowWidth=240*4;
	
	private Point viewLocation=new Point(0, 0);
	
	private int FPS=24; //Standard: 24 Frames per Second
	
	private boolean viewable=true; //Game should be watchable 
	
	private int timeLimit=200;
	
	private boolean timer=true;  //timer should be visible
	
	private boolean paused=false;
	
	private boolean labels=false;
	
	private boolean debugView=false;
	
	private boolean marioAlwaysCentered=false;
	
	private boolean powerRestauration=false;
	
	private MODE marioStartMode = MODE.MODE_FIRE;
	
	private boolean marioInvulnerable=false; //shouldnt be possible for competition
	
	private boolean exitWhenFinished=false;
	
	private boolean viewAlwaysOnTop=false;
	
	private LevelConfig config;
	
	private int trials=0,maxTrials=1; //number of trials
	
	private int ZLevelEnemies=1, ZLevelMap=1;

	public RunnerOptions(Agent agent,LevelConfig config) {
		this.config=config;		
		this.agent=agent;
	}
	
	public RunnerOptions(RunnerOptions toCopy) {
		this.agent=toCopy.agent; //could be problematic (but till now it isn't)
		//agent.reset();
		this.windowHeigth=toCopy.getWindowHeigth();
		this.windowWidth=toCopy.getWindowWidth();
		this.FPS=toCopy.FPS;
		this.viewable=toCopy.isViewable();
		this.timeLimit=toCopy.getTimeLimit();
		this.timer=toCopy.isTimer();
		this.paused=toCopy.isPaused();
		this.powerRestauration=toCopy.isPowerRestauration();
		this.marioStartMode=toCopy.getMarioStartMode();
		this.marioInvulnerable=toCopy.isMarioInvulnerable();
		this.exitWhenFinished=toCopy.isExitWhenFinished();
		this.viewAlwaysOnTop=toCopy.isViewAlwaysOnTop();
		this.trials=toCopy.trials;
		this.maxTrials=toCopy.getMaxTrials();
		this.ZLevelEnemies=toCopy.getZLevelEnemies();
		this.ZLevelMap=toCopy.getZLevelMap();
		this.viewLocation=toCopy.getViewLocation();
		this.config=toCopy.config;
		this.debugView=toCopy.debugView;
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

	private void setTimeLimit(int timeLimit) { // shouldn't be changeable
		this.timeLimit = timeLimit;
	}

	public boolean isTimer() {
		return timer;
	}

	public void setTimer(boolean timer) {
		this.timer = timer;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPowerRestauration() {
		return powerRestauration;
	}

	public void setPowerRestauration(boolean powerRestauration) {
		this.powerRestauration = powerRestauration;
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

	public boolean isExitWhenFinished() {
		return exitWhenFinished;
	}

	public void setExitWhenFinished(boolean exitWhenFinished) {
		this.exitWhenFinished = exitWhenFinished;
	}

	public boolean isViewAlwaysOnTop() {
		return viewAlwaysOnTop;
	}

	public void setViewAlwaysOnTop(boolean viewAlwaysOnTop) {
		this.viewAlwaysOnTop = viewAlwaysOnTop;
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

	public static int getInfinitefps() {
		return infiniteFps;
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

	public boolean isMarioAlwaysCentered() {
		return marioAlwaysCentered;
	}

	public void setMarioAlwaysCentered(boolean marioAlwaysCentered) {
		this.marioAlwaysCentered = marioAlwaysCentered;
	}

	public boolean isLabels() {
		return labels;
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

	
	
}
