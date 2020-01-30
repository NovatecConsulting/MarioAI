package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;

public class BrickAnim extends Sprite //cloneable
{
    private int life = 16;
    private static final SpriteKind kind = SpriteKind.KIND_BRICK_ANIM;
    
    private static final int yPicPreset = 1;

    public BrickAnim(LevelScene world,int x, int y, float xa, float ya)
    {
        this(world,x, y, xa, ya, (int)(Math.random()*2));
    }

    public BrickAnim(LevelScene world,int x, int y, float xa, float ya, int xPic)
    {
    	this.spriteContext=world;
        sheet = Art.particles;
        this.x = x;
        this.y = y;
        this.xa = xa;
        this.ya = ya;
        this.xPic = xPic;
        this.yPic = yPicPreset;
        this.xPicO = 4;
        this.yPicO = 4;
        
        wPic = 8;
        hPic = 8;
        life = 10;
    }
    
    public BrickAnim(LevelScene alreadyCopied,BrickAnim toCopy) {
    	super(alreadyCopied,toCopy);
    	
    	this.spriteContext=alreadyCopied;
    	this.life=toCopy.life;
    }

    public void move()
    {
        if (life--<0) this.spriteContext.removeSprite(this);
        x+=xa;
        y+=ya;
        ya*=0.95f;
        ya+=3;
    }
    
    @Override
	public SpriteKind getKind() {
		return kind;
	}
}