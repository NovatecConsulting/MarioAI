package de.novatec.marioai.simulation;

import ch.idsia.benchmark.mario.engine.SimulatorOptions.ReceptiveFieldMode;
import ch.idsia.benchmark.mario.engine.generalization.Enemy;
import ch.idsia.benchmark.mario.options.FastOpts;

public enum LevelConfig {

	LEVEL_1(FastOpts.VIS_ON_2X + FastOpts.L_FLAT_OFF + FastOpts.L_BLOCKS_OFF + FastOpts.L_CANNONS_OFF + FastOpts.L_COINS_OFF + FastOpts.L_DEAD_ENDS_OFF + FastOpts.L_GAPS_OFF
			+ FastOpts.L_HIDDEN_BLOCKS_OFF + FastOpts.L_PLATFORMS_OFF + FastOpts.L_LADDERS_OFF + FastOpts.L_TUBES_OFF + FastOpts.L_DIFFICULTY(0)),

	LEVEL_2(FastOpts.VIS_ON_2X + FastOpts.L_FLAT_OFF + FastOpts.L_BLOCKS_OFF + FastOpts.L_CANNONS_OFF + FastOpts.L_COINS_OFF + FastOpts.L_DEAD_ENDS_OFF + FastOpts.L_GAPS_OFF
			+ FastOpts.L_HIDDEN_BLOCKS_OFF + FastOpts.L_PLATFORMS_OFF + FastOpts.L_LADDERS_OFF + FastOpts.L_TUBES_OFF + FastOpts.L_DIFFICULTY(0) + FastOpts.L_ENEMY(Enemy.GOOMBA) ),

	LEVEL_3(FastOpts.VIS_ON_2X + FastOpts.L_FLAT_OFF + FastOpts.L_BLOCKS_OFF + FastOpts.L_CANNONS_OFF + FastOpts.L_COINS_OFF + FastOpts.L_DEAD_ENDS_OFF + FastOpts.L_GAPS_OFF
			+ FastOpts.L_HIDDEN_BLOCKS_OFF + FastOpts.L_PLATFORMS_OFF + FastOpts.L_LADDERS_OFF + FastOpts.L_TUBES_OFF + FastOpts.L_DIFFICULTY(0) + FastOpts.L_ENEMY(Enemy.GREEN_KOOPA)),


	LEVEL_4(FastOpts.VIS_ON_2X + FastOpts.L_FLAT_OFF + FastOpts.L_BLOCKS_OFF + FastOpts.L_CANNONS_OFF + FastOpts.L_COINS_OFF + FastOpts.L_DEAD_ENDS_OFF + FastOpts.L_GAPS_OFF
			+ FastOpts.L_HIDDEN_BLOCKS_OFF + FastOpts.L_PLATFORMS_OFF + FastOpts.L_LADDERS_OFF + FastOpts.L_TUBES_OFF + FastOpts.L_DIFFICULTY(0) + FastOpts.L_ENEMY(Enemy.SPIKY)),


	LEVEL_5(FastOpts.VIS_ON_2X + FastOpts.L_FLAT_OFF + FastOpts.L_BLOCKS_OFF + FastOpts.L_CANNONS_OFF + FastOpts.L_COINS_OFF + FastOpts.L_DEAD_ENDS_OFF + FastOpts.L_GAPS_OFF
			+ FastOpts.L_HIDDEN_BLOCKS_OFF + FastOpts.L_PLATFORMS_OFF + FastOpts.L_LADDERS_OFF + FastOpts.L_TUBES_OFF + FastOpts.L_DIFFICULTY(0) + FastOpts.L_ENEMY(Enemy.SPIKY,Enemy.GOOMBA));

	private String options;

	private LevelConfig(String options) {
		this.options = options;
	}

	public String getOptions() {
		return options;
	}

	public String getOptionsRandomized() {
		return options + FastOpts.L_RANDOMIZE;
	}

	public String getOptionsVisualizationOff() {
		return options + FastOpts.VIS_OFF;
	}

	public String getOptionsRndVissOff() {
		return options + FastOpts.VIS_OFF + FastOpts.L_RANDOMIZE;
	}

}
