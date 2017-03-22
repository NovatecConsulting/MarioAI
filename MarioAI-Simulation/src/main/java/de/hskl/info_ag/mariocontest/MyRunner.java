package de.hskl.info_ag.mariocontest;

import ch.idsia.agents.IAgent;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.tools.EvaluationInfo;
import de.hskl.info_ag.mariocontest.MyAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class which runs the MarioAI-Simulation
 * Configure your simulation by modify static settings below
 */
public class MyRunner 
{
    private static LevelConfig level = LevelConfig.HSKL_LEVEL_3_DEBUG;
    private static IAgent yourAgent = new MyAgent();

    private static Logger log = LoggerFactory.getLogger(MyRunner.class);

    /**
     * The simulation will be started by this main method.
     * @param args
     */
    public static void main(String[] args) {
        MarioSimulator simulator = new MarioSimulator(level.getOptions());

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