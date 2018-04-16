package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.level.SpriteTemplate;

import java.awt.*;

public abstract class Sprite
{
    public static final int KIND_NONE = 0;
    public static final int KIND_MARIO = -31;
    public static final int KIND_GOOMBA = 2;
    public static final int KIND_GOOMBA_WINGED = 3;
    public static final int KIND_RED_KOOPA = 4;
    public static final int KIND_RED_KOOPA_WINGED = 5;
    public static final int KIND_GREEN_KOOPA = 6;
    public static final int KIND_GREEN_KOOPA_WINGED = 7;
    public static final int KIND_BULLET_BILL = 8;
    public static final int KIND_SPIKY = 9;
    public static final int KIND_SPIKY_WINGED = 10;
//  public static final int KIND_ENEMY_FLOWER = 11;
    public static final int KIND_ENEMY_FLOWER = 12;
    public static final int KIND_SHELL = 13;
    public static final int KIND_MUSHROOM = 14;
    public static final int KIND_FIRE_FLOWER = 15;    
    public static final int KIND_PARTICLE = 21;
    public static final int KIND_SPARCLE = 22;
    public static final int KIND_COIN_ANIM = 20;
    public static final int KIND_FIREBALL = 25;

    public static final int KIND_UNDEF = -42;

    protected LevelScene spriteContext;
    protected byte kind = KIND_UNDEF;
    
    protected float xOld, yOld, x, y, xa, ya;
    protected int mapX, mapY;
    
    protected int xPic, yPic; //coords for picture
    protected int wPic = 32; //width of picture
    protected int hPic = 32;// height of picture
    protected int xPicO, yPicO;
    protected boolean xFlipPic = false;
    protected boolean yFlipPic = false;
    protected Image[][] sheet;
    protected boolean visible = true;
    
    protected int layer = 1;

    protected SpriteTemplate spriteTemplate;
    
    protected boolean isClone=false;

	public Sprite() {
    	
    }
    
    protected Sprite(LevelScene alreadyCopied,Sprite toCopy) { 
    	//spritecontext=toCopy.levelscenecopy
    	
    	this.kind = toCopy.kind;
		this.xOld = toCopy.xOld;
		this.yOld = toCopy.yOld;
		this.x = toCopy.x;
		this.y = toCopy.y;
		this.xa = toCopy.xa;
		this.ya = toCopy.ya;
		this.mapX = toCopy.mapX;
		this.mapY = toCopy.mapY;
		this.xPic = toCopy.xPic;
		this.yPic = toCopy.yPic;
		this.wPic = toCopy.wPic; 
		this.hPic = toCopy.hPic;
		this.xPicO = toCopy.xPicO;
		this.yPicO = toCopy.yPicO;
		this.xFlipPic = toCopy.xFlipPic;
		this.yFlipPic = toCopy.yFlipPic;
		this.sheet = toCopy.sheet; // does this work? just graphics, wont be changed
		this.visible = toCopy.visible;
		this.layer = toCopy.layer;
		
		this.isClone=true;
		
		
		if(toCopy.spriteTemplate!=null)this.spriteTemplate = new SpriteTemplate(this, toCopy.spriteTemplate); //needs copy constructor
		this.spriteContext=alreadyCopied;
    }
    
    @Override
	public String toString() {
		return "Sprite [kind=" + kind + ", x=" + x + ", y=" + y + ", isClone= "+isClone+"]";
	}

	public void registerSpriteContext(LevelScene spriteContext) {
    	this.spriteContext=spriteContext;
    }

	public void move()
    {
        x+=xa;
        y+=ya;
    }
    
    public void render(Graphics og)
    {
        if (!visible) return;
        
//        int xPixel = (int)(xOld+(x-xOld)*alpha)-xPicO;
//        int yPixel = (int)(yOld+(y-yOld)*alpha)-yPicO;

        int xPixel = (int)x-xPicO;
        int yPixel = (int)y-yPicO;


        og.drawImage(sheet[xPic][yPic], xPixel+(xFlipPic?wPic:0), yPixel+(yFlipPic?hPic:0), xFlipPic?-wPic:wPic, yFlipPic?-hPic:hPic, null);
        if (spriteContext.isLabels()) 
            og.drawString("" + xPixel + "," + yPixel, xPixel, yPixel);
    }
    
    public final void tick()
    {
        xOld = x;
        yOld = y;
//        mapX = (int)(xOld / 16);
//        mapY = (int)(yOld / 16);
        move();
    }

    public final void tickNoMove()
    {
        xOld = x;
        yOld = y;        
    }

    public void collideCheck()
    {
    }

    public void bumpCheck(int xTile, int yTile)
    {
    }

    public boolean shellCollideCheck(Shell shell)
    {
        return false;
    }

    public void release(Mario mario)
    {
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        return false;
    }
    
    public byte getKind() {
    	return kind;
    }

	public float getxOld() {
		return xOld;
	}

	public float getyOld() {
		return yOld;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getXa() {
		return xa;
	}

	public float getYa() {
		return ya;
	}

	public int getMapX() {
		return (int)x/16;
	}

	public int getMapY() {
		return (int)y/16;
	}

	public int getxPic() {
		return xPic;
	}

	public int getyPic() {
		return yPic;
	}

	public int gethPic() {
		return hPic;
	}

	public int getxPicO() {
		return xPicO;
	}

	public int getyPicO() {
		return yPicO;
	}

	public boolean isxFlipPic() {
		return xFlipPic;
	}

	public boolean isyFlipPic() {
		return yFlipPic;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getLayer() {
		return layer;
	}
	
    public void setSpriteTemplate(SpriteTemplate spriteTemplate) {
		this.spriteTemplate = spriteTemplate;
	}
}