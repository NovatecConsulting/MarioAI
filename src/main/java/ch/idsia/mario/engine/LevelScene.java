package ch.idsia.mario.engine;

import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.LevelGenerator;
import ch.idsia.mario.engine.level.SpriteTemplate;
import ch.idsia.mario.engine.sprites.*;
import ch.idsia.mario.engine.sprites.Mario.MODE;
import ch.idsia.mario.engine.sprites.Mario.STATUS;
import ch.idsia.mario.environments.Environment;
import ch.idsia.utils.MathX;
import de.novatec.mario.engine.generalization.Coordinates;
import de.novatec.mario.engine.generalization.Entities.EntityType;
import de.novatec.mario.engine.generalization.Entity;
import de.novatec.mario.engine.generalization.Tile;
import de.novatec.mario.engine.generalization.Tiles.TileType;
import de.novatec.marioai.tools.LevelConfig;
import de.novatec.marioai.tools.MarioInput;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LevelScene  implements SpriteContext {
	//- Sprites
	private List<Sprite> sprites = new ArrayList<Sprite>();
	private List<Sprite> spritesToAdd = new ArrayList<Sprite>();
	private List<Sprite> spritesToRemove = new ArrayList<Sprite>();
	
	List<Shell> shellsToCheck = new ArrayList<Shell>();

	private Level level;
	private Mario mario;
	private Task task;
	private float xCam, yCam;
	private int tick,lastTickFireball;

	//- Time 
	private static final int TPS=24; // ticks per second, used for correct timing 
	private int startTime = 0;
	private int timeLeft;
	private int totalTime = 200; //standard, but will always be overridden
	private int fireballsOnScreen = 0;
	
	//- LevelInfo
	private long levelSeed;
	private MarioComponent renderer;
	private Level.LEVEL_TYPES levelType;
	private int levelDifficulty;
	private int levelLength;
	//- Statistics (TODO MOVE)
	private int killedCreaturesTotal;
	private int killedCreaturesByFireBall;
	private int killedCreaturesByStomp;
	private int killedCreaturesByShell;

	//- Standard-Constructor
	public LevelScene(MarioComponent renderer,Task task, long seed,int levelDifficulty, Level.LEVEL_TYPES type, int levelLength, int timeLimit) {
		this.levelSeed = seed;
		this.renderer = renderer;
		this.levelDifficulty = levelDifficulty;
		this.levelType = type;
		this.levelLength = levelLength;
		this.setTotalTime(timeLimit);
		killedCreaturesTotal = 0;
		killedCreaturesByFireBall = 0;
		killedCreaturesByStomp = 0;
		killedCreaturesByShell = 0;
		this.task=task;
	}
	
	@Override
	public String toString() {
		return "LevelScene [sprites=" + sprites.size() + ", spritesToAdd=" + spritesToAdd.size() + ", spritesToRemove="
				+ spritesToRemove.size() + ", level=" + level + ", mario=" + mario + ", tick=" + tick 
				+ ", startTime=" + startTime
				+ ", timeLeft=" + timeLeft + ", fireballsOnScreen=" + fireballsOnScreen + ", totalTime=" + totalTime
				+ ", levelSeed=" + levelSeed + ", levelType=" + levelType
				+ ", levelDifficulty=" + levelDifficulty + ", levelLength=" + levelLength + ", killedCreaturesTotal="
				+ killedCreaturesTotal + ", killedCreaturesByFireBall=" + killedCreaturesByFireBall
				+ ", killedCreaturesByStomp=" + killedCreaturesByStomp + ", killedCreaturesByShell="
				+ killedCreaturesByShell + "]";
	}

	public LevelScene(LevelScene toCopy, boolean simpleCopy) {
		super();
		if(toCopy==null) {
			System.err.println("Error: LevelScene can't be copied because toCopy is null!");
			return;
		}
		
		this.sprites = deepCopyList(toCopy.sprites);
		
		if(toCopy.spritesToAdd.isEmpty()) this.spritesToAdd=new ArrayList<>();
		else this.spritesToAdd = deepCopyList(toCopy.spritesToAdd);
		
		if(toCopy.spritesToRemove.isEmpty()) this.spritesToRemove=new ArrayList<>();
		else this.spritesToRemove = deepCopyList(toCopy.spritesToRemove);
		
		this.level = new Level(this, toCopy.level);
		this.shellsToCheck = deepCopyShellList(toCopy.shellsToCheck); 
		for(Shell next:shellsToCheck) if(next.isCarried()) {
			this.mario = new Mario(this,next,toCopy.mario);
			break;
		}
		if(this.mario==null) this.mario = new Mario(this,null,toCopy.mario);
		this.task=toCopy.task;
		sprites.add(mario);
		
		this.xCam = toCopy.xCam;
		this.yCam = toCopy.yCam;
		this.tick = toCopy.tick;
		this.lastTickFireball=toCopy.lastTickFireball;
		
		this.startTime = toCopy.startTime;
		this.timeLeft = toCopy.timeLeft;
		this.totalTime = toCopy.totalTime;
		this.levelSeed = toCopy.levelSeed;
		
		if(!simpleCopy)this.renderer = new MarioComponent(this, toCopy.renderer);
		
		this.levelType = toCopy.levelType;
		this.levelDifficulty = toCopy.levelDifficulty;
		this.levelLength = toCopy.levelLength;
		this.killedCreaturesTotal = toCopy.killedCreaturesTotal;
		this.killedCreaturesByFireBall = toCopy.killedCreaturesByFireBall;
		this.killedCreaturesByStomp = toCopy.killedCreaturesByStomp;
		this.killedCreaturesByShell = toCopy.killedCreaturesByShell;
		this.fireballsOnScreen = toCopy.fireballsOnScreen;
		
		this.fireballsToCheck = deepCopyFireballList(toCopy.fireballsToCheck); 		
	}
	
	private List<Sprite> deepCopyList(List<Sprite> toCopy){
		List<Sprite> res=new ArrayList<Sprite>();
				
		for(Sprite next:toCopy) {
			if(next instanceof Enemy &&  !(next instanceof FlowerEnemy)) {
				res.add(new Enemy(this, (Enemy)next));
			}
			else if(next instanceof BulletBill) {
				res.add(new BulletBill(this, (BulletBill)next));
			}
			else if(next instanceof CoinAnim) {
				res.add(new CoinAnim(this, (CoinAnim)next));
			}
			else if(next instanceof Sparkle) {
				res.add(new Sparkle(this, (Sparkle)next));
			}
			else if(next instanceof Fireball) {
				res.add(new Fireball(this, (Fireball)next));
			}
			else if(next instanceof Particle) {
				res.add(new Particle(this, (Particle)next));
			}
			else if(next instanceof Shell) {
				res.add(new Shell(this, (Shell)next));
			}
			else if(next instanceof FireFlower) {
				res.add(new FireFlower(this, (FireFlower)next));
			}
			else if(next instanceof FlowerEnemy) {
				res.add(new FlowerEnemy(this, (FlowerEnemy)next));
			}
			else if(next instanceof Mushroom) {
				res.add(new Mushroom(this, (Mushroom)next));
			}
		}
		return res;
	}
	
	private List<Shell> deepCopyShellList(List<Shell> toCopy){
		List<Shell> res=new ArrayList<>();
		
		for(Shell next:toCopy) {
			res.add(new Shell(this,next));
		}
		
		return res;
	}
	
	private List<Fireball> deepCopyFireballList(List<Fireball> toCopy){
		List<Fireball> res=new ArrayList<>();
		
		for(Fireball next:toCopy) {
			res.add(new Fireball(this,next));
		}
		
		return res;
	}
	
	private String mapElToStr(int el) {
		String s = "";
		if (el == 0 || el == 1)
			s = "##";
		s += (el == mario.getKind()) ? "#M.#" : el;
		while (s.length() < 4)
			s += "#";
		return s + " ";
	}

	private String enemyToStr(int el) {
		String s = "";
		if (el == 0)
			s = "";
		s += (el == mario.getKind()) ? "-m" : el;
		while (s.length() < 2)
			s += "#";
		return s + " ";
	}

	private byte ZLevelMapElementGeneralization(byte el, int ZLevel) {
		if (el == 0)
			return 0;
		switch (ZLevel) {
		case (0):
			switch (el) {
			case 16: // brick, simple, without any surprise.
			case 17: // brick with a hidden coin
			case 18: // brick with a hidden flower
				return 16; // prevents cheating
			case 21: // question brick, contains coin
			case 22: // question brick, contains flower/mushroom
				return 21; // question brick, contains something
			}
			return el;
		case (1):
			switch (el) {
			case 16: // brick, simple, without any surprise.
			case 17: // brick with a hidden coin
			case 18: // brick with a hidden flower
				return 16; // prevents cheating
			case 21: // question brick, contains coin
			case 22: // question brick, contains flower/mushroom
				return 21; // question brick, contains something
			case (-108):
			case (-107):
			case (-106):
			case (15): // Sparcle, irrelevant
				return 0;
			case (34):
				return 34; // COINS $$$
			case (-128):
			case (-127):
			case (-126):
			case (-125):
			case (-120):
			case (-119):
			case (-118):
			case (-117):
			case (-116):
			case (-115):
			case (-114):
			case (-113):
			case (-112):
			case (-111):
			case (-110):
			case (-109):
			case (-104):
			case (-103):
			case (-102):
			case (-101):
			case (-100):
			case (-99):
			case (-98):
			case (-97):
			case (-69):
			case (-65):
			case (-88):
			case (-87):
			case (-86):
			case (-85):
			case (-84):
			case (-83):
			case (-82):
			case (-81):
			case (4): // kicked hidden brick
			case (9):
				return -10; // border, cannot pass through, can stand on
			case (-124):
			case (-123):
			case (-122):
			case (-76):
			case (-74):
				return -11; // half-border, can jump through from bottom and can stand on
			case (10):
			case (11):
			case (26):
			case (27):
				return 20;// flower pot
			case (14):
			case (30):
			case (46): // canon
				return 14; // angry flower pot or cannon
			}
			System.err.println("Unknown value el = " + el + " ; Please, inform the developers");
			return el;
		case (2):
			switch (el) {
			// cancel out half-borders, that could be passed through
			case (0):
			case (-108):
			case (-107):
			case (-106):
			case (34): // coins
			case (15): // Sparcle, irrelevant
				return 0;
			}
			return 1; // everything else is "something", so it is 1
		}
		System.err.println("Unkown ZLevel Z" + ZLevel);
		return el; // TODO: Throw unknown ZLevel exception
	}

	private byte ZLevelEnemyGeneralization(byte el, int ZLevel) {

		switch (ZLevel) {
		case (0):

			switch (el) {
			// cancel irrelevant sprite codes
			case (Sprite.KIND_COIN_ANIM):
			case (Sprite.KIND_PARTICLE):
			case (Sprite.KIND_SPARCLE):
				
				return Sprite.KIND_NONE;
			}
			return el; // all the rest should go as is
		case (1):

			switch (el) {
			case (Sprite.KIND_COIN_ANIM):
			case (Sprite.KIND_PARTICLE):
			case (Sprite.KIND_SPARCLE):
				return Sprite.KIND_NONE;
			case (Sprite.KIND_MARIO):
				return Sprite.KIND_MARIO;
			case (Sprite.KIND_FIREBALL):
				return Sprite.KIND_FIREBALL;
			case (Sprite.KIND_BULLET_BILL):
			case (Sprite.KIND_GOOMBA):
			case (Sprite.KIND_GOOMBA_WINGED):
			case (Sprite.KIND_GREEN_KOOPA):
			case (Sprite.KIND_GREEN_KOOPA_WINGED):
			case (Sprite.KIND_RED_KOOPA):
			case (Sprite.KIND_RED_KOOPA_WINGED):
			case (Sprite.KIND_SHELL):

				return Sprite.KIND_GOOMBA;
			case (Sprite.KIND_SPIKY):
			case (Sprite.KIND_ENEMY_FLOWER):
			case (Sprite.KIND_SPIKY_WINGED):
				return Sprite.KIND_SPIKY;
			}
			System.err.println("UNKOWN el = " + el);
			return el;
		case (2):
			switch (el) {
			case (Sprite.KIND_COIN_ANIM):
			case (Sprite.KIND_PARTICLE):
			case (Sprite.KIND_SPARCLE):
			case (Sprite.KIND_FIREBALL):

				return Sprite.KIND_NONE;
			case (Sprite.KIND_MARIO):
				return Sprite.KIND_MARIO;
			case (Sprite.KIND_BULLET_BILL):
			case (Sprite.KIND_GOOMBA):
			case (Sprite.KIND_GOOMBA_WINGED):
			case (Sprite.KIND_GREEN_KOOPA):
			case (Sprite.KIND_GREEN_KOOPA_WINGED):
			case (Sprite.KIND_RED_KOOPA):
			case (Sprite.KIND_RED_KOOPA_WINGED):
			case (Sprite.KIND_SHELL):
			case (Sprite.KIND_SPIKY):
			case (Sprite.KIND_ENEMY_FLOWER):
				return 1;
			}
			System.err.println("Z2 UNKNOWNN el = " + el);
			return 1;
		}
		return el; // TODO: Throw unknown ZLevel exception
	}

	public byte[][] levelSceneObservation(int ZLevel) {
		byte[][] ret = new byte[Environment.HalfObsWidth * 2][Environment.HalfObsHeight * 2];

		int MarioXInMap = (int) mario.getX() / 16;
		int MarioYInMap = (int) mario.getY() / 16;

		for (int y = MarioYInMap - Environment.HalfObsHeight, obsX = 0; y < MarioYInMap
				+ Environment.HalfObsHeight; y++, obsX++) {
			for (int x = MarioXInMap - Environment.HalfObsWidth, obsY = 0; x < MarioXInMap
					+ Environment.HalfObsWidth; x++, obsY++) {
				if (x >= 0 /* && x <= level.xExit */ && y >= 0 && y < level.getHeight()) {
					ret[obsX][obsY] = ZLevelMapElementGeneralization(level.getMap(x, y), ZLevel);
				} else
					ret[obsX][obsY] = 0;
				// if (x == MarioXInMap && y == MarioYInMap)
				// ret[obsX][obsY] = mario.kind;
			}
		}
		return ret;
	}

	public Map<Coordinates, Tile> getTiles() {
		Map<Coordinates, Tile> res = new HashMap<>();

		int MarioXInMap = (int) mario.getX() / 16;
		int MarioYInMap = (int) mario.getY() / 16;

		for (int y = MarioYInMap - Environment.HalfObsHeight; y < MarioYInMap + Environment.HalfObsHeight; y++) {

			for (int x = MarioXInMap - Environment.HalfObsWidth; x < MarioXInMap + Environment.HalfObsWidth; x++) {

				if (x >= 0 && y >= 0 && y < level.getHeight()) {
					TileType tile;
					switch (level.getMap(x, y)) {
					case 16: // brick, simple, without any surprise.
					case 17: // brick with a hidden coin
					case 18: // brick with a hidden flower
						tile = TileType.BREAKABLE_BRICK; // prevents cheating
						break;
					case 21: // question brick, contains coin
					case 22: // question brick, contains flower/mushroom
						tile = TileType.QUESTION_BRICK; // question brick, contains something
						break;
					case (-108):
					case (-107):
					case (-106):
					case (15): // Sparcle, irrelevant
						tile = TileType.NOTHING;
						break;
					case (34):
						tile = TileType.COIN; // COINS $$$
						break;
					case (-128):
					case (-127):
					case (-126):
					case (-125):
					case (-120):
					case (-119):
					case (-118):
					case (-117):
					case (-116):
					case (-115):
					case (-114):
					case (-113):
					case (-112):
					case (-111):
					case (-110):
					case (-109):
					case (-104):
					case (-103):
					case (-102):
					case (-101):
					case (-100):
					case (-99):
					case (-98):
					case (-97):
					case (-69):
					case (-65):
					case (-88):
					case (-87):
					case (-86):
					case (-85):
					case (-84):
					case (-83):
					case (-82):
					case (-81):
					case (4): // kicked hidden brick
					case (9):
						tile = TileType.UNBREAKABLE_BRICK; // border, cannot pass through, can stand on
						break;
					case (-124):
					case (-123):
					case (-122):
					case (-76):
					case (-74):
						tile = TileType.LADDER; // half-border, can jump through from bottom and can stand on
						break;
					case (10):
					case (11):
					case (26):
					case (27):
						tile = TileType.FLOWER_POT;// flower pot
						break;
					case (14):
					case (30):
					case (46): // canon
						tile = TileType.CANON; // angry flower pot or cannon
						break;
					default:
						tile = TileType.UNKNOWN;
						break;
					}
					// System.out.println(" Tile: "+tile+" x: "+(x-MarioXInMap)+" y:
					// "+(y-MarioYInMap));

					// --- HEAVY DEBUG
					// int tmp=level.map[x][y];
					// int tmpx=x-MarioXInMap,tmpy=y-MarioYInMap;
					//
					// if(tmp<-99) System.out.print(level.map[x][y]+"["+tmpx+","+tmpy+"]");
					// else if(tmp<-9) System.out.print(" "+level.map[x][y]+"["+tmpx+","+tmpy+"]");
					// else if(tmp<0) System.out.print(level.map[x][y]+" "+"["+tmpx+","+tmpy+"]");
					// else if(tmp<10) System.out.print(" "+level.map[x][y]+"["+tmpx+","+tmpy+"]");
					// else System.out.print(" "+level.map[x][y]+"["+tmpx+","+tmpy+"]");

					if (level.getMap(x, y) != 0)
						res.put(new Coordinates(x - MarioXInMap, y - MarioYInMap), new Tile(new Coordinates(x-MarioXInMap,y-MarioYInMap),tile));
				}

			}
			// System.out.println();
		}
		return res;
	}

	public byte[][] enemiesObservation(int ZLevel) {
		byte[][] ret = new byte[Environment.HalfObsWidth * 2][Environment.HalfObsHeight * 2];
		// TODO: Move to constants 16
		int MarioXInMap = (int) mario.getX() / 16;
		int MarioYInMap = (int) mario.getY() / 16;

		for (int w = 0; w < ret.length; w++)
			for (int h = 0; h < ret[0].length; h++)
				ret[w][h] = 0;
		// ret[Environment.HalfObsWidth][Environment.HalfObsHeight] = mario.kind;
		for (Sprite sprite : sprites) {
			if (sprite.getKind() == mario.getKind())
				continue;
			if (sprite.getMapX() >= 0 && sprite.getMapX() > MarioXInMap - Environment.HalfObsWidth
					&& sprite.getMapX() < MarioXInMap + Environment.HalfObsWidth && sprite.getMapY() >= 0
					&& sprite.getMapY() > MarioYInMap - Environment.HalfObsHeight
					&& sprite.getMapY() < MarioYInMap + Environment.HalfObsHeight) {
				int obsX = sprite.getMapY() - MarioYInMap + Environment.HalfObsHeight;
				int obsY = sprite.getMapX() - MarioXInMap + Environment.HalfObsWidth;
				ret[obsX][obsY] = ZLevelEnemyGeneralization(sprite.getKind(), ZLevel);
			}
		}
		return ret;
	}

	public Map<Coordinates, List<Entity>> getEntities() {
		int MarioXInMap = (int) mario.getX() / 16;
		int MarioYInMap = (int) mario.getY() / 16;

		Map<Coordinates, List<Entity>> entities = new HashMap<>();

		for (Sprite sprite : sprites) {
			if (sprite.getKind() == mario.getKind())
				continue;
			if (sprite.getMapX() >= 0 && sprite.getMapX() > MarioXInMap - Environment.HalfObsWidth
					&& sprite.getMapX() < MarioXInMap + Environment.HalfObsWidth && sprite.getMapY() >= 0
					&& sprite.getMapY() > MarioYInMap - Environment.HalfObsHeight
					&& sprite.getMapY() < MarioYInMap + Environment.HalfObsHeight) {
				Coordinates c = new Coordinates(sprite.getMapX() - MarioXInMap, sprite.getMapY() - MarioYInMap);
				List<Entity> tmp = entities.get(c);

				EntityType type = EntityType.getKindByType(sprite.getKind());
				Entity toAdd = new Entity(type, c);

				if (tmp == null && type != EntityType.NONE) {
					tmp = new LinkedList<>();

					tmp.add(toAdd);
					entities.put(c, tmp);
				} else {
					if (type != EntityType.NONE)
						tmp.add(toAdd);
				}
			}
		}
		return entities;
	}

	public float[] enemiesFloatPos() {
		List<Float> poses = new ArrayList<Float>();
		for (Sprite sprite : sprites) {
			// check if is an influenceable creature
			if (sprite.getKind() >= Sprite.KIND_GOOMBA && sprite.getKind() <= Sprite.KIND_MUSHROOM) {
				poses.add((float) sprite.getKind());
				poses.add(sprite.getX());
				poses.add(sprite.getY());
			}
		}

		float[] ret = new float[poses.size()];

		int i = 0;
		for (Float F : poses)
			ret[i++] = F;

		return ret;
	}

	public byte[][] mergedObservation(int ZLevelScene, int ZLevelEnemies) {
		byte[][] ret = new byte[Environment.HalfObsWidth * 2][Environment.HalfObsHeight * 2];
		
		int MarioXInMap =  mario.getMapX();
		int MarioYInMap =  mario.getMapY();

		for (int y = MarioYInMap - Environment.HalfObsHeight, obsX = 0; y < MarioYInMap
				+ Environment.HalfObsHeight; y++, obsX++) {
			for (int x = MarioXInMap - Environment.HalfObsWidth, obsY = 0; x < MarioXInMap
					+ Environment.HalfObsWidth; x++, obsY++) {
				if (x >= 0 /* && x <= level.xExit */ && y >= 0 && y < level.getHeight()) {
					ret[obsX][obsY] = ZLevelMapElementGeneralization(level.getMap(x, y), ZLevelScene);
				} else
					ret[obsX][obsY] = 0;
				// if (x == MarioXInMap && y == MarioYInMap)
				// ret[obsX][obsY] = mario.kind;
			}
		}

		// for (int w = 0; w < ret.length; w++)
		// for (int h = 0; h < ret[0].length; h++)
		// ret[w][h] = -1;
		// ret[Environment.HalfObsWidth][Environment.HalfObsHeight] = mario.kind;
		for (Sprite sprite : sprites) {
			if (sprite.getKind() == mario.getKind())
				continue;
			if (sprite.getMapX() >= 0 && sprite.getMapX() > MarioXInMap - Environment.HalfObsWidth
					&& sprite.getMapX() < MarioXInMap + Environment.HalfObsWidth && sprite.getMapY() >= 0
					&& sprite.getMapY() > MarioYInMap - Environment.HalfObsHeight
					&& sprite.getMapY() < MarioYInMap + Environment.HalfObsHeight) {
				int obsX = sprite.getMapY() - MarioYInMap + Environment.HalfObsHeight;
				int obsY = sprite.getMapX() - MarioXInMap + Environment.HalfObsWidth;
				if (ret[obsX][obsY] != 14) {
					byte tmp = ZLevelEnemyGeneralization(sprite.getKind(), ZLevelEnemies);
					if (tmp != Sprite.KIND_NONE)
						ret[obsX][obsY] = tmp;
				}
			}
		}

		return ret;
	}

	public String bitmapLevelObservation(int ZLevel) {
		String ret = "";
		int MarioXInMap =  mario.getMapX();
		int MarioYInMap =  mario.getMapY();

		char block = 0;
		byte bitCounter = 0;
		for (int y = MarioYInMap - Environment.HalfObsHeight; y < MarioYInMap
				+ Environment.HalfObsHeight; y++) {
			for (int x = MarioXInMap - Environment.HalfObsWidth; x < MarioXInMap
					+ Environment.HalfObsWidth; x++) {
				if (bitCounter > 15) {
					// update a symbol and store the current one
					ret += block;
					block = 0;
					bitCounter = 0;
				}
				if (x >= 0 && x <= level.getXExit() && y >= 0 && y < level.getHeight()) {
					int temp = ZLevelMapElementGeneralization(level.getMap(x, y), ZLevel);
					if (temp != 0)
						block |= MathX.powsof2[bitCounter];
				}
				++bitCounter;
			}
			// if (block != 0)
			// {
			// System.out.println("block = " + block);
			// show(block);
			// }

		}

		if (bitCounter > 0)
			ret += block;

		// try {
		// String s = new String(code, "UTF8");
		// System.out.println("s = " + s);
		// ret = s;
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace(); //To change body of catch statement use File | Settings
		// | File Templates.
		// }
		// System.out.println("totalBits = " + totalBits);
		// System.out.println("totalBytes = " + totalBytes);
		// System.out.println("ret = " + ret);

		return ret;
	}

	public String bitmapEnemiesObservation(int ZLevel) {
		String ret = "";
		byte[][] enemiesObservation = enemiesObservation(ZLevel);
		mario.getX();
		mario.getY();

		char block = 0;
		char bitCounter = 0;
		for (int i = 0; i < enemiesObservation.length; ++i) {
			for (int j = 0; j < enemiesObservation[0].length; ++j) {
				if (bitCounter > 7) {
					// update a symbol and store the current one
					ret += block;
					block = 0;
					bitCounter = 0;
				}
				int temp = enemiesObservation[i][j];
				if (temp != -1)
					block |= MathX.powsof2[bitCounter];
				++bitCounter;
			}
			// if (block != 0)
			// {
			// System.out.println("block = " + block);
			// show(block);
			// }

		}

		if (bitCounter > 0)
			ret += block;

		// System.out.println("totalBits = " + totalBits);
		// System.out.println("totalBytes = " + totalBytes);
		// System.out.println("ret = " + ret);
		return ret;
	}

	public List<String> LevelSceneAroundMarioASCII(boolean Enemies, boolean LevelMap, boolean mergedObservationFlag,
			int ZLevelScene, int ZLevelEnemies) {
		// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));//
		// bw.write("\nTotal world width = " + level.width);
		List<String> ret = new ArrayList<String>();
		if (level != null && mario != null) {
			ret.add("Total world width = " + level.getWidth());
			ret.add("Total world height = " + level.getHeight());
			ret.add("Physical Mario Position (x,y): (" + mario.getX() + "," + mario.getY() + ")");
			ret.add("Mario Observation Width " + Environment.HalfObsWidth * 2);
			ret.add("Mario Observation Height " + Environment.HalfObsHeight * 2);
			ret.add("X Exit Position: " + getLevelXExit());
			int MarioXInMap =  mario.getMapX();
			int MarioYInMap =  mario.getMapY();
			ret.add("Calibrated Mario Position (x,y): (" + MarioXInMap + "," + MarioYInMap + ")\n");

			byte[][] levelScene = levelSceneObservation(ZLevelScene);
			if (LevelMap) {
				ret.add("~ZLevel: Z" + ZLevelScene + " map:\n");
				for (int x = 0; x < levelScene.length; ++x) {
					String tmpData = "";
					for (int y = 0; y < levelScene[0].length; ++y)
						tmpData += mapElToStr(levelScene[x][y]);
					ret.add(tmpData);
				}
			}

			byte[][] enemiesObservation = null;
			if (Enemies || mergedObservationFlag) {
				enemiesObservation = enemiesObservation(ZLevelEnemies);
			}

			if (Enemies) {
				ret.add("~ZLevel: Z" + ZLevelScene + " Enemies Observation:\n");
				for (int x = 0; x < enemiesObservation.length; x++) {
					String tmpData = "";
					for (int y = 0; y < enemiesObservation[0].length; y++) {
						// if (x >=0 && x <= level.xExit)
						tmpData += enemyToStr(enemiesObservation[x][y]);
					}
					ret.add(tmpData);
				}
			}

			if (mergedObservationFlag) {
				// ret.add("~ZLevel: Z" + ZLevelScene + "===========\nAll objects:
				// (LevelScene[x,y], Sprite[x,y])==/* Mario ~> MM */=====\n");
				// for (int x = 0; x < levelScene.length; ++x)
				// {
				// String tmpData = "";
				// for (int y = 0; y < levelScene[0].length; ++y)
				// tmpData += "(" + levelScene[x][y] + "," + enemiesObservation[x][y] + ")";
				// ret.add(tmpData);
				// }

				byte[][] mergedObs = mergedObservation(ZLevelScene, ZLevelEnemies);
				ret.add("~ZLevelScene: Z" + ZLevelScene + " ZLevelEnemies: Z" + ZLevelEnemies
						+ " ; Merged observation /* Mario ~> #M.# */");
				for (int x = 0; x < levelScene.length; ++x) {
					String tmpData = "";
					for (int y = 0; y < levelScene[0].length; ++y)
						tmpData += mapElToStr(mergedObs[x][y]);
					ret.add(tmpData);
				}
			}
		} else
			ret.add("~level or mario is not available");
		return ret;
	}

	public void init(LevelConfig config, MODE startMode) {
		try {
			Level.loadBehaviors(new DataInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("tiles.dat")));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
			
		if(config.isUseStandardGenerator()) {
			level = LevelGenerator.createLevel(config.getLength(), 15, config.getSeed(), config.getPresetDifficulty(), config.getType()); // Standard level-generation
		}
		else if(config.isFlat()) {
			level=LevelGenerator.createFlatLevel(config.getLength(), 15, config.getSeed(), config.getPresetDifficulty(), config.isEnemies(), config.isBricks(), config.isCoins()); // flat level generation
		}
		else level=LevelGenerator.createCustomLevel(config.getLength(), 15, config.getSeed(), config.getPresetDifficulty(), config.getType(), config.getOdds(),config.isEnemies(), config.isBricks(), config.isCoins()); //custom level generation

		sprites.clear();
		
		mario = new Mario(this,startMode);

		sprites.add(mario);
		startTime = 1;

		timeLeft = totalTime * TPS;
		tick = 0;
		lastTickFireball=-1;
	}
	
	public void checkShellCollide(Shell shell) {
		shellsToCheck.add(shell);
	}

	List<Fireball> fireballsToCheck = new ArrayList<Fireball>();

	public void checkFireballCollide(Fireball fireball) {
		fireballsToCheck.add(fireball);
	}

	public void tick() {
		
		if (mario.getStatus()==STATUS.RUNNING)
			timeLeft--;
		
		if (timeLeft == 0) {
			mario.die();			
		}
		
		if (startTime > 0&&mario.getStatus()==STATUS.RUNNING) {
			startTime++;
		}

		float targetXCam = mario.getX() - 160;

		xCam = targetXCam;

		if (xCam < 0)
			xCam = 0;
		if (xCam > level.getWidth() * 16 - 320)
			xCam = level.getWidth() * 16 - 320;

		fireballsOnScreen = 0;

		for (Sprite sprite : sprites) {
			if (sprite != mario) {
				float xd = sprite.getX() - xCam;
				float yd = sprite.getY() - yCam;
				if (xd < -64 || xd > 320 + 64 || yd < -64 || yd > 240 + 64) {
					removeSprite(sprite);
				} else {
					if (sprite instanceof Fireball) {
						fireballsOnScreen++;
					}
				}
			}
		}

	
		
			tick++;
			level.tick();

			for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + 320) / 16 + 1; x++)
				for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + 240) / 16 + 1; y++) {
					int dir = 0;

					if (x * 16 + 8 > mario.getX() + 16)
						dir = -1;
					if (x * 16 + 8 < mario.getX() - 16)
						dir = 1;

					SpriteTemplate st = level.getSpriteTemplate(x, y);

					if (st != null) {
						if (st.getLastVisibleTick() != tick - 1) {
							if (st.getSprite() == null || !sprites.contains(st.getSprite())) {
								st.spawn(this, x, y, dir);
							}
						}

						st.setLastVisinleTick(tick); 					}

					if (dir != 0) {
						byte b = level.getBlock(x, y);
						if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0) {
							if ((b % 16) / 4 == 3 && b / 16 == 0) {
								if ((tick - x * 2) % 100 == 0) {
									for (int i = 0; i < 8; i++) {
										addSprite(new Sparkle(this,x * 16 + 8, y * 16 + (int) (Math.random() * 16),
												(float) Math.random() * dir, 0, 0, 1, 5));
									}
									addSprite(new BulletBill(this, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
								}
							}
						}
					}
				}

			for (Sprite sprite : sprites) {
				sprite.tick();
			}
			

			for (Sprite sprite : sprites) {
				sprite.collideCheck();
			}

			for (Shell shell : shellsToCheck) {
				for (Sprite sprite : sprites) {
					if (sprite != shell && !shell.isDead()) {
						if (sprite.shellCollideCheck(shell)) {
							if (mario.getCarried() == shell && !shell.isDead()) {
								mario.setCarried(null);
								shell.die();
								++this.killedCreaturesTotal;
							}
						}
					}
				}
			}
			shellsToCheck.clear();

			for (Fireball fireball : fireballsToCheck) {
				for (Sprite sprite : sprites) {
					if (sprite != fireball && !fireball.isDead()) {
						if (sprite.fireballCollideCheck(fireball)) {
							fireball.die();
						}
					}
				}
			}
			fireballsToCheck.clear();
		

		sprites.addAll(spritesToAdd);
		sprites.removeAll(spritesToRemove);
		spritesToAdd.clear();
		spritesToRemove.clear();
}

	public void addSprite(Sprite sprite) {
		spritesToAdd.add(sprite);
		sprite.tick();
	}

	public void removeSprite(Sprite sprite) {
		spritesToRemove.add(sprite);
	}

	public void bump(int x, int y, boolean canBreakBricks) {
		byte block = level.getBlock(x, y);

		if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BUMPABLE) > 0) {
			bumpInto(x, y - 1);
			level.setBlock(x, y, (byte) 4);
			level.setBlockData(x, y, (byte) 4);

			if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_SPECIAL) > 0) {
				if (!mario.isLarge()) {
					addSprite(new Mushroom(this, x * 16 + 8, y * 16 + 8));
				} else {
					addSprite(new FireFlower(this, x * 16 + 8, y * 16 + 8));
				}
			} else {
				mario.getCoin();
				addSprite(new CoinAnim(this,x, y));
			}
		}

		if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BREAKABLE) > 0) {
			bumpInto(x, y - 1);
			if (canBreakBricks) {
				level.setBlock(x, y, (byte) 0);
				for (int xx = 0; xx < 2; xx++)
					for (int yy = 0; yy < 2; yy++)
						addSprite(new Particle(this,x * 16 + xx * 8 + 4, y * 16 + yy * 8 + 4, (xx * 2 - 1) * 4,
								(yy * 2 - 1) * 4 - 8));
			} else {
				level.setBlockData(x, y, (byte) 4);
			}
		}
	}

	public void bumpInto(int x, int y) {
		byte block = level.getBlock(x, y);
		if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0) {
			mario.getCoin();
			level.setBlock(x, y, (byte) 0);
			addSprite(new CoinAnim(this,x, y + 1));
		}

		for (Sprite sprite : sprites) {
			sprite.bumpCheck(x, y);
		}
	}

	public int getFireballsOnScreen() {
		return this.fireballsOnScreen;
	}
	
	public int getStartTime() {
		return totalTime-timeLeft / TPS;
	}

	public int getTimeLeft() {
		return timeLeft / TPS;
	}
	
	public int getExactTimeLeft() {
		return timeLeft;
	}
	
	public int getExactStartTime() {
		return totalTime*TPS-timeLeft;
	}
	
	
	public int getKilledCreaturesTotal() {
		return killedCreaturesTotal;
	}

	public void incrementKilledCreaturesTotal() {
		++killedCreaturesTotal;
	}

	public int getKilledCreaturesByFireBall() {
		return killedCreaturesByFireBall;
	}

	public void killedCreaturesByFireBall() {
		++killedCreaturesByFireBall;
	}

	public int getKilledCreaturesByStomp() {
		return killedCreaturesByStomp;
	}

	public void killedCreatureByStomp() {
		++ killedCreaturesByStomp;
	}

	public int getKilledCreaturesByShell() {
		return killedCreaturesByShell;
	}

	public void killedCreaturesByShell() {
		++killedCreaturesByShell;
	}

	//---Mario Getter/Setter
	
	public float getMarioX() {
		return mario.getX();
	}
	
	public int getMarioMapX() {
		return mario.getMapX();
	}
	
	public float getMarioXA() {
		return mario.getXa();
	}
	
	public float getMarioXCam() {
		return xCam;
	}
	
	public float getMarioY() {
		return mario.getY();
	}
	
	public int getMarioMapY() {
		return mario.getMapY();
	}
	
	public float getMarioYA() {
		return mario.getYa();
	}
	
	public Coordinates getMarioPos() {
		return new Coordinates(this.getMarioMapX(), this.getMarioMapY());
	}
	
	public Coordinates getMarioFloatPos() {
		return new Coordinates(this.getMarioX(),this.getMarioY());
	}
	
	public int getMarioFacing() {
		return mario.getFacing();
	}
	
	public boolean isMarioSliding() {
		return mario.isSliding();
	}
	
	public void getMarioHurt() {
		mario.getHurt();
	}
	
	public void marioKick(Shell shell) {
		mario.kick(shell);
	}
	
	public Sprite getMarioCarried() {
		return mario.getCarried();
	}
	
	public boolean isMarioCarrying() {
		return getMarioCarried()!=null;
	}
	
	public int getMarioCoins() {
		return mario.getCoins();
	}
	
	public boolean mayMarioJump() {
		return mario.mayJump();
	}
	
	public boolean mayMarioShoot() {
		if(lastTickFireball==tick-1) return false;
		return mario.getMode()==MODE.MODE_FIRE&&fireballsOnScreen<2;
	}
	
	public boolean isMarioFalling() {
		return !isMarioOnGround()&&!(getMarioYA()<=0);
	}
	
	public void setMarioCarried(Sprite carried) {
		mario.setCarried(carried);
	}
	
	public int getMarioHeight() {
		return mario.getHeight();
	}
	
	public void resetMarioCoins() {
		mario.resetCoins();
	}
	
	public MODE getMarioMode() {
		return mario.getMode();
	}
	
	public void resetMario(MODE marioMode) {
		mario.reset(marioMode);
	}
	
	public STATUS getMarioStatus(){
		return mario.getStatus();
	}
	
	public boolean isMarioOnGround() {
		return mario.isOnGround();
	}
	
	public boolean wasMarioOnGround() {
		
		return mario.wasOnGround();
	}
	
	public void marioStomp(Shell shell) {
		 mario.stomp(shell);
	}
	
	public void marioStomp(BulletBill bulletBill) {
		mario.stomp(bulletBill);
	}
	
	public void marioStomp(Enemy enemy) {
		mario.stomp(enemy);
	}
	
	public void getMarioMushroom() {
		mario.getMushroom();
	}
	
	public void getMarioFlower() {
		mario.getFlower();
	}
	
	public void setMarioKeys(boolean[] input) {
		mario.setKeys(input);
	}
	
	public void setMarioInput(MarioInput input) {
		this.setMarioKeys(input.toArray());
	}

	public int getTimesMarioHurt() {
		return this.mario.getTimesHurt();
	}
	
	public int getMarioGainedFowers() {
		return this.mario.getGainedFlowers();
	}
	
	public int getMarioGainedMushrooms() {
		return this.mario.getGainedMushrooms();
	}
	
	//--- A* Help Methods
	
	public LevelScene getDeepCopy() {
		return new LevelScene(this,false);
	}
	
	public LevelScene getAStarCopy() {
		return new LevelScene(this, true);
	}
	
	public double getScore() {
		return task.getScoreBasesOnValues(mario.getStatus(), getTimeLeft(), mario.getX(), killedCreaturesTotal, killedCreaturesByStomp, killedCreaturesByShell, killedCreaturesByFireBall, mario.getCoins(), mario.getGainedMushrooms(), mario.getGainedFlowers(), mario.getTimesHurt());
	}

	public void usedFireball() {
		lastTickFireball=tick;
	}

	List<Sprite> getSprites() {
		return sprites;
	}
	
	//---Level
	public Level.LEVEL_TYPES getLevelType() {
		return levelType;
	}
	
	public int getTotalCoins() {
		return level.getTotalCoins();
	}

	protected Level getLevel() {
		return level;
	}
	
	public int getLevelDifficulty() {
		return levelDifficulty;
	}
		
	public long getLevelSeed() {
		return levelSeed;
	}

	public boolean levelIsBlocking(int x, int y, float xa, float ya) {
		return level.isBlocking(x, y, xa, ya);
	}
	
	public byte levelGetBlock(int x, int y) {
		return level.getBlock(x, y);
	}
	
	public void setLevelBlock(int x, int y, byte b) {
		level.setBlock(x, y, b);
	}
	
	public int getLevelWidth() {
		return level.getWidth();
	}
	
	public int getLevelHight() {
		return level.getHeight();
	}
	
	public double getLevelWidthPhys() {
		return level.getWidthPhys();
	} 
	
	public int getLevelXExit() {
		return level.getXExit();
	}
	

	//--- Camera

	public float getxCam() {
		return xCam;
	}

	public float getyCam() {
		return yCam;
	}
	
	//---Time & Ticks

	public int getTick() {
		return tick;
	}
	
	public int getTotalTime() {
		return totalTime; 
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
}