package de.novatec.marioai.agents;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import de.novatec.marioai.MarioAgenNtBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Agent that sprints forward, jumps and shoots.
 * <p>
 * This agent has successful rate ~ 50%.
 *
 * @author Jakub 'Jimmy' Gemrot, gemrot@gamedev.cuni.cz
 */
public class Agent04_Shooter extends MarioAgenNtBase {

    private static Logger log = LoggerFactory.getLogger(Agent04_Shooter.class);

    private boolean enemyAhead() {
        return
                getEntities().danger(1, 0)  ||
                getEntities().danger(1, -1) ||
                getEntities().danger(2, 0)  ||
                getEntities().danger(2, -1) ||
                getEntities().danger(3, 0)  ||
                getEntities().danger(2, -1);
    }

    private boolean brickAhead() {
        return
                getTiles().brick(1, 0)  ||
                getTiles().brick(1, -1) ||
                getTiles().brick(2, 0)  ||
                getTiles().brick(2, -1) ||
                getTiles().brick(3, 0)  ||
                getTiles().brick(3, -1);
    }

    public MarioInput doAiLogic() {
        // ALWAYS RUN RIGHT
        getMarioControl().runRight();

        // ALWAYS SPRINT
        getMarioControl().sprint();

        // ALWAYS SHOOT (if able ... max 2 fireballs at once!)
        getMarioControl().shoot();

        // ENEMY || BRICK AHEAD => JUMP
        // WARNING: do not press JUMP if UNABLE TO JUMP!
        if (enemyAhead() || brickAhead())
            getMarioControl().jump();

        // If in the air => keep JUMPing
        if (!getMarioEntity().onGround)
            getMarioControl().jump();

        return getMarioInput();
    }


}