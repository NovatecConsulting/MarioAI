package ch.idsia.mario.engine.level;

import java.io.*;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.BulletBill;
import ch.idsia.mario.engine.sprites.CoinAnim;
import ch.idsia.mario.engine.sprites.Enemy;
import ch.idsia.mario.engine.sprites.FireFlower;
import ch.idsia.mario.engine.sprites.Fireball;
import ch.idsia.mario.engine.sprites.FlowerEnemy;
import ch.idsia.mario.engine.sprites.Mushroom;
import ch.idsia.mario.engine.sprites.Particle;
import ch.idsia.mario.engine.sprites.Shell;
import ch.idsia.mario.engine.sprites.Sparkle;
import ch.idsia.mario.engine.sprites.Sprite;

public class Level
{
    public enum LEVEL_TYPES {
		OVERGROUND("Overground", 0), UNDERGROUND("Underground", 1), CASTLE("Castle", 2), UNKNOWN("UNKNOWN", 3);
	
		private int type;
		private String name;
	
		private LEVEL_TYPES(String name, int type) {
			this.name = name;
			this.type = type;
		}
	
		public String toString() {
			return name;
		}
	
		public int getType() {
			return type;
		}
	
		public static String getNameByType(int type) {
			return getLevelTypebyType(type).toString();
		}
	
		public static LEVEL_TYPES getLevelTypebyType(int type) {
			switch (type) {
			case 0:
				return LEVEL_TYPES.OVERGROUND;
			case 1:
				return LEVEL_TYPES.UNDERGROUND;
			case 2:
				return LEVEL_TYPES.CASTLE;
			default:
				System.err.println(
						"Unknown Type - either this is a bug or int > 2 --- Returning unkown world type!");
				return LEVEL_TYPES.UNKNOWN;
			}
		}
	}

	public static final String[] BIT_DESCRIPTIONS = {//
    "BLOCK UPPER", //
            "BLOCK ALL", //
            "BLOCK LOWER", //
            "SPECIAL", //
            "BUMPABLE", //
            "BREAKABLE", //
            "PICKUPABLE", //
            "ANIMATED",//
    };

    public static byte[] TILE_BEHAVIORS = new byte[256];

    public static final int BIT_BLOCK_UPPER = 1 << 0;
    public static final int BIT_BLOCK_ALL = 1 << 1;
    public static final int BIT_BLOCK_LOWER = 1 << 2;
    public static final int BIT_SPECIAL = 1 << 3;
    public static final int BIT_BUMPABLE = 1 << 4;
    public static final int BIT_BREAKABLE = 1 << 5;
    public static final int BIT_PICKUPABLE = 1 << 6;
    public static final int BIT_ANIMATED = 1 << 7;

    private static final int FILE_HEADER = 0x271c4178;
   
    private int width;
   	private int height;

    private byte[][] map;
    private byte[][] data;
    private byte[][] observation;
    
    public SpriteTemplate[][] spriteTemplates;

    private int xExit;
	private int yExit;
	
	private int totalCoins;
    
    
    public Level(int width, int height)
    {
        this.width = width;
        this.height = height;

        xExit = 10;
        yExit = 10;
        totalCoins=0;
        map = new byte[width][height];
        data = new byte[width][height];
        spriteTemplates = new SpriteTemplate[width][height];
        observation = new byte[width][height];
    }
    
    public Level(LevelScene alreadyCopied,Level toCopy) {
    	 this.width = toCopy.width;
         this.height = toCopy.height;
         this.xExit=toCopy.xExit;
         this.yExit=toCopy.yExit;
         this.totalCoins=toCopy.totalCoins;
         
         this.map=getDeepCopyOf(toCopy.map);
         this.data=getDeepCopyOf(toCopy.data);
         this.observation=getDeepCopyOf(toCopy.observation);
         
         this.spriteTemplates=getDeepCopyOf(alreadyCopied, toCopy.spriteTemplates);         
    }
    
