package de.novatec.marioai.agents;

import ch.idsia.benchmark.mario.engine.input.MarioInput;
import de.novatec.marioai.Environment;
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
public class AGENT_TEST extends MarioAgenNtBase {

    private static Logger log = LoggerFactory.getLogger(AGENT_TEST.class);

    private boolean enemyAhead() {
    	
    	Environment environment = getEnvironment();
    	return environment.getTileRelativeToMario(1, 0).isEnemy() ||
    			environment.getTileRelativeToMario(1,  -1).isEnemy() || 
    			environment.getTileRelativeToMario(2, 0).isEnemy() ||
    			environment.getTileRelativeToMario(2, -1).isEnemy() ||
    			environment.getTileRelativeToMario(3, 0).isEnemy() ||
    			environment.getTileRelativeToMario(2, -1).isEnemy()
    			;
    }

    private boolean brickAhead() {
    	Environment environment = getEnvironment();
    	return environment.getTileRelativeToMario(1, 0).isBrick() ||
    			environment.getTileRelativeToMario(1,  -1).isBrick() || 
    			environment.getTileRelativeToMario(2, 0).isBrick() ||
    			environment.getTileRelativeToMario(2, -1).isBrick() ||
    			environment.getTileRelativeToMario(3, 0).isBrick()||
    			environment.getTileRelativeToMario(2, -1).isBrick()
    			;
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