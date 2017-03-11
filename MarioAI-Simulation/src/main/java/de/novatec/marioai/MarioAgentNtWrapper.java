package de.novatec.marioai;

import java.awt.Graphics;

import ch.idsia.agents.controllers.MarioHijackAIBase;
import ch.idsia.agents.controllers.modules.Entities;
import ch.idsia.agents.controllers.modules.Tiles;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.VisualizationComponent;
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity;
import ch.idsia.benchmark.mario.engine.input.MarioControl;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;

/**
 * This class is wrapping the given {@link MarioHijackAIBase} API in order to hide irrelevant members.
 * It will be used by {@link MarioAgenNtBase}
 *
 * @author tj NovaTec GmbH
 */
public abstract class MarioAgentNtWrapper extends MarioHijackAIBase {

    /**
     * @see {@link MarioAgenNtBase#getMarioControl}
     */
    public MarioControl getMarioControl() {
        return control;
    }

    /**
     * @see {@link MarioAgenNtBase#getMarioEntity}
     */
    public MarioEntity getMarioEntity() {
        return mario;
    }

    /**
     * @see {@link MarioAgenNtBase#getMarioInput}
     */
    public MarioInput getMarioInput() {
        return action;
    }

    /**
     * @see {@link MarioAgenNtBase#getEntities}
     */
    public Entities getEntities() {
        return e;
    }

    /**
     * @see {@link MarioAgenNtBase#getTiles}
     */
    public Tiles getTiles() {
        return  t;
    }
    
    public LevelScene getLevelScene() {
    	return levelScene;
    }
    
}
