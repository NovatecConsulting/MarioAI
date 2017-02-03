package de.novatec.marioai.agents;

import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.IAgent;
import ch.idsia.agents.controllers.MarioHijackAIBase;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.VisualizationComponent;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Agent that sprints forward, jumps and shoots.
 * <p>
 * This agent has successful rate ~ 50%.
 *
 * @author Jakub 'Jimmy' Gemrot, gemrot@gamedev.cuni.cz
 */
public class Agent04_Shooter extends MarioHijackAIBase implements IAgent {

    private static Logger log = LoggerFactory.getLogger(Agent04_Shooter.class);

    @Override
    public void reset(AgentOptions options) {
        super.reset(options);
    }

    private boolean enemyAhead() {
        return
                e.danger(1, 0) || e.danger(1, -1)
                        || e.danger(2, 0) || e.danger(2, -1)
                        || e.danger(3, 0) || e.danger(2, -1);
    }

    private boolean brickAhead() {
        return
                t.brick(1, 0) || t.brick(1, -1)
                        || t.brick(2, 0) || t.brick(2, -1)
                        || t.brick(3, 0) || t.brick(3, -1);
    }

    public MarioInput actionSelectionAI() {
        // ALWAYS RUN RIGHT
        control.runRight();

        // ALWAYS SPRINT
        control.sprint();

        // ALWAYS SHOOT (if able ... max 2 fireballs at once!)
        control.shoot();

        // ENEMY || BRICK AHEAD => JUMP
        // WARNING: do not press JUMP if UNABLE TO JUMP!
        if (enemyAhead() || brickAhead())
            control.jump();

        // If in the air => keep JUMPing
        if (!mario.onGround)
            control.jump();

        return action;
    }

    @Override
    public void debugDraw(VisualizationComponent vis, LevelScene level, IEnvironment env, Graphics g) {
        super.debugDraw(vis, level, env, g);
        if (mario == null)
            return;

        if (enemyAhead())
            log.debug("|ENEMY AHEAD|");

        if (brickAhead())
            log.debug("|BRICK AHEAD|");
    }
}