package de.novatec.marioai.tools;

import java.util.Random;

import ch.idsia.mario.engine.level.Level.LEVEL_TYPES;
import ch.idsia.mario.engine.level.LevelGenerator;

public class LevelConfig{
	public static final LevelConfig Level1=new LevelConfig(797938204,256,0,LEVEL_TYPES.OVERGROUND);
	public static final LevelConfig Level2=new LevelConfig(958938223,256,0,LEVEL_TYPES.CASTLE);
	public static final LevelConfig Level3=new LevelConfig(1193454339,256,2,LEVEL_TYPES.OVERGROUND);
	public static final LevelConfig ASTARKILLER=new LevelConfig(652649838,256,15,LEVEL_TYPES.OVERGROUND);
	public static final LevelConfig LevelFLAT1=new LevelConfig(1193454339,256,2,false,false,true);
	public static final LevelConfig LevelFLAT2=new LevelConfig(1193454339,256,0,false,true,false);
	public static final LevelConfig LevelCUSTOM1=new LevelConfig(12345678,256,10,LEVEL_TYPES.OVERGROUND,false,true,true,new int[]{10,0,0,0,0});
	public static final LevelConfig LevelCUSTOM2=new LevelConfig(12345678,256,4,LEVEL_TYPES.OVERGROUND,false,false,false,new int[]{1,0,0,10,0});
	public static final LevelConfig LevelCUSTOM3=new LevelConfig(12345678,256,10,LEVEL_TYPES.OVERGROUND,false,false,false,new int[]{1,0,0,10,0});
	
	public static LevelConfig randomize(LevelConfig toRandomize) {
		if(toRandomize!=null) {
			int[] odds=toRandomize.getOdds();
			if(odds==null) {
				odds=new int[5];
				  	odds[LevelGenerator.ODDS_STRAIGHT] = 20;
			        odds[LevelGenerator.ODDS_HILL_STRAIGHT] = 10;
			        odds[LevelGenerator.ODDS_TUBES] = 2 + 1 * toRandomize.getPresetDifficulty();
			        odds[LevelGenerator.ODDS_JUMP] = 2 * toRandomize.getPresetDifficulty();
			        odds[LevelGenerator.ODDS_CANNONS] = -10 + 5 * toRandomize.getPresetDifficulty();
			}
			
			if(toRandomize.isFlat()) return new LevelConfig(new Random().nextInt(), toRandomize.length, toRandomize.presetDifficulty,toRandomize.isEnemies(),toRandomize.isBricks(), toRandomize.isCoins());
			else return new LevelConfig(new Random().nextInt(), toRandomize.getLength(), toRandomize.getPresetDifficulty(),toRandomize.getType(),toRandomize.isEnemies(),toRandomize.isBricks(), toRandomize.isCoins(),odds);
		}
		return new LevelConfig(1, 256, 0, LEVEL_TYPES.OVERGROUND);
	}

	private int seed,length,presetDifficulty;
	private LEVEL_TYPES type;
	private boolean useStandardGenerator,enemies=true,bricks=true,coins=true,flat;
	private int[] odds;
	
	private LevelConfig(int seed, int length, int presetDifficulty, LEVEL_TYPES type,boolean enemies, boolean bricks,boolean coins, int[] odds) { //custom level generation
		this.seed = seed;
		this.length = length;
		this.presetDifficulty = presetDifficulty;
		this.type = type;
		this.useStandardGenerator=false;
		this.enemies = enemies;
		this.bricks = bricks;
		this.coins=coins;
		this.odds = odds;
		this.flat=false;
	}
	
	private LevelConfig(int seed, int length, int presetDifficulty,boolean enemies,boolean bricks,boolean coins) { //flat level
		this.seed = seed;
		this.length = length;
		this.presetDifficulty = presetDifficulty;
		this.useStandardGenerator=false;
		this.enemies = enemies;
		this.bricks = bricks;
		this.coins=coins;
		this.useStandardGenerator=false;
		this.flat=true;
		this.type=LEVEL_TYPES.OVERGROUND;
	}
	
	private LevelConfig(int seed, int length, int presetDifficulty, LEVEL_TYPES type) { //standard level generation
		this.seed = seed;
		this.length = length;
		this.presetDifficulty = presetDifficulty;
		this.type = type;
		this.useStandardGenerator=true;
		this.flat=false;
	}
	
	
	public int getSeed() {
		return seed;
	}
	
	public LEVEL_TYPES getType() {
		return type;
	}
	
	public int getLength() {
		return length;
	}

	public int getPresetDifficulty() {
		return presetDifficulty;
	}

	public void setPresetDifficulty(int difficulty) {
		presetDifficulty=difficulty;
	}

	public boolean isUseStandardGenerator() {
		return useStandardGenerator;
	}

	public boolean isEnemies() {
		return enemies;
	}

	public boolean isBricks() {
		return bricks;
	}
	
	public boolean isCoins() {
		return coins;
	}

	public boolean isFlat() {
		return flat;
	}

	public int[] getOdds() {
		return odds;
	}
}