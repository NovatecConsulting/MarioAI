package de.novatec.marioai.agents;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import de.novatec.marioai.MarioAgenNtBase;


/**
 * An agent that sprints forward and jumps if it detects an obstacle ahead.
 *
 * @author Jakub 'Jimmy' Gemrot, gemrot@gamedev.cuni.cz
 */
public class Agent03_Forward extends MarioAgenNtBase {

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
        boolean brickAhed = getTiles().brick(1, 0)  ||
                            getTiles().brick(1, -1) ||
                            getTiles().brick(2, 0)  ||
                            getTiles().brick(2, -1) ||
                            getTiles().brick(3, 0)  ||
                            getTiles().brick(3, -1);

        return brickAhed;
    }


    public MarioInput doAiLogic() {
        // ALWAYS RUN RIGHT
        getMarioControl().runRight();

        // ALWAYS SPEED RUN
        getMarioControl().sprint();

        // IF (ENEMY || BRICK AHEAD) => JUMP
        if (enemyAhead() || brickAhead())
            getMarioControl().jump();

        // If (In the air) => keep JUMPing
        if (!getMarioEntity().onGround) {
            getMarioControl().jump();
        }

        return getMarioInput();
    }

}