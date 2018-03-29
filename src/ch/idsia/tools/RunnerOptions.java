package ch.idsia.tools;

import java.awt.Point;
import java.util.Random;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.Level.LEVEL_TYPES;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Mario.MODE;

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
	
	private boolean marioAlwaysCentered=false;
	
	private boolean powerRestauration=false;
	
	private MODE marioStartMode = MODE.MODE_FIRE;
	
	private boolean marioInvulnerable=false; //shouldnt be possible for competition
	
	private boolean exitWhenFinished=false;
	
	private boolean viewAlwaysOnTop=true;
	
	private int difficulty=0;
	
	private int levelLength=256;
	
	private int levelSeed=new Random().nextInt(); //random levelSeed per default
	
	private Level.LEVEL_TYPES levelType=Level.LEVEL_TYPES.getLevelTypebyType(new Random().nextInt(3)); //random levelType
	
	private int trials=0,maxTrials=1; //number of trials
	
	private int ZLevelEnemies=1, ZLevelMap=1;
	
	public RunnerOptions(Agent agent) {
		Random r=new Random();
		
		int[] length= {256,512,1024};
		int[] timeLimits= {200,400,800};
		int tmp=r.nextInt(length.length);
		this.levelLength=length[tmp];
		this.timeLimit=timeLimits[tmp];
		
		this.agent=agent;
	}
	
	public RunnerOptions(RunnerOptions toCopy) {
		this.agent=toCopy.agent; //could be problematic
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
		this.difficulty=toCopy.getDifficulty();
		this.levelLength=toCopy.getLevelLength();
		this.levelSeed=toCopy.getLevelSeed();
		this.levelType=toCopy.getLevelType();
		this.trials=toCopy.trials;
		this.maxTrials=toCopy.getMaxTrials();
		this.ZLevelEnemies=toCopy.getZLevelEnemies();
		this.ZLevelMap=toCopy.getZLevelMap();
		this.viewLocation=toCopy.getViewLocation();
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

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getLevelLength() {
		return levelLength;
	}

	public void setLevelLength(int levelLength) {
		this.levelLength = levelLength;
		this.timeLimit=200; //TODO find better way
	}

	public int getLevelSeed() {
		return levelSeed;
	}

	public void setLevelSeed(int levelSeed) {
		this.levelSeed = levelSeed;
	}

	public Level.LEVEL_TYPES getLevelType() {
		return levelType;
	}

	public void setLevelType(Level.LEVEL_TYPES levelType) {
		this.levelType = levelType;
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
	
	@Override
	public String toString() {
		return "RunnerOptions [agent=" + agent + ", FPS=" + FPS + ", viewable=" + viewable + ", timeLimit=" + timeLimit
				+ ", timer=" + timer + ", paused=" + paused + ", powerRestauration=" + powerRestauration
				+ ", marioStartMode=" + marioStartMode + ", marioInvulnerable=" + marioInvulnerable
				+ ", exitWhenFinished=" + exitWhenFinished + ", viewAlwaysOnTop=" + viewAlwaysOnTop + ", difficulty="
				+ difficulty + ", levelLength=" + levelLength + ", levelSeed=" + levelSeed + ", levelType=" + levelType
				+ ", trials=" + trials + ", ZLevelEnemies=" + ZLevelEnemies + ", ZLevelMap=" + ZLevelMap
				+ ", getAgent()=" + getAgent() + ", getFPS()=" + getFPS() + ", isViewable()=" + isViewable()
				+ ", getTimeLimit()=" + getTimeLimit() + ", isTimer()=" + isTimer() + ", isPaused()=" + isPaused()
				+ ", isPowerRestauration()=" + isPowerRestauration() + ", getMarioStartMode()=" + getMarioStartMode()
				+ ", isMarioInvulnerable()=" + isMarioInvulnerable() + ", isExitWhenFinished()=" + isExitWhenFinished()
				+ ", isViewAlwaysOnTop()=" + isViewAlwaysOnTop() + ", getDifficulty()=" + getDifficulty()
				+ ", getLevelLength()=" + getLevelLength() + ", getLevelSeed()=" + getLevelSeed() + ", getLevelType()="
				+ getLevelType() + ", getTrials()=" + getTrials() + ", getZLevelEnemies()=" + getZLevelEnemies()
				+ ", getZLevelMap()=" + getZLevelMap() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
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

	public void setLabels(boolean labels) {
		this.labels = labels;
	}

	
	
}
