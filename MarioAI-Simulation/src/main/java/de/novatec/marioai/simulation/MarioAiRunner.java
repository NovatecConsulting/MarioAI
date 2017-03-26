package de.novatec.marioai.simulation;

import ch.idsia.agents.IAgent;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.benchmark.mario.engine.SimulatorOptions.ReceptiveFieldMode;
import ch.idsia.benchmark.mario.options.FastOpts;
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
     * @param IAgent agent 
     * 		to run
     * @param LevelConfig level
     * 		to load
     * @param boolean debug
     * 		draw debug view
     */
    public static void run(IAgent agent, LevelConfig level, boolean debug) {

        MarioSimulator simulator;

        String options = level.getOptions();
        
        if (debug) {
        	options += FastOpts.VIS_FIELD(ReceptiveFieldMode.GRID);
        }
        
        simulator = new MarioSimulator(options);

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