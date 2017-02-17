package de.novatec.marioai;

import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.IAgent;
import ch.idsia.agents.controllers.modules.Entities;
import ch.idsia.agents.controllers.modules.Tiles;
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity;
import ch.idsia.benchmark.mario.engine.input.MarioControl;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;

/**
 * Base class for MarioAgents.
 * It wraps methods and instances which will be needed to implement an MarioAgent.
 * A Agent has to extend this class and also to implement the method {@link #doAiLogic()}.
 *
 * @author tj NovaTec GmbH
 */
public abstract class MarioAgenNtBase implements IAgent {

    /**
     * The execution of {@link #doAiLogic()} has to be delegated to the anonymous instance of {@link #baseApi}.
     * This instance also overwrites {@link MarioAgentNtWrapper#actionSelectionAI()}
     */
    private MarioAgentNtWrapper baseApi = new MarioAgentNtWrapper(){
        public MarioInput actionSelectionAI() {
            return doAiLogic();
        }
    };

    /**
     * The logic (i.e. the Agent's algorithm) has to be implemented in this method.
     * @return the {@link MarioInput} instance which will be delegated through the simulator execution.
     */
    public abstract MarioInput doAiLogic();

    /**
     * Send control commands to Mario (like {@link MarioControl#runRight()})
     * @return the {@link MarioControl}
     */
    public MarioControl getMarioControl() {
        return baseApi.getMarioControl();
    }

    /**
     * Get some information about the Mario it self like {@link MarioEntity#onGround}
     * @return a {@link MarioEntity} instance
     */
    public MarioEntity getMarioEntity() {
        return baseApi.getMarioEntity();
    }

    /**
     * Send direct inputs to Mario is possible through a {@link MarioInput} instance (like {@link MarioInput#press})
     * @return a {@link MarioInput} instance
     */
    public MarioInput getMarioInput() {
        return baseApi.getMarioInput();
    }

    /**
     * Looking for {@link Entities} in Mario's near is possible by the {@link Entities} object.
     * @return a {@link Entities} instance
     */
    public Entities getEntities() {
        return baseApi.getEntities();
    }

    /**
     * Looking for {@link Tiles} in Mario's near is possible by the {@link Tiles} object.
     * @return a {@link Tiles} instance
     */
    public Tiles getTiles() {
        return baseApi.getTiles();
    }

    /**
     * default final implementation which is not allowed to override
     */
    @Override
    public String getName() {
        return "NT Agent";
    }

    /**
     * default final implementation which is not allowed to override
     */
    @Override
    public final void reset(AgentOptions options) {
        baseApi.reset(options);
    }

    /**
     * default final implementation which is not allowed to override
     */
    @Override
    public final void observe(IEnvironment environment) {
        baseApi.observe(environment);
    }

    /**
     * default final implementation which is not allowed to override
     */
    @Override
    public final MarioInput actionSelection() {
        return baseApi.actionSelectionAI();
    }

    /**
     * default final implementation which is not allowed to override
     */
    @Override
    public final void receiveReward(float intermediateReward) {
        baseApi.receiveReward(intermediateReward);
    }
}
