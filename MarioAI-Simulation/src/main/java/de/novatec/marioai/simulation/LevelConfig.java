package de.novatec.marioai.simulation;

import ch.idsia.benchmark.mario.engine.generalization.Enemy;
import ch.idsia.benchmark.mario.options.FastOpts;

public enum LevelConfig {

	LEVEL_1(FastOpts.VIS_ON_2X + FastOpts.LEVEL_01_FLAT),

	LEVEL_2(FastOpts.VIS_ON_2X + FastOpts.LEVEL_01_FLAT + FastOpts.L_ENEMY(Enemy.GOOMBA, Enemy.GREEN_KOOPA)),

	LEVEL_3(FastOpts.VIS_ON_2X + FastOpts.L_RANDOM_SEED(41) + FastOpts.L_GAPS_ON + FastOpts.L_PLATFORMS_ON + FastOpts.L_COINS_OFF),

	LEVEL_4(FastOpts.VIS_ON_2X + FastOpts.L_GAPS_ON + FastOpts.L_PLATFORMS_ON + FastOpts.L_TUBES_ON + FastOpts.L_ENEMY(Enemy.GOOMBA, Enemy.GREEN_KOOPA, Enemy.SPIKY)),

	LEVEL_5(FastOpts.VIS_ON_2X
			+ FastOpts.L_RANDOM_SEED(2708)
			+ FastOpts.L_DEAD_ENDS_ON
			+ FastOpts.L_GAPS_ON
			+ FastOpts.L_PLATFORMS_ON
			+ FastOpts.L_TUBES_ON
			+ FastOpts.L_ENEMY(Enemy.GOOMBA, Enemy.GREEN_KOOPA, Enemy.SPIKY)
			+ FastOpts.L_COINS_ON);
	
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
