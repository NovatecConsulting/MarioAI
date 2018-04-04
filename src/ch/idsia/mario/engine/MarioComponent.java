package ch.idsia.mario.engine;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.CheaterKeyboardAgent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.Level.LEVEL_TYPES;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Mario.STATUS;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.GameViewer;
import ch.idsia.tools.RunnerOptions;
import de.novatec.mario.engine.generalization.Coordinates;
import de.novatec.mario.engine.generalization.Entity;
import de.novatec.mario.engine.generalization.Tile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MarioComponent extends JComponent implements Runnable, /*KeyListener,*/ FocusListener, Environment {
    private static final long serialVersionUID = 790878775993203817L;

    private boolean running = false;
    private int width, height;
    private GraphicsConfiguration graphicsConfiguration;
    private RunnerOptions rOptions;

    private int frame;
    private int delay;
    private int ZLevelEnemies = 1;
    private int ZLevelScene = 1;

    public void setGameViewer(GameViewer gameViewer) {
        this.gameViewer = gameViewer;
    }

    private GameViewer gameViewer = null;
    private CheaterKeyboardAgent cheatAgent = null;

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

        
        if (this.cheatAgent == null)
        {
            this.cheatAgent = new CheaterKeyboardAgent();
            this.addKeyListener(cheatAgent);
        }        
        
    }
    
    public MarioComponent(LevelScene alreadyCopied,MarioComponent toCopy) {

        this.width = toCopy.width;
        this.height = toCopy.height;
    	this.setFocusable(true);
        this.setEnabled(true);
       
        Dimension size = new Dimension(width, height);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        if (this.cheatAgent == null)
        {
            this.cheatAgent = new CheaterKeyboardAgent();
            this.addKeyListener(cheatAgent);
        }        
        
        this.running = toCopy.running;
		this.graphicsConfiguration = toCopy.graphicsConfiguration; //ready only
		this.rOptions = new RunnerOptions(toCopy.rOptions);
		this.frame = toCopy.frame;
		this.delay = toCopy.delay;
		ZLevelEnemies = toCopy.ZLevelEnemies;
		ZLevelScene = toCopy.ZLevelScene;
		this.gameViewer = toCopy.gameViewer;
		
		this.levelScene = alreadyCopied;
    }

	public void adjustFPS() { //MAKING MARIO FASTER! SMALLER DELAY=FASTER RUNNING
    	if(rOptions.isViewable()) delay = (rOptions.getFPS() > 0) ? (rOptions.getFPS() >= RunnerOptions.getInfinitefps()) ? 0 : (1000 /rOptions.getFPS()) : 100;
    	else delay=0;
    }
    
    public boolean mayShoot() {
    	return levelScene.getFireballsOnScreen()<2&&(levelScene.getMarioMode()==Mario.MODE.MODE_FIRE);
    }
    
    public void paint(Graphics g) {
    }

    public void update(Graphics g) {
    }

    public void init() {
        graphicsConfiguration = getGraphicsConfiguration();
//        if (graphicsConfiguration != null) {
            Art.init(graphicsConfiguration);
//        }
    }

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

       
        long tm = System.currentTimeMillis();  // Remember the starting time
        STATUS marioStatus = STATUS.RUNNING;
        
        
        
        int totalActionsPerfomed = 0;
