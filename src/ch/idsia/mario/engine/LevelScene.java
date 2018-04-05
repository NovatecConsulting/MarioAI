package ch.idsia.mario.engine;

import ch.idsia.mario.engine.level.BgLevelGenerator;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.Level.LEVEL_TYPES;
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

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LevelScene extends Scene implements SpriteContext {
	//- Sprites
	private List<Sprite> sprites = new ArrayList<Sprite>();
	private List<Sprite> spritesToAdd = new ArrayList<Sprite>();
	private List<Sprite> spritesToRemove = new ArrayList<Sprite>();

	private Level level;
	private Mario mario;
	private float xCam, yCam;
	private int tick;

	//- Renderer
	private LevelRenderer layer;
	private BgRenderer[] bgLayer = new BgRenderer[2];

	private GraphicsConfiguration graphicsConfiguration;

	//- Time 
	private boolean paused = false;
	private int startTime = 0;
	private int timeLeft;
	private int totalTime = 200;
	
	private int fireballsOnScreen = 0;
	
	private static final DecimalFormat df = new DecimalFormat("00");
	private static final DecimalFormat df2 = new DecimalFormat("000");

	public int getTotalTime() {
		return totalTime; 
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	// private Recorder recorder = new Recorder();
	// private Replayer replayer = null;

	private long levelSeed;
	private MarioComponent renderer;
	private Level.LEVEL_TYPES levelType;
	private int levelDifficulty;
	private int levelLength;
	private int killedCreaturesTotal;
	private int killedCreaturesByFireBall;
	private int killedCreaturesByStomp;
	private int killedCreaturesByShell;

	public LevelScene(GraphicsConfiguration graphicsConfiguration, MarioComponent renderer, long seed,
			int levelDifficulty, Level.LEVEL_TYPES type, int levelLength, int timeLimit) {
		this.graphicsConfiguration = graphicsConfiguration;
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
	}
	
	@Override
	public String toString() {
		return "LevelScene [sprites=" + sprites.size() + ", spritesToAdd=" + spritesToAdd.size() + ", spritesToRemove="
				+ spritesToRemove.size() + ", level=" + level + ", mario=" + mario + ", xCam=" + xCam + ", yCam=" + yCam
				+ ", tick=" + tick + ", layer=" + layer + ", bgLayer=" + Arrays.toString(bgLayer)
				+ ", graphicsConfiguration=" + graphicsConfiguration + ", paused=" + paused + ", startTime=" + startTime
				+ ", timeLeft=" + timeLeft + ", fireballsOnScreen=" + fireballsOnScreen + ", totalTime=" + totalTime
				+ ", levelSeed=" + levelSeed + ", renderer=" + renderer + ", levelType=" + levelType
				+ ", levelDifficulty=" + levelDifficulty + ", levelLength=" + levelLength + ", killedCreaturesTotal="
				+ killedCreaturesTotal + ", killedCreaturesByFireBall=" + killedCreaturesByFireBall
				+ ", killedCreaturesByStomp=" + killedCreaturesByStomp + ", killedCreaturesByShell="
				+ killedCreaturesByShell + ", shellsToCheck=" + shellsToCheck + ", fireballsToCheck=" + fireballsToCheck
				+ "]";
	}

	public LevelScene(LevelScene toCopy) {
		super();
		if(toCopy==null) {
			System.err.println("Error: LevelScene can't be copied because toCopy is null!");
			return;
		}
		
		this.sprites = deepCopyList(toCopy.sprites);// TODO TESTING
		this.spritesToAdd = deepCopyList(toCopy.spritesToAdd);
		this.spritesToRemove = deepCopyList(toCopy.spritesToRemove);
		
		this.level = new Level(this, toCopy.level); //TODO TESTING
		this.mario = new Mario(this,toCopy.mario);
		sprites.add(mario);
		
		this.xCam = toCopy.xCam;
		this.yCam = toCopy.yCam;
		this.tick = toCopy.tick;
		this.layer = new LevelRenderer(level, toCopy.layer);
		
		this.bgLayer = toCopy.bgLayer; // special cloneable needed?
		
		this.graphicsConfiguration = toCopy.graphicsConfiguration; //read-only, shouldn't be a problem
		
		this.paused = toCopy.paused;
		this.startTime = toCopy.startTime;
		this.timeLeft = toCopy.timeLeft;
		this.totalTime = toCopy.totalTime;
		this.levelSeed = toCopy.levelSeed;
		
		this.renderer = new MarioComponent(this, toCopy.renderer); //TODO testing
		
		this.levelType = toCopy.levelType;
		this.levelDifficulty = toCopy.levelDifficulty;
		this.levelLength = toCopy.levelLength;
		this.killedCreaturesTotal = toCopy.killedCreaturesTotal;
		this.killedCreaturesByFireBall = toCopy.killedCreaturesByFireBall;
		this.killedCreaturesByStomp = toCopy.killedCreaturesByStomp;
		this.killedCreaturesByShell = toCopy.killedCreaturesByShell;
		this.fireballsOnScreen = toCopy.fireballsOnScreen;
		
		this.shellsToCheck = deepCopyShellList(toCopy.shellsToCheck); //TODO TESTING
		this.fireballsToCheck = deepCopyFireballList(toCopy.fireballsToCheck); //TODO TESTING
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
				// case(Sprite.KIND_MARIO):

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
		// TODO: Move to constants 16
		int MarioXInMap = (int) mario.getX() / 16;
		int MarioYInMap = (int) mario.getY() / 16;

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
				// quick fix TODO: handle this in more general way.
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
		int MarioXInMap = (int) mario.getX() / 16;
		int MarioYInMap = (int) mario.getY() / 16;

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
			int MarioXInMap = (int) mario.getX() / 16;
			int MarioYInMap = (int) mario.getY() / 16;
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

	public void init() {
		try {
			Level.loadBehaviors(new DataInputStream(LevelScene.class.getResourceAsStream("resources/tiles.dat")));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		LevelConfig config=renderer.getRunnerOptions().getConfig();
			
		if(config.isUseStandardGenerator()) {
			level = LevelGenerator.createLevel(config.getLength(), 15, config.getSeed(), config.getPresetDifficulty(), config.getType().getType()); // Standard level-generation
		}
		else if(config.isFlat()) {
			level=LevelGenerator.createFlatLevel(config.getLength(), 15, config.getSeed(), config.getPresetDifficulty(), config.isEnemies(), config.isBricks(), config.isCoins()); // flat level generation
		}
		else level=LevelGenerator.createCustomLevel(config.getLength(), 15, config.getSeed(), config.getPresetDifficulty(), config.getType().getType(), config.getOdds(),config.isEnemies(), config.isBricks(), config.isCoins()); //custom level generation
		
		setPaused(false);
		sprites.clear();
		layer = new LevelRenderer(level, graphicsConfiguration, 320, 240);
		for (int i = 0; i < 2; i++) {
			int scrollSpeed = 4 >> i;
			int w = ((level.getWidth() * 16) - 320) / scrollSpeed + 320;
			int h = ((level.getHeight() * 16) - 240) / scrollSpeed + 240;
			Level bgLevel = BgLevelGenerator.createLevel(w / 32 + 1, h / 32 + 1, i == 0, levelType.getType());
			bgLayer[i] = new BgRenderer(bgLevel, graphicsConfiguration, 320, 240, scrollSpeed);
		}
		mario = new Mario(this,renderer.getRunnerOptions().getMarioStartMode());

		sprites.add(mario);
		startTime = 1;

		timeLeft = totalTime * 15;

		tick = 0;
	}

	List<Shell> shellsToCheck = new ArrayList<Shell>();

	public void checkShellCollide(Shell shell) {
		shellsToCheck.add(shell);
	}

	List<Fireball> fireballsToCheck = new ArrayList<Fireball>();

	public void checkFireballCollide(Fireball fireball) {
		fireballsToCheck.add(fireball);
	}

	public void tick() {
	
		//if(paused) return; TODO COULD BE USEFULL
		
		if (renderer.getRunnerOptions().isTimer()&&mario.getStatus()==STATUS.RUNNING&&!paused)
			timeLeft--;
		
		if (timeLeft == 0) {
			mario.die();			
		}
		
		if (startTime > 0&&mario.getStatus()==STATUS.RUNNING&&!paused) {
			startTime++;
		}

		float targetXCam = mario.getX() - 160;

		xCam = targetXCam;

		if (xCam < 0)
			xCam = 0;
		if (xCam > level.getWidth() * 16 - 320)
			xCam = level.getWidth() * 16 - 320;

		/*
		 * if (recorder != null) { recorder.addTick(mario.getKeyMask()); }
		 * 
		 * if (replayer!=null) { mario.setKeys(replayer.nextTick()); }
		 */

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

		if (isPaused()) {
			for (Sprite sprite : sprites) {
//				if (sprite == mario) {
//					sprite.tick();
//				} else {
					sprite.tickNoMove();
//				}
			}
		} else {
			tick++;
			level.tick();

			for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + layer.getWidth()) / 16 + 1; x++)
				for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + layer.getHeight()) / 16 + 1; y++) {
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
		}

		sprites.addAll(0, spritesToAdd);
		sprites.removeAll(spritesToRemove);
		spritesToAdd.clear();
		spritesToRemove.clear();
	}

	public void render(Graphics g, float alpha) {
		int xCam = (int) (mario.getxOld() + (mario.getX() - mario.getxOld()) * alpha) - 160;
		int yCam = (int) (mario.getyOld() + (mario.getY() - mario.getyOld()) * alpha) - 120;

		if (!renderer.getRunnerOptions().isMarioAlwaysCentered()) {
		
			// int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
			// int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
			if (xCam < 0)
				xCam = 0;
			if (yCam < 0)
				yCam = 0;
			if (xCam > level.getWidth() * 16 - 320)
				xCam = level.getWidth() * 16 - 320;
			if (yCam > level.getHeight() * 16 - 240)
				yCam = level.getHeight() * 16 - 240;
		}
		// g.drawImage(Art.background, 0, 0, null);

		for (int i = 0; i < 2; i++) {
			bgLayer[i].setCam(xCam, yCam);
			bgLayer[i].render(g, tick, alpha);
		}

		g.translate(-xCam, -yCam);

		for (Sprite sprite : sprites) {
			if (sprite.getLayer() == 0)
				sprite.render(g, alpha);
		}

		g.translate(xCam, yCam);

		layer.setCam(xCam, yCam);
		layer.render(g, tick, isPaused() ? 0 : alpha);
		layer.renderExit0(g, tick, isPaused() ? 0 : alpha, mario.getWinTime() == 0);

		g.translate(-xCam, -yCam);

		// TODO: Dump out of render!
//		if (mario.getCheatKeys()[Mario.KEY_DUMP_CURRENT_WORLD])
//			for (int w = 0; w < level.getWidth(); w++)
//				for (int h = 0; h < level.getHeight(); h++)
//					level.setObservation(w, h, (byte)-1);

		for (Sprite sprite : sprites) {
			if (sprite.getLayer() == 1)
				sprite.render(g, alpha);
			if (mario.getCheatKeys()[Mario.KEY_DUMP_CURRENT_WORLD] && sprite.getMapX() >= 0
					&& sprite.getMapX() < level.getObservationXLength() && sprite.getMapY() >= 0
					&& sprite.getMapY() < level.getObservationYLength())
				level.setObservation(sprite.getMapX(), sprite.getMapY(), sprite.getKind());

		}

		g.translate(xCam, yCam);
		g.setColor(Color.BLACK);
		layer.renderExit1(g, tick, isPaused() ? 0 : alpha);

		// drawStringDropShadow(g, "MARIO: " + df.format(Mario.lives), 0, 0, 7);
		// drawStringDropShadow(g, "#########", 0, 1, 7);

		drawStringDropShadow(g, "DIFFICULTY:   " + df.format(this.levelDifficulty), 0, 0,
				this.levelDifficulty > 6 ? 1 : this.levelDifficulty > 2 ? 4 : 7);
		drawStringDropShadow(g, "World " + (this.isPaused() ? "paused" : "running"), 19, 0, 7);
		drawStringDropShadow(g, "SEED:" + this.levelSeed, 0, 1, 7);
		drawStringDropShadow(g, "TYPE:" + levelType.toString(), 0, 2, 7);
		drawStringDropShadow(g, "ALL KILLS: " + killedCreaturesTotal, 19, 1, 1);
		drawStringDropShadow(g, "LENGTH:" + (int) mario.getX() / 16 + " of " + this.levelLength, 0, 3, 7);
		drawStringDropShadow(g, "by Fire  : " + killedCreaturesByFireBall, 19, 2, 1);
		drawStringDropShadow(g, "COINS    : " + df.format(mario.getCoins()), 0, 4, 4);
		drawStringDropShadow(g, "by Shell : " + killedCreaturesByShell, 19, 3, 1);
		drawStringDropShadow(g, "MUSHROOMS: " + df.format(mario.getGainedMushrooms()), 0, 5, 4);
		drawStringDropShadow(g, "by Stomp : " + killedCreaturesByStomp, 19, 4, 1);
		drawStringDropShadow(g, "FLOWERS  : " + df.format(mario.getGainedFlowers()), 0, 6, 4);

		drawStringDropShadow(g, "TIME", 33, 0, 7);
		int time = (timeLeft) / 15;
		if (time < 0)
			time = 0;
		drawStringDropShadow(g, " " + df2.format(time), 33, 1, 7);

		drawProgress(g);

		if (renderer.getRunnerOptions().isLabels()) {
			g.drawString("xCam: " + xCam + "yCam: " + yCam, 70, 40);
			g.drawString("x : " + mario.getX() + "y: " + mario.getY(), 70, 50);
			g.drawString("xOld : " + mario.getxOld() + "yOld: " + mario.getyOld(), 70, 60);
		}

		if (startTime > 0) {
			float t = startTime + alpha - 2;
			t = t * t * 0.6f;
			renderBlackout(g, 160, 120, (int) (t));
		}
		// mario.x>level.xExit*16
		if (mario.getWinTime() > 0) {
			float t = mario.getWinTime() + alpha;
			t = t * t * 0.2f;

			if (t > 500) {
				renderer.levelWon();
			}

			renderBlackout(g, mario.getxDeathPos() - xCam, mario.getyDeathPos() - yCam, (int) (320 - t));
		}

		if (mario.getDeathTime() > 0||timeLeft<=0||mario.getStatus()==STATUS.LOOSE) {
			 float t = mario.getDeathTime() + alpha;
			 t = t * t * 0.4f;
			 //System.out.println(alpha);
//			 System.out.println(mario.getDeathTime());
//			 System.out.println(mario.getStatus());
//			 System.out.println(t);
			// if (t > 500){
			 renderer.levelFailed();
			// System.out.println("used");
			// }
			 
			 renderBlackout(g, (int) (mario.getxDeathPos() - xCam), (int) (mario.getyDeathPos() - yCam), (int) (320 - t));
			 

			
		}
	}

	private void drawProgress(Graphics g) {
		String entirePathStr = "......................................>";
		double physLength = (levelLength - 53) * 16;
		int progressInChars = (int) (mario.getX() * (entirePathStr.length() / physLength));
		String progress_str = "";
		for (int i = 0; i < progressInChars - 1; ++i)
			progress_str += ".";
		progress_str += "M";
		try {
			drawStringDropShadow(g, entirePathStr.substring(progress_str.length()), progress_str.length(), 28, 0);
		} catch (StringIndexOutOfBoundsException e) {
			// System.err.println("warning: progress line inaccuracy");
		}
		drawStringDropShadow(g, progress_str, 0, 28, 2);
	}

	public static void drawStringDropShadow(Graphics g, String text, int x, int y, int c) {
		drawString(g, text, x * 8 + 5, y * 8 + 5, 0);
		drawString(g, text, x * 8 + 4, y * 8 + 4, c);
	}

	private static void drawString(Graphics g, String text, int x, int y, int c) { //c: 0=black,1=red,2=green,3=blue,4=yellow
		char[] ch = text.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
		}
	}

	public void renderBlackout(Graphics g, int x, int y, int radius) {
		if (radius > 320)
			return;

		int[] xp = new int[20];
		int[] yp = new int[20];
		for (int i = 0; i < 16; i++) {
			xp[i] = x + (int) (Math.cos(i * Math.PI / 15) * radius);
			yp[i] = y + (int) (Math.sin(i * Math.PI / 15) * radius);
		}
		xp[16] = 320;
		yp[16] = y;
		xp[17] = 320;
		yp[17] = 240;
		xp[18] = 0;
		yp[18] = 240;
		xp[19] = 0;
		yp[19] = y;
		g.fillPolygon(xp, yp, xp.length);

		for (int i = 0; i < 16; i++) {
			xp[i] = x - (int) (Math.cos(i * Math.PI / 15) * radius);
			yp[i] = y - (int) (Math.sin(i * Math.PI / 15) * radius);
		}
		xp[16] = 320;
		yp[16] = y;
		xp[17] = 320;
		yp[17] = 0;
		xp[18] = 0;
		yp[18] = 0;
		xp[19] = 0;
		yp[19] = y;

		g.fillPolygon(xp, yp, xp.length);
	}

	public void addSprite(Sprite sprite) {
		spritesToAdd.add(sprite);
		sprite.tick();
	}

	public void removeSprite(Sprite sprite) {
		spritesToRemove.add(sprite);
	}

	public float getX(float alpha) {
		int xCam = (int) (mario.getxOld() + (mario.getX() - mario.getxOld()) * alpha) - 160;
		// int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;
		// int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
		// int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
		if (xCam < 0)
			xCam = 0;
		// if (yCam < 0) yCam = 0;
		// if (yCam > 0) yCam = 0;
		return xCam + 160;
	}

	public float getY(float alpha) {
		return 0;
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

	// public void update(boolean[] action)
	// {
	// System.arraycopy(action, 0, mario.keys, 0, 6);
	// }

	public int getFireballsOnScreen() {
		return this.fireballsOnScreen;
	}
	
	public int getStartTime() {
		return totalTime-timeLeft / 15;
	}

	public int getTimeLeft() {
		return timeLeft / 15;
	}
	
	public boolean isPaused() {
		return paused;
	}
 
	public void setPaused(boolean paused) {
		this.paused = paused;
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
	
	//---Mario Getter/Setter
	
	public float getMarioX() {
		return mario.getX();
	}
	
	public int getMarioMapX() {
		return mario.getMapX();
	}
	
	public float getMarioY() {
		return mario.getY();
	}
	
	public int getMarioMapY() {
		return mario.getMapY();
	}
	
	public float getMarioXA() {
		return mario.getXa();
	}
	
	public float getMarioYA() {
		return mario.getYa();
	}
	
	public float getMarioXCam() {
		return xCam;
	}
	
	public int getMarioFacing() {
		return mario.getFacing();
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
	
	public int getMarioCoins() {
		return mario.getCoins();
	}
	
	public boolean mayMarioJump() {
		return mario.mayJump();
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
	
	public void setMarioCheatKeys(boolean[] input) {
		mario.setCheatKeys(input);
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
	
	//--- RunnerOptions from MarioComponent
	
	public boolean isLabels() {
		return renderer.getRunnerOptions().isLabels();
	}
	
	//--- A* Help Methods
	
	public LevelScene getDeepCopy() {
		return new LevelScene(this);
	}
	
	public double getScore() {
		return getScoreBasesOnValues(mario.getStatus(), timeLeft/15, mario.getX(), killedCreaturesTotal, killedCreaturesByStomp, killedCreaturesByShell, killedCreaturesByFireBall, mario.getCoins(), mario.getGainedMushrooms(), mario.getGainedFlowers(), mario.getTimesHurt());
	}
	
	public static double getScoreBasesOnValues(STATUS marioStatus, int timeLeft, double marioX, int killsTotal, int killsByStomp,int killsByShell,int killsByFire, int collectedCoins, int collectedMuhsrooms,int collectedFlowers,int timesHurt) {
		double res=0;
		
//		System.out.println(marioStatus);
//		System.out.println("Time left: "+timeLeft/15);
//		System.out.println("Mario X: "+(int)marioX/16);
//		System.out.println(killsTotal);
//		System.out.println(killsByStomp);
//		System.out.println(killsByShell);
//		System.out.println(killsByFire);
//		System.out.println(collectedCoins);
//		System.out.println(collectedMuhsrooms);
//		System.out.println(collectedFlowers);
//		System.out.println(timesHurt);
		
		
		//---Positive 
		//--- Distance Score
		if(marioX>=0) res+=(int)marioX/16; //adding passed distance 
		
		//---Time Score
		if(timeLeft>=0)res+=timeLeft*8; // adding Points for time left
		
		//---Status Score
		if(marioStatus==STATUS.WIN) res+=1024; //one time bonus for winning
	
		//---Kill Score (violence is bad kids, don't be like mario)
		if(killsTotal>0) res+=killsTotal*42;
		if(killsByStomp>0) res+=killsByStomp*12;
		if(killsByShell>0) res+=killsByShell*17;
		if(killsByFire>0) res+=killsByFire*4;
	
		//---Collectible Score
		
		//---Coin Score 
		if(collectedCoins>=0) res+=collectedCoins*16; //money, money, money
	
		//---PowerUp Score
		if(collectedMuhsrooms>=0) res+=collectedMuhsrooms*42;
		if(collectedFlowers>=0) res+=collectedFlowers*42;

		//---Negative
		//--- Hurt Score
		if(timesHurt>=0) res-=timesHurt*42;
	
		return res;
	}

	public long getLevelSeed() {
		return levelSeed;
	}

	public void togglePaused() {
		this.paused=!paused;
	}

}