package ch.idsia.mario.environments;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Map;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Mario;
import de.novatec.mario.engine.generalization.Coordinates;
import de.novatec.mario.engine.generalization.Entity;
import de.novatec.mario.engine.generalization.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 8:51:57 PM
 * Package: .Environments
 */

public interface Environment
{
    public static final int numberOfButtons = 5;
    public static final int numberOfObservationElements = 486 + 1;
    public static final int HalfObsWidth = 11;
    public static final int HalfObsHeight = 11;

    // always the same dimensionality: 22x22
    // always centered on the agent

    public void setRunnerOptions();
    
    public void setPaused(boolean paused);
    public void togglePaused();
    public boolean isPaused();
    
    public void performTick();
    
    public boolean isDebugView();
    public void setDebugView(boolean debugView);
    public void toggleDebugView();
    
    public void swapAgent();
    
    public void registerKeyboardListener(KeyListener listener);
    public void removeLastKeyboardListener();
    public void addLastKeyboardListener();
    public void registerActualAgent();
    public void removeActualAgent();

    public byte[][] getCompleteObservation();   // default: ZLevelScene = 1, ZLevelEnemies = 1

    public byte[][] getEnemiesObservation();    // default: ZLevelEnemies = 1

    public byte[][] getLevelSceneObservation(); // default: ZLevelScene = 1
    
    public void showMarioViewAsAscii();
    
    public Map<Coordinates,Tile> getTiles();
    public Map<Coordinates,List<Entity>> getEntities();
    
    public void resizeView(int width, int height);
    public Dimension getInitialDimension();
    public Dimension getActualDimension();

    public LevelScene getLevelScene();

    //--- Mario
    public boolean isMarioOnGround();
    public boolean mayMarioJump();
    public boolean isMarioCarrying();
    public boolean mayMarioShoot();
    public boolean isMarioFalling();
    
    public Coordinates getMarioPos();
    public Coordinates getMarioFloatPos();
    public int getMarioMapX();
    public int getMarioMapY();

    public Mario.MODE getMarioMode();
    
    //--- Obserservations
    public byte[][] getMergedObservationZ(int ZLevelScene, int ZLevelEnemies);
    public byte[][] getLevelSceneObservationZ(int ZLevelScene);
    public byte[][] getEnemiesObservationZ(int ZLevelEnemies);

    //--- Kills
    public int getKillsTotal();
    public int getKillsByFire();
    public int getKillsByStomp();
    public int getKillsByShell();
   
}
