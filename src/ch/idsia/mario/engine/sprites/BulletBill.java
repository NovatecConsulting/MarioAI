package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;


public class BulletBill extends Sprite //cloneable
{
    private static final int height = 12;

    private int facing;

    private boolean dead = false;
    private int deadTime = 0;


    public BulletBill(LevelScene world, float x, float y, int dir)
    {
        kind = KIND_BULLET_BILL;
        sheet = Art.enemies;

        this.x = x;
        this.y = y;
        this.spriteContext = world;
        xPicO = 8;
        yPicO = 31;

        facing = 0;
        wPic = 16;
        yPic = 5;

        xPic = 0;
        ya = -5;
        this.facing = dir;
    }
    
    public BulletBill(LevelScene alreadyCopied,BulletBill toCopy) {
    	super(alreadyCopied, toCopy);
    	
    	this.facing=toCopy.facing;
    	this.dead=toCopy.dead;
    	this.deadTime=toCopy.deadTime;
    }

    public void collideCheck()
    {
        if (dead) return;

        float xMarioD = spriteContext.getMarioX() - x;
        float yMarioD = spriteContext.getMarioY() - y;
        if (xMarioD > -16 && xMarioD < 16)
        {
            if (yMarioD > -getHeight() && yMarioD < spriteContext.getMarioHeight())
            {
                if (spriteContext.getMarioYA() > 0 && yMarioD <= 0 && (!spriteContext.isMarioOnGround() || !spriteContext.wasMarioOnGround()))
                {
                    spriteContext.marioStomp(this);
                    dead = true;

                    xa = 0;
                    ya = 1;
                    deadTime = 100;
                }
                else
                {
                    spriteContext.getMarioHurt();
                }
            }
        }
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
                    spriteContext.addSprite(new Sparkle(spriteContext,(int) (x + Math.random() * 16 - 8) + 4, (int) (y - Math.random() * 8) + 4, (float) (Math.random() * 2 - 1), (float) Math.random() * -1, 0, 1, 5));
                }
                spriteContext.removeSprite(this);
            }

            x += xa;
            y += ya;
            ya *= 0.95;
            ya += 1;

            return;
        }

        float sideWaysSpeed = 4f;

        xa = facing * sideWaysSpeed;
        xFlipPic = facing == -1;
        move(xa, 0);
    }

    private boolean move(float xa, float ya)
    {
        x += xa;
        return true;
    }
    
    public boolean fireballCollideCheck(Fireball fireball)
    {
        if (deadTime != 0) return false;

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16)
        {
            if (yD > -getHeight() && yD < fireball.getHeight())
            {
                return true;
            }
        }
        return false;
    }      

    public boolean shellCollideCheck(Shell shell)
    {
        if (deadTime != 0) return false;

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16)
        {
            if (yD > -getHeight() && yD < Shell.getHeight())
            {
                dead = true;

                xa = 0;
                ya = 1;
                deadTime = 100;

                return true;
            }
        }
        return false;
    }

	public static int getHeight() {
		return height;
	}      
}