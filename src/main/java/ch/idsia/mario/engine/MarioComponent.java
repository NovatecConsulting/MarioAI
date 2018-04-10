package ch.idsia.mario.engine;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Mario.STATUS;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.KeyboardInterpreter;
import ch.idsia.tools.RunnerOptions;
import de.novatec.mario.engine.generalization.Coordinates;
import de.novatec.mario.engine.generalization.Entity;
import de.novatec.mario.engine.generalization.Tile;
import de.novatec.marioai.tools.MarioNtAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;


public class MarioComponent extends JComponent implements Runnable, FocusListener, Environment {
    private static final long serialVersionUID = 790878775993203817L;

    private boolean running = false;
    private int width, height;
    private GraphicsConfiguration graphicsConfiguration;
    private RunnerOptions rOptions;
    
    private boolean debugView;
    private boolean viewable;

    private int frame;
    private int delay;
    private int ZLevelEnemies = 1;
    private int ZLevelScene = 1;

    //private CheaterKeyboardAgent cheatAgent = null;

    private KeyListener prevHumanKeyBoardAgent;
    private LevelScene levelScene = null;
 
    public MarioComponent(int width, int height) {
       
        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width; 
        this.height = height;
        
        Dimension size = new Dimension(width, height);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        new KeyboardInterpreter(this);
    }
    
    //--- Copy Constructor
    public MarioComponent(LevelScene alreadyCopied,MarioComponent toCopy) {

        this.width = toCopy.width;
        this.height = toCopy.height;
    	this.setFocusable(true);
        this.setEnabled(true);
       
        Dimension size = new Dimension(width, height);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        this.running = toCopy.running;
		this.graphicsConfiguration = toCopy.graphicsConfiguration; //ready only
		this.rOptions = new RunnerOptions(toCopy.rOptions);
		this.frame = toCopy.frame;
		
		this.delay = toCopy.delay;
		ZLevelEnemies = toCopy.ZLevelEnemies;
		ZLevelScene = toCopy.ZLevelScene;
		
		this.levelScene = alreadyCopied;
		
		this.debugView=toCopy.debugView;
		this.viewable=toCopy.viewable;
		
		for(KeyListener listener:toCopy.getKeyListeners()) this.registerKeyboardListener(listener);
    }

	public void adjustFPS() { //MAKING MARIO FASTER! SMALLER DELAY=FASTER RUNNING
    	if(rOptions.isViewable()) delay = (rOptions.getFPS() > 0) ? (rOptions.getFPS() >= RunnerOptions.getInfinitefps()) ? 0 : (1000/rOptions.getFPS()) : 100;
    	else delay=0;
    }
    
    public boolean mayShoot() {
    	return levelScene.getFireballsOnScreen()<2&&(levelScene.getMarioMode()==Mario.MODE.MODE_FIRE);
    }
    
    //--- JComponent
    public void paint(Graphics g) {
    }

    public void update(Graphics g) {
    }

    public void init() {
        graphicsConfiguration = getGraphicsConfiguration();
        if (graphicsConfiguration != null) {
            Art.init(graphicsConfiguration);
        }
    }

    //--- Runnable
    public void stop() {
        running = false;
    }

    public void run() {

    }

