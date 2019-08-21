package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.LevelScene;
import java.awt.*;

public abstract class Sprite {

    protected LevelScene spriteContext;
    
    protected float xOld, yOld, x, y, xa, ya;
    protected int mapX, mapY;
    
    protected int xPic; //coords for picture
    protected int yPic;
    protected int wPic = 32; //width of picture
    protected int hPic = 32;// height of picture
    protected int xPicO, yPicO;
    protected boolean xFlipPic = false;
    protected boolean yFlipPic = false;
    protected Image[][] sheet;
    protected boolean visible = true;
    
    protected int layer = 1;

    protected boolean dead=false;

    protected boolean isClone=false;

	protected Sprite() { //got to stay
    	
    }
    
    protected Sprite(LevelScene alreadyCopied,Sprite toCopy) { 
    	
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
		this.sheet = toCopy.sheet; // does this work?(yes) just graphics, wont be changed
		this.visible = toCopy.visible;
		this.layer = toCopy.layer;
		this.dead=toCopy.dead;
		
		this.isClone=true;
	
		//if(toCopy.spriteTemplate!=null)this.spriteTemplate = new SpriteTemplate(this, toCopy.spriteTemplate); //needs copy constructor
		this.spriteContext=alreadyCopied;
    }
    
    @Override
	public String toString() {
		return "Sprite [x=" + x + ", y=" + y + ", isClone= "+isClone+"]";
	}

	public void registerSpriteContext(LevelScene spriteContext) {
    	this.spriteContext=spriteContext;
    }

	public void move() {
        x+=xa;
        y+=ya;
    }
    
    public void render(Graphics og) {
        if (!visible) return;

        int xPixel = (int)x-xPicO;
        int yPixel = (int)y-yPicO;

        og.drawImage(sheet[xPic][yPic], xPixel+(xFlipPic?wPic:0), yPixel+(yFlipPic?hPic:0), xFlipPic?-wPic:wPic, yFlipPic?-hPic:hPic, null);
    }
    
    public final void tick() {
        xOld = x;
        yOld = y;
        move();
    }

    public final void tickNoMove() {
        xOld = x;
        yOld = y;        
    }

    public void collideCheck() {
    }

    public void bumpCheck(int xTile, int yTile) {
    }

    public boolean shellCollideCheck(Shell shell) {
        return false;
    }

    public void release(Mario mario) {
    }

    public boolean fireballCollideCheck(Fireball fireball) {
        return false;
    }
    
//    public SpriteKind getKind() {
//    	return kind;
//    }

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

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public abstract SpriteKind getKind();
	
//    public void setSpriteTemplate(SpriteTemplate spriteTemplate) {
//		this.spriteTemplate = spriteTemplate;
//	}
}