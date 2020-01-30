package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;

public class Sparkle extends Sprite //cloneable
{
    private int life;
    private int xPicStart;
    
    private final static int yPicPreset = 1;
    private static final SpriteKind kind = SpriteKind.KIND_SPARCLE;
    
    public Sparkle(LevelScene world,int x, int y, float xa, float ya)
    {
        this(world,x, y, xa, ya, (int)(Math.random()*2), 5);
        yPic = yPicPreset;
    }

    public Sparkle(LevelScene world,int x, int y, float xa, float ya, int xPic, int timeSpan)
    {
    	this.spriteContext=world;
        sheet = Art.particles;
        this.x = x;
        this.y = y;
        this.xa = xa;
        this.ya = ya;
        this.xPic = xPic;
        this.yPic = yPic;
        xPicStart = xPic;
        this.xPicO = 4;
        this.yPicO = 4;
        
        wPic = 8;
        hPic = 8;
        life = 10+(int)(Math.random()*timeSpan);
        
        yPic = yPicPreset;
    }
    
    public Sparkle(LevelScene alreadyCopied,Sparkle toCopy) {
    	super(alreadyCopied,toCopy);
    	
    	this.life=toCopy.life;
    	this.xPicStart=toCopy.xPicStart;
    	yPic = yPicPreset;
    }

    public void move()
    {
        if (life>10)
            xPic = 7;
        else
            xPic = xPicStart+(10-life)*4/10;
        
        if (life--<0) this.spriteContext.removeSprite(this);
        
        x+=xa;
        y+=ya;
    }
    
    @Override
	public SpriteKind getKind() {
		return kind;
	}
}