    public EvaluationInfo run1(int currentTrial, int totalNumberOfTrials) {
        running = true;
        adjustFPS();
        EvaluationInfo evaluationInfo = new EvaluationInfo();

        VolatileImage image = null;
        Graphics g = null;
        Graphics og = null;

        image = createVolatileImage(320, 240);
        g = getGraphics();
        og = image.getGraphics();

        if (!rOptions.isViewable()) {
            String msgClick = "Vizualization is not available";
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
        }

        addFocusListener(this);

        long startTime = System.currentTimeMillis();  // Remember the starting time
        STATUS marioStatus = STATUS.RUNNING;

        int totalActionsPerfomed = 0;

        while (/*Thread.currentThread() == animator*/ running) {
            levelScene.tick();
            float alpha = 0;
            
            if (rOptions.isViewable()) {
                og.fillRect(0, 0, 320, 240);
                levelScene.render(og, alpha);
            }
            boolean[] action = {false,false,false,false,false};

            	if(!levelScene.isPaused()) action = getAgent().getAction(this);
            	
            	if(debugView&&getAgent() instanceof MarioNtAgent&&levelScene.getMarioStatus()==STATUS.RUNNING)((MarioNtAgent)getAgent()).debugDraw(og, this);
           
            if (action != null)
            {
              if(!levelScene.isPaused()) for (int i = 0; i < Environment.numberOfButtons; ++i)
                    if (action[i])
                    {
                        ++totalActionsPerfomed;
                        break;
                    }
            }
            else
            {
                System.err.println("Null Action received. Skipping simulation...");
                stop();
            }

            if(!levelScene.isPaused()) levelScene.setMarioKeys(action);

            if (rOptions.isViewable()) {
                if(running) {
                	 String msg = "Agent: " + getAgent().getName();
                     LevelScene.drawStringDropShadow(og, msg, 0, 7, 5); // DEBUG MESSAGES

                     msg = "Selected Actions: ";
                     LevelScene.drawStringDropShadow(og, msg, 0, 8, 6);

                     msg = "";
                     if (action != null)
                     {
                         for (int i = 0; i < Environment.numberOfButtons; ++i)
                             msg += (action[i]) ? Scene.keysStr[i] : "      ";
                     }
                     else
                         msg = "NULL";                    
                     drawString(og, msg, 6, 78, 1);

                     og.setColor(Color.DARK_GRAY);
                	 LevelScene.drawStringDropShadow(og, "FPS: ", 33, 2, 7);
                	 LevelScene.drawStringDropShadow(og, ((rOptions.getFPS() > 99) ? "\\infty" : ""+rOptions.getFPS()), 33, 3, 7);
                     LevelScene.drawStringDropShadow(og, "Score: "+(int)levelScene.getScore(), 1,27, 4);
                }

                g.drawImage(image, 0, 0, width, height, null); // set size to frame size
               
                } else {
                // Win or Die without renderer!! independently.
                marioStatus =  levelScene.getMarioStatus();
                if (marioStatus != STATUS.RUNNING)
                    stop();
            }
           
            // Delay depending on how far we are behind.
            if (delay > 0)
                try {
                    startTime += delay;
                    Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }
            // Advance the frame
            frame++;
        } 
        
        //--- Show results on end screen
        if (rOptions.isViewable()) {
        	drawEndScreen(g,og,image);
        }
        
        //ADD INFO TO EVALUATION INFO
        evaluationInfo.agentType = getAgent().getClass().getSimpleName();
        evaluationInfo.agentName = getAgent().getName();
        evaluationInfo.marioStatus =  levelScene.getMarioStatus();
        evaluationInfo.lengthOfLevelPassedPhys = levelScene.getMarioX();
        evaluationInfo.lengthOfLevelPassedCells =  levelScene.getMarioMapX();
        evaluationInfo.levelXExit=levelScene.getLevelXExit();
        evaluationInfo.totalLengthOfLevelCells = levelScene.getLevelWidth();
        evaluationInfo.totalLengthOfLevelPhys = levelScene.getLevelWidthPhys();
        evaluationInfo.timeSpentOnLevel = levelScene.getStartTime();
        evaluationInfo.timeLeft = levelScene.getTimeLeft();
        evaluationInfo.totalTimeGiven = levelScene.getTotalTime();
        evaluationInfo.numberOfGainedCoins = levelScene.getMarioCoins();
//      evaluationInfo.totalNumberOfCoins   = -1 ; // TODO: total Number of coins.
        evaluationInfo.totalActionsPerfomed = totalActionsPerfomed; // Counted during the play/simulation process
        evaluationInfo.totalFramesPerfomed = frame;
        evaluationInfo.marioMode = levelScene.getMarioMode();
        
        evaluationInfo.setKillsTotal(levelScene.getKilledCreaturesTotal());
        evaluationInfo.setKilledCreaturesbyStomp(levelScene.getKilledCreaturesByStomp());
        evaluationInfo.setKilledCreaturesbyShell(levelScene.getKilledCreaturesByShell());
        evaluationInfo.setKilledCreaturesbyFire(levelScene.getKilledCreaturesByFireBall());
        
        evaluationInfo.setGainedMushrooms(levelScene.getMarioGainedMushrooms());
        evaluationInfo.setGainedFlower(levelScene.getMarioGainedFowers());
        
        evaluationInfo.setTimesHurt(levelScene.getTimesMarioHurt());

        return evaluationInfo;
    }
    
