package de.novatec.marioai.simulation;

import ch.idsia.agents.IAgent;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.tools.EvaluationInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class which runs the MarioAI-Simulation
 * Configure your simulation by modify static settings below
 */
public class MarioAiRunner {

    private static Logger log = LoggerFactory.getLogger(MarioAiRunner.class);

    /**
     * The simulation will be started by this main method.
     * @param args
     */
    public static void run(IAgent agent, LevelConfig level) {

        MarioSimulator simulator;

        simulator = new MarioSimulator(level.getOptions());

        EvaluationInfo info = simulator.run(agent);

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