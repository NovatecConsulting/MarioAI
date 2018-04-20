package ch.idsia.mario.engine;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.level.BgLevelGenerator;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Mario.STATUS;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MainFrame;
import ch.idsia.tools.RunnerOptions;
import de.novatec.mario.engine.generalization.Coordinates;
import de.novatec.mario.engine.generalization.Entity;
import de.novatec.mario.engine.generalization.Tile;
import de.novatec.marioai.agents.HumanKeyboardAgent;
import de.novatec.marioai.tools.MarioNtAgent;

public class MarioComponent extends JComponent implements Environment {
    private static final long serialVersionUID = 790878775993203817L;

    private boolean running = false;
    private GraphicsConfiguration graphicsConfiguration;
    private LevelRenderer layer;
    private BgRenderer[] bgLayer = new BgRenderer[2];
    private SpriteRenderer spriteRenderer;
    private boolean readyToExit=false,startReady=false;
    private float blackoutTimer;
    private boolean paused = false,setpaused=false, performTick=false,hijacked=false,sethijacked,wasHijacked=false,storedPause=false;
    
    private RunnerOptions rOptions;
    
    private boolean debugView;
    private boolean viewable;

    private int frame;
    private int delay;
    
    private static final int GENERALIZATION_ENEMIES = 1;
    private static final int GENERALIZATION_LEVELSCENE = 1;
    private static final int maxFPS=100;
    private static final DecimalFormat df = new DecimalFormat("0.0");
    private static final DecimalFormat df2 = new DecimalFormat("00");

    private Agent swapper=new HumanKeyboardAgent(), actual;
    private KeyListener prevHumanKeyBoardAgent;
    private LevelScene levelScene = null;
    
    VolatileImage lastImage;
 
    //--- Constructor
    public MarioComponent(int width, int height, RunnerOptions rOptions) {
       
    	this.rOptions=rOptions;
        this.setFocusable(true);
        this.setEnabled(true);

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
    }
    
    //--- Copy Constructor
    public MarioComponent(LevelScene alreadyCopied,MarioComponent toCopy) {

    	this.setFocusable(true);
        this.setEnabled(true);
       
        Dimension size = new Dimension((int)toCopy.getSize().getWidth(), (int)toCopy.getSize().getHeight());

        setPreferredSize(size);
        
        this.running = toCopy.running;
		this.graphicsConfiguration = toCopy.graphicsConfiguration; //ready only
		this.rOptions = new RunnerOptions(toCopy.rOptions);
		this.frame = toCopy.frame;
		this.blackoutTimer=toCopy.blackoutTimer;
		
		this.paused=toCopy.paused;
		this.setpaused=toCopy.setpaused;
		this.performTick=toCopy.performTick;
		this.wasHijacked=toCopy.wasHijacked;
		
		this.delay = toCopy.delay;
		
		this.levelScene = alreadyCopied;
		
		this.debugView=toCopy.debugView;
		this.viewable=toCopy.viewable;
		
		this.layer=new LevelRenderer(alreadyCopied.getLevel(), toCopy.layer);
		this.bgLayer=toCopy.bgLayer;
		this.spriteRenderer=new SpriteRenderer(alreadyCopied.getSprites());
		
		for(KeyListener listener:toCopy.getKeyListeners()) this.registerKeyboardListener(listener);
    }

    //--- FPS
	public void adjustFPS() { //MAKING MARIO FASTER! SMALLER DELAY=FASTER RUNNING
    	if(rOptions.isViewable()) delay = (rOptions.getFPS() > 0) ? (rOptions.getFPS() >= maxFPS) ? 0 : (1000/rOptions.getFPS()) : maxFPS;
    	else delay=0;
    }

    //--- JComponent
    public void paint(Graphics g) {
    }

    public void update(Graphics g) {
    }

    public void init() { //needs to stay, parent container must add the MarioComponent(this) before calling init()
        graphicsConfiguration = getGraphicsConfiguration();
        if (graphicsConfiguration != null) {
            Art.init(graphicsConfiguration);
        }
    }