    private void drawEndScreen(Graphics g, Graphics og, VolatileImage image) {
    	final DecimalFormat df = new DecimalFormat("0.0");
        final int start=4;
        int actualRow=3;
        LevelScene.drawStringDropShadow(og, "Results: ", 1, actualRow++, 1);
        LevelScene.drawStringDropShadow(og, "       Agent: "+getAgent().getName(), start, actualRow++, 2);
        if(getAgent().getClass().getSimpleName().length()<18)LevelScene.drawStringDropShadow(og, "     of Type: "+getAgent().getClass().getSimpleName()+".class", start, actualRow++, 2);
        else { 
        	LevelScene.drawStringDropShadow(og, "     of Type: ", start, actualRow++, 2);
        	LevelScene.drawStringDropShadow(og,"   "+getAgent().getClass().getSimpleName()+".class", start, actualRow++, 2);
        }
        actualRow++;
        LevelScene.drawStringDropShadow(og, "Mario Status: "+levelScene.getMarioStatus(), start, actualRow++, 1);
        actualRow++;
        LevelScene.drawStringDropShadow(og, "       Level: "+levelScene.getLevelSeed(), start, actualRow++, 4);
        LevelScene.drawStringDropShadow(og, " Distance to", start,actualRow++,4);
        LevelScene.drawStringDropShadow(og, "      target: "+levelScene.getLevelXExit(), start, actualRow++, 4);
        LevelScene.drawStringDropShadow(og, "     -passed: "+levelScene.getMarioMapX()+" ("+(df.format((double)levelScene.getMarioMapX()/(double)levelScene.getLevelXExit()*100))+"%)", start, actualRow++, 4);
        actualRow++;
        LevelScene.drawStringDropShadow(og, "  Total Time: "+levelScene.getTotalTime(), start, actualRow++, 4);
        LevelScene.drawStringDropShadow(og, "     -passed: "+levelScene.getStartTime(), start, actualRow++, 4);
        LevelScene.drawStringDropShadow(og, "     -  left: "+levelScene.getTimeLeft(), start, actualRow++, 4);
        actualRow++;
        actualRow++;
        
        LevelScene.drawStringDropShadow(og, "       Kills: "+levelScene.getKilledCreaturesTotal(), start-6, actualRow, 6);
        LevelScene.drawStringDropShadow(og, "    Coins: "+levelScene.getMarioCoins(), start+19, actualRow++, 6);
        
        if(levelScene.getKilledCreaturesByStomp()>0) LevelScene.drawStringDropShadow(og, "    by stomp: "+levelScene.getKilledCreaturesByStomp()+" ("+(df.format((double)levelScene.getKilledCreaturesByStomp()/(double)levelScene.getKilledCreaturesTotal()*100))+"%)", start-6, actualRow, 6);
        else LevelScene.drawStringDropShadow(og, "    by stomp: 0",start-6,actualRow,6);
        	
        LevelScene.drawStringDropShadow(og, "Mushrooms: "+levelScene.getMarioGainedMushrooms(), start+19, actualRow++, 6);
        
        if(levelScene.getKilledCreaturesByShell()>0) LevelScene.drawStringDropShadow(og, "    by shell: "+levelScene.getKilledCreaturesByShell()+" ("+(df.format((double)levelScene.getKilledCreaturesByShell()/(double)levelScene.getKilledCreaturesTotal()*100))+"%)", start-6, actualRow, 6);
        else LevelScene.drawStringDropShadow(og, "    by shell: 0",start-6,actualRow,6);
        LevelScene.drawStringDropShadow(og, "  Flowers: "+levelScene.getMarioGainedFowers(), start+19, actualRow++, 6);
        
        if(levelScene.getKilledCreaturesByFireBall()>0) LevelScene.drawStringDropShadow(og, "    by  fire: "+levelScene.getKilledCreaturesByFireBall()+" ("+(df.format((double)levelScene.getKilledCreaturesByFireBall()/(double)levelScene.getKilledCreaturesTotal()*100))+"%)", start-6, actualRow++, 6);
        else LevelScene.drawStringDropShadow(og, "     by fire: 0",start-6,actualRow,6);
        actualRow++;       
        
        LevelScene.drawStringDropShadow(og, "----------------------------------", start-2, actualRow++, 1);
        LevelScene.drawStringDropShadow(og, "Score: "+(int)(levelScene.getScore()), start-2, actualRow++, 1);
        g.drawImage(image, 0, 0, width, height, null);
    }

