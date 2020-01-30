package ch.idsia.mario.engine.sprites;

import java.awt.Graphics;

import ch.idsia.mario.engine.LevelScene;

public class FlowerEnemy extends Enemy //cloneable
{
    private int tick;
    private int yStart;
    private int jumpTime = 0;
    private final static int yPicPreset = 6;
    private static final SpriteKind kind = SpriteKind.KIND_ENEMY_FLOWER;
    
    public FlowerEnemy(LevelScene world, int x, int y, int mapX, int mapY)
    {
        super(world, x, y, 1, false, mapX, mapY);
        this.spriteContext = world;
        this.xPic = 0;
        this.yPicO = 24;
        this.height = 12;
        this.width = 2;
        yPic = yPicPreset;
        
        yStart = y; 
        ya = -8;
        
        this.y-=1;
        
        this.layer = 0;
        
        for (int i=0; i<4; i++)
        {
            move();
        }
    }
    
    public FlowerEnemy(LevelScene alreadyCopied, FlowerEnemy toCopy) {
    	super(alreadyCopied, toCopy);
    	
    	this.tick=toCopy.tick;
    	this.yStart=toCopy.yStart;
    	this.jumpTime=toCopy.jumpTime;
    }

    public void move()
    {
        if (deadTime > 0)
        {
            deadTime--;

            if (deadTime == 0)
            {
                deadTime = 1;
                for (int i = 0; i < 8; i++)
                {
                    spriteContext.addSprite(new Sparkle(spriteContext,(int) (x + Math.random() * 16 - 8) + 4, (int) (y - Math.random() * 8) + 4, (float) (Math.random() * 2 - 1), (float) Math.random() * -1, 0, 5));
                }
                spriteContext.removeSprite(this);
            }

            x += xa;
            y += ya;
            ya *= 0.95;
            ya += 1;

            return;
        }

        tick++;
        
        if (y>=yStart)
        {
            y = yStart;

            int xd = (int)(Math.abs(spriteContext.getMarioX()-x));
            jumpTime++;
            if (jumpTime>40 && xd>24)
            {
                ya = -8;
            }
            else
            {
                ya = 0;
            }
        }
        else
        {
            jumpTime = 0;
        }
        
        y+=ya;
        ya*=0.9;
        ya+=0.1f;
        
        xPic = ((tick/2)&1)*2+((tick/6)&1);
    }

    public void collideCheck() {
        if (deadTime != 0) {
            return;
        }

        float xMarioD = spriteContext.getMarioX() - x;
        float yMarioD = spriteContext.getMarioY() - y;
        if (xMarioD > -width*2-4 && xMarioD < width*2+4) {
            if (yMarioD > -height && yMarioD < spriteContext.getMarioHeight()) {
                spriteContext.hurtMario();
            }
        }
    }
    
    public boolean fireballCollideCheck(Fireball fireball)
    {
        if (deadTime != 0) return false;

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < fireball.getHeight()) {
                xa = fireball.getFacing() * 2;
                ya = -5;
                flyDeath = true;
                
                this.dead=true;
                
                deadTime = 100;
                winged = false;
                hPic = -hPic;
                yPicO = -yPicO + 16;
                spriteContext.incrementKilledCreaturesTotal();
                spriteContext.killedCreaturesByFireBall();
                return true;
            }
        }
        return false;
    }
    
    @Override
	public SpriteKind getKind() {
		return kind;
	}
    
    public void render(Graphics og) {
    	super.render(og, kind);
    }
    
}