package ch.idsia.mario.environments;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Map;

import ch.idsia.mario.engine.LevelScene;
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
    public void storePaused();
    public void restorePaused();
    
    public void performTick();
    
    public boolean isDebugView();
    public void setDebugView(boolean debugView);
    public void toggleDebugView();
    
    public void swapAgent();
    public void setFPS(int fps);
    public int getFPS();
    
    public void registerKeyboardListener(KeyListener listener);
    public void removeLastKeyboardListener();
    public void addLastKeyboardListener();
    public void registerActualAgent();
    public void removeActualAgent();
    
    public void showMarioViewAsAscii();
    
    public void resizeView(Dimension d);
    public Dimension getInitialDimension();
    public Dimension getActualDimension();

    public LevelScene getLevelScene();
    
    public float[] getMarioFloatPosArray();
    public float[] getEnemiesFloatPosArray();
   
}
