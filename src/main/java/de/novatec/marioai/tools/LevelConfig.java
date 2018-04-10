package de.novatec.marioai.tools;

import java.util.Random;

import ch.idsia.mario.engine.level.Level.LEVEL_TYPES;
import ch.idsia.mario.engine.level.LevelGenerator;

/**
 * Stores information for the LevelGenerator
 * @author rgu
 *
 */
public class LevelConfig{
	/**
	 * Flat level without enemies, coins or bricks.
	 */
	public static final LevelConfig LEVEL_1=new LevelConfig(0,256,0,false,false,false);
	/**
	 * Level with plains and without enemies, coins or bricks.
	 */
	public static final LevelConfig LEVEL_2=new LevelConfig(0,256,1,LEVEL_TYPES.OVERGROUND,false,false,false,new int[]{10,0,0,0,0});
	/**
	 * Same as {@link #Level_1} but with enemies.
	 */
	public static final LevelConfig LEVEL_3=new LevelConfig(0,256,1,LEVEL_TYPES.OVERGROUND,true,false,false,new int[]{10,0,0,0,0});
	/**
	 * Level with plains, tubes, enemies and without coins or bricks.
	 */
	public static final LevelConfig LEVEL_4=new LevelConfig(2,256,3,LEVEL_TYPES.OVERGROUND,true,false,false,new int[]{10,0,2,0,0});
	/**
	 * Level with plains, hills, pipes, coins, bricks and without enemies.
	 */
	public static final LevelConfig LEVEL_5=new LevelConfig(24,256,3,LEVEL_TYPES.OVERGROUND,false,true,true,new int[]{10,5,2,0,0});
	/**
	 * Same as {@link #Level_5} but with enemies.
	 */
	public static final LevelConfig LEVEL_6=new LevelConfig(24,256,3,LEVEL_TYPES.OVERGROUND,true,true,true,new int[]{10,5,2,0,0});
	/**
	 * Training level with tubes.
	 */
	public static final LevelConfig TUBE_TRAINING=new LevelConfig(0,256,0,LEVEL_TYPES.OVERGROUND,true,false,false,new int[]{0,0,10,0,0});
	/**
	 * Training level with holes
	 */
	public static final LevelConfig JUMP_TRAINING=new LevelConfig(1,256,0,LEVEL_TYPES.OVERGROUND,false,false,false,new int[]{0,0,0,10,0});
	/**
	 * Training level with bullet bills
	 */
	public static final LevelConfig BULLET_TRAINING=new LevelConfig(0,256,0,LEVEL_TYPES.OVERGROUND,false,false,false,new int[]{0,0,0,0,10});
	/**
	 * Easy enemy training level
	 */
	public static final LevelConfig EASY_ENEMY_TRAINING=new LevelConfig(42,256,1,true,false,false);
	/**
	 * Medium enemy training level
	 */
	public static final LevelConfig MEDIUM_ENEMY_TRAINING=new LevelConfig(42,256,4,true,false,false);
	/**
	 * Hard enemy training level
	 */
	public static final LevelConfig HARD_ENEMY_TRAINING=new LevelConfig(42,256,6,true,false,false);
	/**
	 * Want to torchure your A*-Agent? Try this.
	 */
	public static final LevelConfig ASTAR_KILLER=new LevelConfig(652649838,1024,15,LEVEL_TYPES.OVERGROUND);
	/**
	 * Well.. just try it.
	 */
	public static final LevelConfig GOOD_LUCK=new LevelConfig(666,512,15,LEVEL_TYPES.OVERGROUND,true,true,true,new int[]{5,15,2,2,4});
	
//  public static final LevelConfig LevelFLAT1=new LevelConfig(1193454339,256,2,false,true,true);
//	public static final LevelConfig LevelFLAT2=new LevelConfig(1193454339,256,0,false,true,false);
//	public static final LevelConfig LevelCUSTOM1=new LevelConfig(1236445678,512,4,LEVEL_TYPES.CASTLE,false,true,true,new int[]{10,0,0,0,0});
//	public static final LevelConfig LevelCUSTOM2=new LevelConfig(12345678,256,4,LEVEL_TYPES.OVERGROUND,false,false,false,new int[]{0,0,0,10,0});
//	public static final LevelConfig LevelCUSTOM3=new LevelConfig(12345678,256,10,LEVEL_TYPES.OVERGROUND,false,false,false,new int[]{1,0,0,10,0});
	
	/**
	 * Standard level with difficulty 0
	 */
	public static final LevelConfig STANDARD00=new LevelConfig(42,256,0,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 1
	 */
	public static final LevelConfig STANDARD01=new LevelConfig(42,256,1,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 2
	 */
	public static final LevelConfig STANDARD02=new LevelConfig(42,256,2,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 3
	 */
	public static final LevelConfig STANDARD03=new LevelConfig(42,256,3,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 4
	 */
	public static final LevelConfig STANDARD04=new LevelConfig(42,256,4,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 5
	 */
	public static final LevelConfig STANDARD05=new LevelConfig(42,256,5,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 6
	 */
	public static final LevelConfig STANDARD06=new LevelConfig(42,256,6,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 7
	 */
	public static final LevelConfig STANDARD07=new LevelConfig(42,256,7,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 8
	 */
	public static final LevelConfig STANDARD08=new LevelConfig(42,256,8,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 9
	 */
	public static final LevelConfig STANDARD09=new LevelConfig(42,256,9,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 10
	 */
	public static final LevelConfig STANDARD10=new LevelConfig(42,256,10,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 11
	 */
	public static final LevelConfig STANDARD11=new LevelConfig(42,256,11,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 12
	 */
	public static final LevelConfig STANDARD12=new LevelConfig(42,256,12,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 13
	 */
	public static final LevelConfig STANDARD13=new LevelConfig(42,256,13,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 14
	 */
	public static final LevelConfig STANDARD14=new LevelConfig(42,256,14,LEVEL_TYPES.OVERGROUND);
	/**
	 * Standard level with difficulty 15
	 */
	public static final LevelConfig STANDARD15=new LevelConfig(42,256,15,LEVEL_TYPES.OVERGROUND);
	
	/**
	 * Randomizes the given LevelConfig
	 * @param toRandomize
	 * @return
	 */
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
	
	/**
	 * Creates a custom level with the given parameters.
	 * @param seed seed for random()
	 * @param length length of the level
	 * @param presetDifficulty difficulty
	 * @param type LevelType 
	 * @param enemies should enemies be spawned?
	 * @param bricks should bricks be spawned?
	 * @param coins should coins be spawned?
	 * @param odds array with length 5, determines the percentage of level parts [STRAIGHT, HILLS, TUBES, HOLES, BULLETBILL]
	 */
	public LevelConfig(int seed, int length, int presetDifficulty, LEVEL_TYPES type,boolean enemies, boolean bricks,boolean coins, int[] odds) { //custom level generation
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
	
	/**
	 * Creates a level with the given parameters.
	 * @param seed seed for random()
	 * @param length length of the level
	 * @param presetDifficulty difficulty
	 * @param enemies should enemies be spawned?
	 * @param bricks should bricks be spawned?
	 * @param coins should coins be spawned?
	 */
	public LevelConfig(int seed, int length, int presetDifficulty,boolean enemies,boolean bricks,boolean coins) { //flat level
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
	
	/**
	 * Creates a level with the standard level generation and the given parameters.
	 * @param seed seed for random()
	 * @param length length of the level
	 * @param presetDifficulty difficulty
	 * @param type LevelType 
	 */
	public LevelConfig(int seed, int length, int presetDifficulty, LEVEL_TYPES type) { //standard level generation
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