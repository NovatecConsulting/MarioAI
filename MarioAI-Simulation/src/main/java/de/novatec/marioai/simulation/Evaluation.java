package de.novatec.marioai.simulation;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.amis.mario.tournament.EvaluateAgentConsole;
import cz.cuni.mff.amis.mario.tournament.run.MarioRunResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is designed to quickly evaluate quality of {@link MarioAiRunner} AI.
 * <p>
 * Check {@link #main(String[])} method where you can easily run evaluation for
 * certain levels all for all level configs.
 * <p>
 * Note that if you add custom level config into {@link LevelConfig}, {@link #evaluateAll(int)} will
 * automatically pick it up ;-)
 */
public class Evaluation {

    /**
     * Hoa many randomized level configs we should evaluate per level.
     */
    public static final int MAPS_COUNT = 200;

    /**
     * How many times we should eveluate the same level configuration.
     */
    public static final int MAP_REPETITIONS = 1;

    private static Logger log = LoggerFactory.getLogger(Evaluation.class);

    private static String[] getEvaluationOptions(int seed, String levelOptions) {
        return new String[]{
                "-s", String.valueOf(seed) // "seed"
                , "-o", levelOptions
                , "-c", String.valueOf(MAPS_COUNT)  // level-count
                , "-r", String.valueOf(MAP_REPETITIONS)  // one-run-repetitions
                , "-a", "Runner"
                , "-i", "MarioAI"   // agent-id
                , "-d", "./results" // result-dir"
        };
    }

    private static String[] getEvaluationOptions(int seed, LevelConfig level) {
        return getEvaluationOptions(seed, level.getOptionsVisualizationOff());
    }

    public static MarioRunResults evaluateLevel(int seed, LevelConfig level) {
        log.info("EVALUATING " + level.name());
        log.info("EVALUATING " + level.getOptions());

        MarioRunResults results = EvaluateAgentConsole.evaluate(getEvaluationOptions(seed, level));

        printResults(level, results);

        return results;
    }

    public static void printResults(LevelConfig level, MarioRunResults results) {
        log.info(level.name());
        log.info("  +-- VICTORIES:  " + results.totalVictories + " / " + results.getTotalRuns() + " (" + ((double) results.totalVictories / (double) results.getTotalRuns()) + "%)");
        log.info("  +-- AVG   TIME: " + ((double) results.totalTimeSpent / (double) results.getTotalRuns()) + "s");
        log.info("  +-- TOTAL TIME: " + results.totalTimeSpent + "s");
        log.info("-------------------");
    }

    public static void evaluateLevels(int seed, LevelConfig... configs) {
        Map<LevelConfig, MarioRunResults> results = new HashMap<LevelConfig, MarioRunResults>();

        for (LevelConfig level : configs) {
            MarioRunResults r = evaluateLevel(seed, level);
            results.put(level, r);
        }

        log.info("===================================");
        log.info("RESULTS (Maps per level: " + MAPS_COUNT + ", Map reptitions: " + MAP_REPETITIONS);
        log.info("===================================");

        for (LevelConfig level : configs) {
            printResults(level, results.get(level));
        }
    }

    public static void evaluateAll(int seed) {
        Map<LevelConfig, MarioRunResults> results = new HashMap<LevelConfig, MarioRunResults>();

        for (LevelConfig level : LevelConfig.values()) {
            MarioRunResults r = evaluateLevel(seed, level);
            results.put(level, r);
        }

        log.info("=======");
        log.info("RESULTS");
        log.info("=======");

        for (LevelConfig level : LevelConfig.values()) {
            printResults(level, results.get(level));
        }

    }

    /**
     * Simple way how to evaluate your {@link MarioAiRunner}.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Change the seed to receive evaluation for different levels ~ it alters procedural generation of level maps.
        int masterSeed = 20;

        evaluateLevel(masterSeed, LevelConfig.LEVEL_1);
        //evaluateLevel(masterSeed, LevelConfig.LEVEL_1_JUMPING);
        //evaluateLevel(masterSeed, LevelConfig.LEVEL_2_GOOMBAS);
        //evaluateLevel(masterSeed, LevelConfig.LEVEL_3_TUBES);
        //evaluateLevel(masterSeed, LevelConfig.LEVEL_4_SPIKIES);

        //evaluateLevels(masterSeed, LevelConfig.LEVEL_0_FLAT, LevelConfig.LEVEL_1_JUMPING, LevelConfig.LEVEL_2_GOOMBAS);

        //evaluateAll(masterSeed);
    }

}