    private SpriteTemplate[][] getDeepCopyOf(LevelScene alreadyCopied,SpriteTemplate[][] toCopy){
    	SpriteTemplate[][] copy=new SpriteTemplate[toCopy.length][toCopy[0].length];
    	Sprite toAdd;
    	
    	for(int i=0; i<toCopy.length;i++) {
    		for(int j=0;j<toCopy[i].length;j++) {
    			if(toCopy[i][j]==null) continue;
    			Sprite actual=toCopy[i][j].getSprite();
    			
    			if(actual instanceof Enemy &&  !(actual instanceof FlowerEnemy)) {
    				toAdd=new Enemy(alreadyCopied, (Enemy)actual);
    			}
    			else if(actual instanceof BulletBill) {
    				toAdd=new BulletBill(alreadyCopied, (BulletBill)actual);
    			}
    			else if(actual instanceof CoinAnim) {
    				toAdd=new CoinAnim(alreadyCopied, (CoinAnim)actual);
    			}
    			else if(actual instanceof Sparkle) {
    				toAdd=new Sparkle(alreadyCopied, (Sparkle)actual);
    			}
    			else if(actual instanceof Fireball) {
    				toAdd=new Fireball(alreadyCopied, (Fireball)actual);
    			}
    			else if(actual instanceof Particle) {
    				toAdd=new Particle(alreadyCopied, (Particle)actual);
    			}
    			else if(actual instanceof Shell) {
    				toAdd=new Shell(alreadyCopied, (Shell)actual);
    			}
    			else if(actual instanceof FireFlower) {
    				toAdd=new FireFlower(alreadyCopied, (FireFlower)actual);
    			}
    			else if(actual instanceof FlowerEnemy) {
    				toAdd=new FlowerEnemy(alreadyCopied, (FlowerEnemy)actual);
    			}
    			else if(actual instanceof Mushroom) {
    				toAdd=new Mushroom(alreadyCopied, (Mushroom)actual);
    			}
    			else toAdd=null;
    			copy[i][j]=new SpriteTemplate(toAdd, toCopy[i][j]);
    		}
    	}
    	
    	return copy;
    }
    
    private byte[][] getDeepCopyOf(byte[][] toCopy){
    	byte[][] copy=new byte[toCopy.length][toCopy[0].length];
    	for(int i=0; i<toCopy.length;i++) {
    		for(int j=0;j<toCopy[i].length;j++) {
    			copy[i][j]=toCopy[i][j];
    		}
    	}
    	return copy;
    }
    
    public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
    
    public byte getData(int x, int y) {
    	return data[x][y];
    }
    
    public byte getMap(int x, int y) {
    	return map[x][y];
    }
    
    public byte getObservation(int x, int y) {
    	return map[x][y];
    }
    
    public void setObservation(int x,int y,byte value) {
    	observation[x][y]=value;
    }
    
    public int getObservationXLength() {
    	return observation.length;
    }
    
    public int getObservationYLength() {
    	return observation[0].length;
    }

    public static void loadBehaviors(DataInputStream dis) throws IOException
    {
        dis.readFully(Level.TILE_BEHAVIORS);
    }

    public static void saveBehaviors(DataOutputStream dos) throws IOException
    {
        dos.write(Level.TILE_BEHAVIORS);
    }

    public static Level load(DataInputStream dis) throws IOException
    {
        long header = dis.readLong();
        if (header != Level.FILE_HEADER) throw new IOException("Bad level header");
        int width = dis.readShort() & 0xffff;
        int height = dis.readShort() & 0xffff;
        Level level = new Level(width, height);
        level.map = new byte[width][height];
        level.data = new byte[width][height];
        for (int i = 0; i < width; i++)
        {
            dis.readFully(level.map[i]);
            dis.readFully(level.data[i]);
        }
        return level;
    }

    public void save(DataOutputStream dos) throws IOException
    {
        dos.writeLong(Level.FILE_HEADER);
        dos.write((byte) 0);

        dos.writeShort((short) width);
        dos.writeShort((short) height);

        for (int i = 0; i < width; i++)
        {
            dos.write(map[i]);
            dos.write(data[i]);
        }
    }

    public void tick()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (data[x][y] > 0) data[x][y]--;
            }
        }
    }

    public byte getBlockCapped(int x, int y)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return map[x][y];
    }

    public byte getBlock(int x, int y)
    {
        if (x < 0) x = 0;
        if (y < 0) return 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return map[x][y];
    }

    public void setBlock(int x, int y, byte b)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        map[x][y] = b;
    }

    public void setBlockData(int x, int y, byte b)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        data[x][y] = b;
    }

    public boolean isBlocking(int x, int y, float xa, float ya)
    {
        byte block = getBlock(x, y);
        boolean blocking = ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_ALL) > 0;
        blocking |= (ya > 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_UPPER) > 0;
        blocking |= (ya < 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_LOWER) > 0;

        return blocking;
    }

    public SpriteTemplate getSpriteTemplate(int x, int y)
    {
        if (x < 0) return null;
        if (y < 0) return null;
        if (x >= width) return null;
        if (y >= height) return null;
        return spriteTemplates[x][y];
    }

    public void setSpriteTemplate(int x, int y, SpriteTemplate spriteTemplate)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        spriteTemplates[x][y] = spriteTemplate;
    }
    
    public int getXExit() {
		return xExit;
	}

	public int getYExit() {
		return yExit;
	}
	
	public void setXExit(int xExit) {
		this.xExit=xExit;
	}
	
	public void setYExit(int yExit) {
		this.yExit=yExit;
	}

    public double getWidthPhys() {
    	return width * 16;
    	}

	public int getTotalCoins() {
		return totalCoins;
	}

	public void setTotalCoins(int totalCoins) {
		this.totalCoins = totalCoins;
	}

	@Override
	public String toString() {
		return "Level [width=" + width + ", height=" + height + ", xExit=" + xExit +"]";
	}
}