// TODO: Manage better place for this:
        levelScene.resetMarioCoins();

        while (/*Thread.currentThread() == animator*/ running) {
            // Display the next frame of animation.
//                repaint();
            levelScene.tick();
            if (gameViewer != null && gameViewer.getContinuousUpdatesState())
                gameViewer.tick();

            float alpha = 0;

//            og.setColor(Color.RED);
            if (rOptions.isViewable()) {
                og.fillRect(0, 0, 320, 240);
                levelScene.render(og, alpha);
            }
            boolean[] action = {false,false,false,false,false};
            if(!levelScene.isPaused()) action = getAgent().getAction(this);
            
            if (action != null)
            {
                for (int i = 0; i < Environment.numberOfButtons; ++i)
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

             levelScene.setMarioKeys(action);
             levelScene.setMarioCheatKeys(cheatAgent.getAction(this)); 

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
                     LevelScene.drawStringDropShadow(og, "Score: "+levelScene.getScore(), 1,27, 4);
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
                    tm += delay;
                    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
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
        evaluationInfo.totalLengthOfLevelCells = levelScene.getLevelWidth();
        evaluationInfo.totalLengthOfLevelPhys = levelScene.getLevelWidthPhys();
        evaluationInfo.timeSpentOnLevel = levelScene.getStartTime();
        evaluationInfo.timeLeft = levelScene.getTimeLeft();
        evaluationInfo.totalTimeGiven = levelScene.getTotalTime();
        evaluationInfo.numberOfGainedCoins = levelScene.getMarioCoins();
//        evaluationInfo.totalNumberOfCoins   = -1 ; // TODO: total Number of coins.
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
        
//        evaluationInfo.Memo = "Number of attempt: " + Mario.numberOfAttempts;

        return evaluationInfo;
    }
    
    private void drawEndScreen(Graphics g,Graphics og, VolatileImage image) {
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
        LevelScene.drawStringDropShadow(og, "   of Length: "+levelScene.getLevelWidth(), start, actualRow++, 4);
        LevelScene.drawStringDropShadow(og, "     -passed: "+levelScene.getMarioMapX()+" ("+(df.format((double)levelScene.getMarioMapX()/(double)levelScene.getLevelWidth()*100))+"%)", start, actualRow++, 4);
        actualRow++;
        LevelScene.drawStringDropShadow(og, "  Total Time: "+levelScene.getTotalTime(), start, actualRow++, 4);
        LevelScene.drawStringDropShadow(og, "     -passed: "+levelScene.getStartTime(), start, actualRow++, 4);
        LevelScene.drawStringDropShadow(og, "     -  left: "+levelScene.getTimeLeft(), start, actualRow++, 4);
        actualRow++;
        actualRow++;
        
        LevelScene.drawStringDropShadow(og, "       Kills: "+levelScene.getKilledCreaturesTotal(), start-6, actualRow, 6);
        LevelScene.drawStringDropShadow(og, "    Coins: "+levelScene.getMarioCoins(), start+19, actualRow++, 6);
        
        System.out.println(levelScene.getKilledCreaturesByStomp()+" ("+(df.format((double)levelScene.getKilledCreaturesByStomp()/(double)levelScene.getKilledCreaturesTotal()*100))+"%)");
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
        LevelScene.drawStringDropShadow(og, "Score: "+levelScene.getScore(), start-2, actualRow++, 1);
        g.drawImage(image, 0, 0, width, height, null);
    }

    @Override
	public String toString() {
		return "MarioComponent [running=" + running + ", width=" + width + ", height=" + height
				+  ", frame=" + frame
				+ ", delay=" + delay + ", ZLevelEnemies=" + ZLevelEnemies + ", ZLevelScene=" + ZLevelScene
				+ ", gameViewer=" + gameViewer + ", cheatAgent=" + cheatAgent + ", prevHumanKeyBoardAgent="
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
        levelScene = ((LevelScene) levelScene);
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
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).LevelSceneAroundMarioASCII(Enemies, LevelMap, Complete, ZLevelMap, ZLevelEnemies);
        else {
            return new ArrayList<String>();
        }
    }

    // Chaning ZLevel during the game on-the-fly;
    public byte[][] getMergedObservationZ(int zLevelScene, int zLevelEnemies) {
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).mergedObservation(zLevelScene, zLevelEnemies);
        return null;
    }

    public byte[][] getLevelSceneObservationZ(int zLevelScene) {
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).levelSceneObservation(zLevelScene);
        return null;
    }

    public byte[][] getEnemiesObservationZ(int zLevelEnemies) {
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).enemiesObservation(zLevelEnemies);
        return null;
    }

    public int getKillsTotal() {
    	 if (levelScene instanceof LevelScene)
    		 return ((LevelScene) levelScene).getKilledCreaturesTotal();
         return -1; //Error
    }

    public int getKillsByFire() {
    	 if (levelScene instanceof LevelScene)
    		 return ((LevelScene) levelScene).getKilledCreaturesByFireBall();
         return -1; //Error
    }

    public int getKillsByStomp() {
    	 if (levelScene instanceof LevelScene)
    		 return ((LevelScene) levelScene).getKilledCreaturesByStomp();
         return -1; //Error
       
    }

    public int getKillsByShell() {
    	 if (levelScene instanceof LevelScene)
    		 return ((LevelScene) levelScene).getKilledCreaturesByShell();
         return -1; //Error
    }

    public byte[][] getCompleteObservation() {
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).mergedObservation(this.ZLevelScene, this.ZLevelEnemies);
        return null;
    }

    public byte[][] getEnemiesObservation() {
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).enemiesObservation(this.ZLevelEnemies);
        return null;
    }

    public byte[][] getLevelSceneObservation() {
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).levelSceneObservation(this.ZLevelScene);
        return null;
    }

    public boolean isMarioOnGround() {
        return ((LevelScene) levelScene).isMarioOnGround();
    }

    public boolean mayMarioJump() {
        return ((LevelScene) levelScene).mayMarioJump();
    }
    
    public boolean isFalling() {
    	return ((LevelScene) levelScene).getMarioYA()<0;
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

    public float[] getMarioFloatPos()
    {
        return new float[]{((LevelScene) levelScene).getMarioX(), ((LevelScene) levelScene).getMarioY()};
    }

    public float[] getEnemiesFloatPos()
    {
        if (levelScene instanceof LevelScene)
            return ((LevelScene) levelScene).enemiesFloatPos();
        return null;
    }

    public Mario.MODE getMarioMode()
    {
        return ((LevelScene) levelScene).getMarioMode();
    }

    public boolean isMarioCarrying()
    {
        return ((LevelScene) levelScene).getMarioCarried() != null;
    }

	@Override
	public Map<Coordinates, Tile> getTiles() {
		if (levelScene instanceof LevelScene) {
			return ((LevelScene)levelScene).getTiles();
		}
		 return null;
	}

	@Override
	public Map<Coordinates, List<Entity>> getEntities() {
		if (levelScene instanceof LevelScene) {
			return ((LevelScene)levelScene).getEntities();
		}
		return null;
	}
	

	public RunnerOptions getRunnerOptions() {
		return rOptions;
	}

	@Override
	public void setRunnerOptions(RunnerOptions rOptions) {
		this.rOptions=rOptions;
		rOptions.getAgent().reset();
		 adjustFPS();
		 
		 registerKeyListenerAgent(rOptions.getAgent());
		
		startLevel(rOptions.getLevelSeed(), rOptions.getDifficulty(), rOptions.getLevelType(), rOptions.getLevelLength(), rOptions.getTimeLimit());
		
	}
	
	public Agent getAgent() {
		return rOptions.getAgent();
	}
	
  	public void registerKeyListenerAgent(Agent agent) {
        if (agent instanceof KeyListener) {
            if (prevHumanKeyBoardAgent != null)
                this.removeKeyListener(prevHumanKeyBoardAgent);
            this.prevHumanKeyBoardAgent = (KeyListener) agent;
            this.addKeyListener(prevHumanKeyBoardAgent);
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
}