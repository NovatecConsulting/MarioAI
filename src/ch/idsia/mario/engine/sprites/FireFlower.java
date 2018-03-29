package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;


public class FireFlower extends Sprite //cloneable
{
    private static final int height = 12;

    private int life;

    public FireFlower(LevelScene world, int x, int y)
    { 
    	this.spriteContext=world;
        kind = KIND_FIRE_FLOWER;
        sheet = Art.items;

        this.x = x;
        this.y = y;
        xPicO = 8;
        yPicO = 15;

        xPic = 1;
        yPic = 0;
        wPic  = hPic = 16;
        life = 0;
    }
    
    public FireFlower(LevelScene alreadyCopied, FireFlower toCopy) {
    	super(alreadyCopied, toCopy);
    	
    	this.life=toCopy.life;
    }

    public void collideCheck()
    {
        float xMarioD = spriteContext.getMarioX() - x;
        float yMarioD = spriteContext.getMarioY() - y;
        if (xMarioD > -16 && xMarioD < 16)
        {
            if (yMarioD > -height && yMarioD < spriteContext.getMarioHeight())
            {
                spriteContext.getMarioFlower();
                spriteContext.removeSprite(this);
            }
        }
    }

    public void move()
    {
        if (life<9)
        {
            layer = 0;
            y--;
            life++;
            return;
        }
    }
}