    @Override
	public String toString() {
		return "MarioComponent [running=" + running + ", width=" + width + ", height=" + height
				+  ", frame=" + frame
				+ ", delay=" + delay + ", ZLevelEnemies=" + ZLevelEnemies + ", ZLevelScene=" + ZLevelScene
				+ ", prevHumanKeyBoardAgent="
				+ prevHumanKeyBoardAgent + "]";
	}

	private void drawString(Graphics g, String text, int x, int y, int c) {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }

    public void startLevel(long seed, int difficulty, Level.LEVEL_TYPES type, int levelLength, int timeLimit) {
        levelScene = new LevelScene(graphicsConfiguration, this, seed, difficulty, type, levelLength, timeLimit);
        levelScene.init();
    }

    public void levelFailed() {
        stop();
    }

    public void focusGained(FocusEvent arg0) {
    }

    public void focusLost(FocusEvent arg0) {
    }

    public void levelWon() {
        stop();
    }

    public List<String> getTextObservation(boolean Enemies, boolean LevelMap, boolean Complete, int ZLevelMap, int ZLevelEnemies) {
            return levelScene.LevelSceneAroundMarioASCII(Enemies, LevelMap, Complete, ZLevelMap, ZLevelEnemies);
    }

    // Changing ZLevel during the game on-the-fly;
    public byte[][] getMergedObservationZ(int zLevelScene, int zLevelEnemies) {
            return levelScene.mergedObservation(zLevelScene, zLevelEnemies);
    }

    public byte[][] getLevelSceneObservationZ(int zLevelScene) {
            return levelScene.levelSceneObservation(zLevelScene);
    }

    public byte[][] getEnemiesObservationZ(int zLevelEnemies) {
            return levelScene.enemiesObservation(zLevelEnemies);
    }

    public int getKillsTotal() {
    	return levelScene.getKilledCreaturesTotal();
    }

    public int getKillsByFire() {
    	return levelScene.getKilledCreaturesByFireBall();
    }

    public int getKillsByStomp() {
    		 return levelScene.getKilledCreaturesByStomp();
    }

    public int getKillsByShell() {
    		 return levelScene.getKilledCreaturesByShell();
    }

    public byte[][] getCompleteObservation() {
            return levelScene.mergedObservation(this.ZLevelScene, this.ZLevelEnemies);
    }

    public byte[][] getEnemiesObservation() {
            return levelScene.enemiesObservation(this.ZLevelEnemies);
    }

    public byte[][] getLevelSceneObservation() {
            return levelScene.levelSceneObservation(this.ZLevelScene);
    }