    public void stop() {
        running = false;
    }
    
    private void checkPaused() {
    	if(levelScene.getMarioStatus()==STATUS.RUNNING)	this.paused=setpaused;
    	else this.paused=true;
    }

    public EvaluationInfo run() {
    	
        running = true;
        adjustFPS();
        EvaluationInfo evaluationInfo = new EvaluationInfo(rOptions.getTask());

        VolatileImage image = null;
        Graphics g = null;
        Graphics og = null;

        image = createVolatileImage(320, 240);

        long startTime = System.currentTimeMillis();  // Remember the starting time
        STATUS marioStatus = STATUS.RUNNING;

        int totalActionsPerfomed = 0;

        while (running||!readyToExit) {
        	boolean tmpPerformTick=performTick;
        	
        	lastImage=image;
        	
        	g=getGraphics();
        	og=image.getGraphics();
            if(!paused||tmpPerformTick)levelScene.tick();
          
            if (rOptions.isViewable()) {
                og.fillRect(0, 0, getSize().width, getSize().height);
                render(og);
                checkGameStatus(og);
            }
            checkPaused();
        	checkHijacked();
            boolean[] action = {false,false,false,false,false};

            	if(!paused||tmpPerformTick) action = getAgent().getAction(this);
            	
            	if(rOptions.isViewable()&&getAgent() instanceof MarioNtAgent&&levelScene.getMarioStatus()==STATUS.RUNNING)((MarioNtAgent)getAgent()).debugDraw(og,this,debugView,!paused||performTick);
           
            if (action != null)
            {
              if(!paused||tmpPerformTick) for (int i = 0; i < Environment.numberOfButtons; ++i)
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

            if(!paused||tmpPerformTick) levelScene.setMarioKeys(action);

            if (rOptions.isViewable()) {
                if(running&&!readyToExit) {
                	 drawProgress(og);
                	 drawInfos(og);
                	 String msg = "Agent: " + getAgent().getName();
                     drawStringDropShadow(og, msg, 0, 7, 5); // DEBUG MESSAGES

                     msg = "Selected Actions: ";
                     drawStringDropShadow(og, msg, 0, 8, 6);

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
                	 drawStringDropShadow(og, "FPS: ", 33, 2, 7);
                	 drawStringDropShadow(og, ((rOptions.getFPS() > 99) ? "\\infty" : ""+rOptions.getFPS()), 33, 3, 7);
                     drawStringDropShadow(og, "Score: "+(int)levelScene.getScore(), 1,27, 4);
                }

                g.drawImage(image, 0, 0, getSize().width, getSize().height, null); // set size to frame size
               
                } else {
                // Win or Die without renderer!! independently.
                marioStatus =  levelScene.getMarioStatus();
                if (marioStatus != STATUS.RUNNING) break;
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
            
            if(tmpPerformTick==true)performTick=false;
        } 
        
        //--- Show results on end screen
        if (rOptions.isViewable()) {
        	redrawEndScreen();
		
        }
        
        //ADD INFO TO EVALUATION INFO
        evaluationInfo.agentType = rOptions.getAgent().getClass().getSimpleName();
        evaluationInfo.agentName = rOptions.getAgent().getName();
        evaluationInfo.marioStatus = levelScene.getMarioStatus();
        evaluationInfo.lengthOfLevelPassedPhys = levelScene.getMarioX();
        evaluationInfo.lengthOfLevelPassedCells =  levelScene.getMarioMapX();
        evaluationInfo.levelXExit=levelScene.getLevelXExit();
        evaluationInfo.totalLengthOfLevelCells = levelScene.getLevelWidth();
        evaluationInfo.totalLengthOfLevelPhys = levelScene.getLevelWidthPhys();
        evaluationInfo.timeSpentOnLevel = levelScene.getStartTime();
        evaluationInfo.timeLeft = levelScene.getTimeLeft();
        evaluationInfo.totalTimeGiven = levelScene.getTotalTime();
        evaluationInfo.setExactTimeLeft(levelScene.getExactTimeLeft());
        evaluationInfo.numberOfGainedCoins = levelScene.getMarioCoins();
        evaluationInfo.totalNumberOfCoins   = levelScene.getTotalCoins() ; 
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
    
   //--- Rendering
    private void render(Graphics g) {
    	int xCam=(int)levelScene.getxCam();
    	int yCam=(int)levelScene.getyCam();
    	int tick=levelScene.getTick();
    	for (int i = 0; i < 2; i++) {
			bgLayer[i].setCam(xCam, yCam);
			bgLayer[i].render(g, tick);
		}
    	
    	spriteRenderer.render(g,xCam, yCam,0);
    	
    	g.translate((int)levelScene.getxCam(), (int)levelScene.getyCam());
    	layer.setCam(xCam,yCam);
		layer.render(g, tick);
		layer.renderExit0(g, tick, levelScene.getMarioStatus()!=STATUS.WIN);
		
		spriteRenderer.render(g,xCam, yCam,1);
		
		g.translate((int)levelScene.getxCam(), (int)levelScene.getyCam());
		g.setColor(Color.BLACK);
		layer.renderExit1(g, tick);
    }
    
    private void checkGameStatus(Graphics g) {
    	int xCam=(int)levelScene.getxCam();
    	int yCam=(int)levelScene.getyCam();
    	
    	if (!startReady&&levelScene.getStartTime() > 0) {
			renderBlackout(g, 160, 120, (int) (blackoutTimer));
			blackoutTimer+=10;
			if(blackoutTimer>=320) startReady=true;
		}

		if (levelScene.getMarioStatus()==STATUS.WIN) {
			setPaused(true);
			renderBlackout(g, (int)levelScene.getMarioX() - xCam, (int)levelScene.getMarioY() - yCam, (int) (blackoutTimer));
			levelWon();
			if ((int)blackoutTimer <= 0){
				readyToExit=true;
			}
			else blackoutTimer-=10;
		}
		
		if (levelScene.getTimeLeft()<=0||levelScene.getMarioStatus()==STATUS.LOSE) { 
			setPaused(true);
			renderBlackout(g, (int)levelScene.getMarioX() - xCam, (int)levelScene.getMarioY() - yCam, (int) (blackoutTimer));
			levelFailed();

			if ((int)blackoutTimer <= 0){
				readyToExit=true;
			}
			else blackoutTimer-=10;
		}
		
    }
    
    public static void drawStringDropShadow(Graphics g, String text, int x, int y, int c) {
		drawString(g, text, x * 8 + 5, y * 8 + 5, 0);
		drawString(g, text, x * 8 + 4, y * 8 + 4, c);
	}

	private static void drawString(Graphics g, String text, int x, int y, int c) { //c: 0=black,1=red,2=green,3=blue,4=yellow
		char[] ch = text.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
		}
	}
	
	private void renderBlackout(Graphics g, int x, int y, int radius) {
		if (radius > 320)
			return;

		int[] xp = new int[20];
		int[] yp = new int[20];
		for (int i = 0; i < 16; i++) {
			xp[i] = x + (int) (Math.cos(i * Math.PI / 15) * radius);
			yp[i] = y + (int) (Math.sin(i * Math.PI / 15) * radius);
		}
		xp[16] = 320;
		yp[16] = y;
		xp[17] = 320;
		yp[17] = 240;
		xp[18] = 0;
		yp[18] = 240;
		xp[19] = 0;
		yp[19] = y;
		g.fillPolygon(xp, yp, xp.length);

		for (int i = 0; i < 16; i++) {
			xp[i] = x - (int) (Math.cos(i * Math.PI / 15) * radius);
			yp[i] = y - (int) (Math.sin(i * Math.PI / 15) * radius);
		}
		xp[16] = 320;
		yp[16] = y;
		xp[17] = 320;
		yp[17] = 0;
		xp[18] = 0;
		yp[18] = 0;
		xp[19] = 0;
		yp[19] = y;

		g.fillPolygon(xp, yp, xp.length);
	}
	
	private void drawInfos(Graphics g) {
		
		MarioComponent.drawStringDropShadow(g, "DIFFICULTY: " + df2.format(levelScene.getLevelDifficulty()), 0, 0,levelScene.getLevelDifficulty() > 6 ? 1 : levelScene.getLevelDifficulty() > 2 ? 4 : 7);
		MarioComponent.drawStringDropShadow(g, "World " + (paused ? "paused" : "running"), 19, 0, 7);
		MarioComponent.drawStringDropShadow(g, "SEED:" + levelScene.getLevelSeed(), 0, 1, 7);
		MarioComponent.drawStringDropShadow(g, "TYPE:" + levelScene.getLevelType(), 0, 2, 7);
		MarioComponent.drawStringDropShadow(g, "ALL KILLS: " + getKillsTotal(), 19, 1, 1);
		MarioComponent.drawStringDropShadow(g, "LENGTH:" + levelScene.getMarioMapX() + " of " + levelScene.getLevelXExit(), 0, 3, 7);
		MarioComponent.drawStringDropShadow(g, "by Fire  : " + getKillsByFire(), 19, 2, 1);
		MarioComponent.drawStringDropShadow(g, "COINS    : " + df2.format(levelScene.getMarioCoins()), 0, 4, 4);
		MarioComponent.drawStringDropShadow(g, "by Shell : " + getKillsByShell(), 19, 3, 1);
		MarioComponent.drawStringDropShadow(g, "MUSHROOMS: " + df2.format(levelScene.getMarioGainedMushrooms()), 0, 5, 4);
		MarioComponent.drawStringDropShadow(g, "by Stomp : " + getKillsByStomp(), 19, 4, 1);
		MarioComponent.drawStringDropShadow(g, "FLOWERS  : " + df2.format(levelScene.getMarioGainedFowers()), 0, 6, 4);
		if(wasHijacked)MarioComponent.drawStringDropShadow(g, "HIJACKED!" , 19, 6, 1);

		MarioComponent.drawStringDropShadow(g, "TIME", 33, 0, 7);
		int time = (levelScene.getTimeLeft());
		if (time < 0)
			time = 0;
		MarioComponent.drawStringDropShadow(g, " " + df2.format(time), 33, 1, 7);
	}
	
	private void drawProgress(Graphics g) {
		String entirePathStr = "......................................|";
		double physLength = (levelScene.getLevelWidth() - 53) * 16;
		int progressInChars = (int) (levelScene.getMarioX() * (entirePathStr.length() / physLength));
		String progress_str = "";
		for (int i = 0; i < progressInChars - 1; ++i)
			progress_str += ".";
		progress_str += "M";
		try {
			drawStringDropShadow(g, entirePathStr.substring(progress_str.length()), progress_str.length(), 28, 0);
		} catch (StringIndexOutOfBoundsException e) {
			//System.err.println("warning: progress line inaccuracy");
		}
		drawStringDropShadow(g, progress_str, 0, 28, 2);
	}
    
	public void redrawEndScreen() {
		if(levelScene.getMarioStatus()==STATUS.WIN||levelScene.getMarioStatus()==STATUS.LOSE) {
			getParent().setBackground(Color.BLACK);
        	SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					drawEndScreen(getGraphics(), lastImage.getGraphics(), lastImage);
				}
			});
		}
	}
    private void drawEndScreen(Graphics g, Graphics og, VolatileImage image) {
        final int start=4;
        int actualRow=3;
        drawStringDropShadow(og, "Results: ", 1, actualRow++, 1);
        drawStringDropShadow(og, "       Agent: "+rOptions.getAgent().getName(), start, actualRow++, 2);
        if(rOptions.getAgent().getClass().getSimpleName().length()<18) {
        	drawStringDropShadow(og, "     of Type: "+rOptions.getAgent().getClass().getSimpleName()+".class", start, actualRow++, 2);
        	actualRow++;
        }
        else { 
        	drawStringDropShadow(og, "     of Type: ", start, actualRow++, 2);
        	drawStringDropShadow(og,"   "+getAgent().getClass().getSimpleName()+".class", start, actualRow++, 2);
        }
        actualRow++;
        drawStringDropShadow(og, "Mario Status: "+levelScene.getMarioStatus(), start, actualRow, 1);
        if(wasHijacked) {
        	drawStringDropShadow(og,  "(HIJACKED!)", start+19, actualRow++, 1);
        }
        else actualRow++;
        actualRow++;
        drawStringDropShadow(og, "       Level: "+levelScene.getLevelSeed(), start, actualRow++, 4);
        drawStringDropShadow(og, " Distance to", start,actualRow++,4);
        drawStringDropShadow(og, "      target: "+levelScene.getLevelXExit(), start, actualRow++, 4);
        drawStringDropShadow(og, "     -passed: "+(int)levelScene.getMarioX()/16+" ("+(df.format((double)levelScene.getMarioX()/16/(double)levelScene.getLevelXExit()*100))+"%)", start, actualRow++, 4);
        actualRow++;
        drawStringDropShadow(og, "  Total Time: "+levelScene.getTotalTime(), start, actualRow++, 4);
        drawStringDropShadow(og, "     -passed: "+levelScene.getStartTime(), start, actualRow++, 4);
        drawStringDropShadow(og, "     -  left: "+levelScene.getTimeLeft(), start, actualRow++, 4);
        actualRow++;
        actualRow++;
        
        drawStringDropShadow(og, "       Kills: "+levelScene.getKilledCreaturesTotal(), start-6, actualRow, 6);
        drawStringDropShadow(og, "    Coins: "+levelScene.getMarioCoins()+"/"+levelScene.getTotalCoins(), start+19, actualRow++, 6);
        
        if(levelScene.getKilledCreaturesByStomp()>0) drawStringDropShadow(og, "    by stomp: "+levelScene.getKilledCreaturesByStomp()+" ("+(df.format((double)levelScene.getKilledCreaturesByStomp()/(double)levelScene.getKilledCreaturesTotal()*100))+"%)", start-6, actualRow, 6);
        else drawStringDropShadow(og, "    by stomp: 0",start-6,actualRow,6);
        	
        drawStringDropShadow(og, "Mushrooms: "+levelScene.getMarioGainedMushrooms(), start+19, actualRow++, 6);
        
        if(levelScene.getKilledCreaturesByShell()>0) drawStringDropShadow(og, "    by shell: "+levelScene.getKilledCreaturesByShell()+" ("+(df.format((double)levelScene.getKilledCreaturesByShell()/(double)levelScene.getKilledCreaturesTotal()*100))+"%)", start-6, actualRow, 6);
        else drawStringDropShadow(og, "    by shell: 0",start-6,actualRow,6);
        drawStringDropShadow(og, "  Flowers: "+levelScene.getMarioGainedFowers(), start+19, actualRow++, 6);
        
        if(levelScene.getKilledCreaturesByFireBall()>0) drawStringDropShadow(og, "    by  fire: "+levelScene.getKilledCreaturesByFireBall()+" ("+(df.format((double)levelScene.getKilledCreaturesByFireBall()/(double)levelScene.getKilledCreaturesTotal()*100))+"%)", start-6, actualRow++, 6);
        else drawStringDropShadow(og, "    by  fire: 0",start-6,actualRow++,6);
        actualRow++;       
        
        drawStringDropShadow(og, "----------------------------------", start-2, actualRow++, 1);
        drawStringDropShadow(og, "Score: "+(int)(levelScene.getScore())+" ("+rOptions.getTask().getName()+")", start-2, actualRow++, 1);
        g.drawImage(image, 0, 0, getSize().width, getSize().height, null);
    }

    @Override
	public String toString() {
		return "MarioComponent [running=" + running + ", width=" + getSize().width + ", height=" + getSize().height
				+  ", frame=" + frame
				+ ", delay=" + delay
				+ "]";
	}

	//--- Control
    public void startLevel(long seed, int difficulty, Level.LEVEL_TYPES type, int levelLength, int timeLimit) {
        levelScene = new LevelScene(this, seed, difficulty, type, levelLength, timeLimit);
        levelScene.init();
        layer = new LevelRenderer(levelScene.getLevel(), graphicsConfiguration, 320, 240);
        
        for (int i = 0; i < 2; i++) {
			int scrollSpeed = 4 >> i;
			int w = ((levelScene.getLevelWidth() * 16) - 320) / scrollSpeed + 320;
			int h = ((levelScene.getLevelHight() * 16) - 240) / scrollSpeed + 240;
			Level bgLevel = BgLevelGenerator.createLevel(w / 32 + 1, h / 32 + 1, i == 0, levelScene.getLevelType());
			bgLayer[i] = new BgRenderer(bgLevel, graphicsConfiguration, 320, 240, scrollSpeed);
		}
        spriteRenderer=new SpriteRenderer(levelScene.getSprites());
    }

    public void levelFailed() {
        stop();
    }
    
    public void levelWon() {
        stop();
    }
    
    public void swapAgent() {
    	if(rOptions.getAgent().getClass().equals(HumanKeyboardAgent.class)) return;
    	this.setPaused(true);
    	this.wasHijacked=true;
    	this.sethijacked=!hijacked;
    }
    
    public void checkHijacked() {
    	if(rOptions.getAgent().getClass().equals(HumanKeyboardAgent.class)) return;
    	if(levelScene.getMarioStatus()!=STATUS.RUNNING) return;
    	Agent tmp;
    	if(sethijacked!=hijacked) {
    		tmp=swapper;
    		swapper=actual;
    		actual=tmp;	
    		registerKeyListenerAgent(actual);
    		if(swapper instanceof KeyListener) this.removeKeyListener((KeyListener)swapper);
    			
    		
    	}
    	sethijacked=hijacked;
    }

    //---FocusListener
    public void focusGained(FocusEvent arg0) {
    }

    public void focusLost(FocusEvent arg0) {
    }

    public List<String> getTextObservation(boolean Enemies, boolean LevelMap, boolean Complete, int ZLevelMap, int ZLevelEnemies) {
            return levelScene.LevelSceneAroundMarioASCII(Enemies, LevelMap, Complete, ZLevelMap, ZLevelEnemies);
    }
    
    //--- Statistics
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

    // --- Observation
    public byte[][] getMergedObservationZ(int zLevelScene, int zLevelEnemies) {
            return levelScene.mergedObservation(zLevelScene, zLevelEnemies);
    }

    public byte[][] getLevelSceneObservationZ(int zLevelScene) {
            return levelScene.levelSceneObservation(zLevelScene);
    }

    public byte[][] getEnemiesObservationZ(int zLevelEnemies) {
            return levelScene.enemiesObservation(zLevelEnemies);
    }

    public byte[][] getCompleteObservation() {
            return levelScene.mergedObservation(MarioComponent.GENERALIZATION_LEVELSCENE, MarioComponent.GENERALIZATION_ENEMIES);
    }

    public byte[][] getEnemiesObservation() {
            return levelScene.enemiesObservation(MarioComponent.GENERALIZATION_ENEMIES);
    }

    public byte[][] getLevelSceneObservation() {
            return levelScene.levelSceneObservation(MarioComponent.GENERALIZATION_LEVELSCENE);
    }

    public void setPaused(boolean paused) {
       setpaused=paused;
    } 

	@Override
	public Map<Coordinates, Tile> getTiles() {
			return levelScene.getTiles();
	}

	@Override
	public Map<Coordinates, List<Entity>> getEntities() {
			return levelScene.getEntities();
	}
	
	
	//--- RunnerOptions
	public RunnerOptions getRunnerOptions() {
		return rOptions;
	}

	@Override
	public void setRunnerOptions() {
		this.viewable=rOptions.isViewable();
		this.debugView=rOptions.isDebugView();
		rOptions.getAgent().reset();
		adjustFPS();
		 
		actual=rOptions.getAgent();
		
		startLevel(rOptions.getLevelSeed(), rOptions.getDifficulty(), rOptions.getLevelType(), rOptions.getLevelLength(), rOptions.getTimeLimit());
	}
	
	public Agent getAgent() {
		return actual;
	}
	
	Task getTask(){
		if(rOptions!=null) return rOptions.getTask();
		return null;
	}
	
	@Override
	public void registerKeyboardListener(KeyListener listener) {
		this.addKeyListener(listener);
	}
	
	@Override
	public void removeLastKeyboardListener() {
		 if (prevHumanKeyBoardAgent != null) { 
			 getTopLevelAncestor().removeKeyListener(prevHumanKeyBoardAgent);
		 }
	}
	
	@Override
	public void addLastKeyboardListener() {
		if (prevHumanKeyBoardAgent != null) {
			getTopLevelAncestor().addKeyListener(prevHumanKeyBoardAgent);
		 }
	}
	
  	private void registerKeyListenerAgent(Agent agent) {
        if (agent instanceof KeyListener) {
        	
            if (prevHumanKeyBoardAgent != null) getTopLevelAncestor().removeKeyListener(prevHumanKeyBoardAgent);
            
            this.prevHumanKeyBoardAgent = (KeyListener) agent;
            getTopLevelAncestor().addKeyListener(this.prevHumanKeyBoardAgent);

        }
    }

	@Override
	public LevelScene getLevelScene() {
		return levelScene;
	}

	@Override
	public void togglePaused() {
		this.setpaused=!setpaused;
	}
	
	@Override
	public boolean isPaused() {
		return paused;
	}
	
	@Override
	public void performTick() {
		if(levelScene.getMarioStatus()==STATUS.RUNNING) {
			setPaused(true);
			this.performTick=true;
		}
	}

	//--- Mario 
	
	@Override
	public int getMarioMapX() {
		return levelScene.getMarioMapX();
	}

	@Override
	public int getMarioMapY() {
		return levelScene.getMarioMapY();
	}
	
    public Coordinates getMarioPos() {
        return new Coordinates(getMarioMapX(), getMarioMapY());
    }
    
    public Coordinates getMarioFloatPos() {
        return new Coordinates(levelScene.getMarioX(), levelScene.getMarioY());
    }

    public boolean mayMarioShoot() {
    	return levelScene.mayMarioShoot();
    }
    
    public Mario.MODE getMarioMode(){
        return levelScene.getMarioMode();
    }

    public boolean isMarioCarrying() {
        return levelScene.getMarioCarried() != null;
    }
    
    public boolean isMarioOnGround() {
        return levelScene.isMarioOnGround();
    }

    public boolean mayMarioJump() {
        return levelScene.mayMarioJump();
    }
    
    public boolean isMarioFalling() {
    	return this.levelScene.isMarioFalling();
    }
    
	//--- Debug View
	
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
		setPaused(true);
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
	
	//--- Screen Size
	
	@Override
	public Dimension getActualDimension() {
		return getSize();
	}

	@Override
	public Dimension getInitialDimension() {
		return new Dimension(rOptions.getWindowWidth(),rOptions.getWindowHeigth());
	}

	@Override
	public void resizeView(Dimension d) {
		setPreferredSize(d);
		revalidate();
		redrawEndScreen();
		repaint();
	}

	@Override
	public void registerActualAgent() {
		if(rOptions.getAgent()!=null&&rOptions.getAgent() instanceof KeyListener) {
			getTopLevelAncestor().addKeyListener((KeyListener)rOptions.getAgent());
		}
		
	}

	@Override
	public void removeActualAgent() {
		if(rOptions.getAgent()!=null&&rOptions.getAgent() instanceof KeyListener) {
			getTopLevelAncestor().removeKeyListener((KeyListener)rOptions.getAgent());
		}
		
	}

	@Override
	public void storePaused() {
		this.storedPause=this.paused;
		
	}

	@Override
	public void restorePaused() {
		this.setPaused(storedPause);
	}
}

	