package de.novatec.marioai.simulation;

import ch.idsia.agents.IAgent;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.tools.EvaluationInfo;

import de.novatec.marioai.agents.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class which runs the MarioAI-Simulation
 * Configure your simulation by modify static settings below
 */
public class Runner {

    // CONFIGURE YOUR MARIO SIMULATION:
    //  choose a Level or set it to a random Level
    //  instantiate your yourAgent
    private static LevelConfig level = LevelConfig.LEVEL_2_GOOMBAS;
    private static boolean useRandomLevel = false;
    private static IAgent yourAgent = new Agent04_Shooter();

    private static Logger log = LoggerFactory.getLogger(Runner.class);

    /**
     * The simulation will be started by this main method.
     * @param args
     */
    public static void main(String[] args) {

        MarioSimulator simulator;

        if(useRandomLevel)
            simulator = new MarioSimulator(level.getOptionsRandomized());
        else
            simulator = new MarioSimulator(level.getOptions());

        EvaluationInfo info = simulator.run(yourAgent);

        switch (info.getResult()) {
            case LEVEL_TIMEDOUT:
                log.info("LEVEL TIMED OUT!");
                break;

            case MARIO_DIED:
                log.info("MARIO KILLED");
                break;

            case SIMULATION_RUNNING:
                log.info("SIMULATION STILL RUNNING?");
                throw new RuntimeException("Invalid evaluation info state, simulation should not be running.");

            case VICTORY:
                log.info("VICTORY!!!");
                break;
        }
    }
}