    public boolean isMarioOnGround() {
        return levelScene.isMarioOnGround();
    }

    public boolean mayMarioJump() {
        return levelScene.mayMarioJump();
    }
    
    public boolean isFalling() {
    	return this.levelScene.isMarioFalling();
    }

    public void setPaused(boolean paused) {
        levelScene.setPaused(paused);
    } 

    public void setZLevelEnemies(int ZLevelEnemies) {
        this.ZLevelEnemies = ZLevelEnemies;
    }

    public void setZLevelScene(int ZLevelScene) {
        this.ZLevelScene = ZLevelScene;
    }

    public float[] getMarioFloatPos() {
        return new float[]{((LevelScene) levelScene).getMarioX(), ((LevelScene) levelScene).getMarioY()};
    }

    public float[] getEnemiesFloatPos() {
            return levelScene.enemiesFloatPos();
    }

    public Mario.MODE getMarioMode(){
        return levelScene.getMarioMode();
    }

    public boolean isMarioCarrying() {
        return levelScene.getMarioCarried() != null;
    }

	@Override
	public Map<Coordinates, Tile> getTiles() {
			return levelScene.getTiles();
	}

	@Override
	public Map<Coordinates, List<Entity>> getEntities() {
			return levelScene.getEntities();
	}
	

	public RunnerOptions getRunnerOptions() {
		return rOptions;
	}

	@Override
	public void setRunnerOptions(RunnerOptions rOptions) {
		this.rOptions=rOptions;
		this.viewable=rOptions.isViewable();
		this.debugView=rOptions.isDebugView();
		rOptions.getAgent().reset();
		adjustFPS();
		 
		registerKeyListenerAgent(rOptions.getAgent());
		
		startLevel(rOptions.getLevelSeed(), rOptions.getDifficulty(), rOptions.getLevelType(), rOptions.getLevelLength(), rOptions.getTimeLimit());
	}
	
	public Agent getAgent() {
		return rOptions.getAgent();
	}
	
	@Override
	public void registerKeyboardListener(KeyListener listener) {
		this.addKeyListener(listener);
	}
	
  	private void registerKeyListenerAgent(Agent agent) {
        if (agent instanceof KeyListener) {
        	
            if (prevHumanKeyBoardAgent != null) this.removeKeyListener(prevHumanKeyBoardAgent);
            
            this.prevHumanKeyBoardAgent = (KeyListener) agent;
            registerKeyboardListener(this.prevHumanKeyBoardAgent);;
        }
    }

	@Override
	public LevelScene getLevelScene() {
		return levelScene;
	}

	@Override
	public void togglePaused() {
		levelScene.togglePaused();
	}

	@Override
	public int getMarioX() {
		return levelScene.getMarioMapX();
	}

	@Override
	public int getMarioY() {
		return levelScene.getMarioMapY();
	}

	@Override
	public boolean isDebugView() {
		return debugView;
	}

	@Override
	public void setDebugView(boolean debugView) {		
		this.debugView=debugView;
	}

	@Override
	public void toggleDebugView() {
		debugView=!debugView;
	}

	@Override
	public void showMarioViewAsAscii() {
		byte[][] tmp=getCompleteObservation();
		System.out.println(" --------------------------------Marios Receptive Field:--------------------------------");
    	for(int i=0;i<tmp.length;i++) {
    		for(int j=0;j<tmp[i].length;j++) {
    			if(i==11&&j==11) System.out.print("   M");
    			
    			else if(tmp[i][j]<10&&tmp[i][j]>=0)  System.out.print("   "+tmp[i][j]);
	    			
	    			 else if(tmp[i][j]<0) System.out.print(" "+tmp[i][j]);
	    				  else System.out.print("  "+tmp[i][j]);
    		}
    		System.out.println();
    	}
    	System.out.println(" ----------------------------------------------------------------------------------------");
    	System.out.println();
		
	}
}