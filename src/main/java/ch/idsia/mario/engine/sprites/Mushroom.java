package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;


public class Mushroom extends Sprite //cloneable
{
    private static float GROUND_INERTIA = 0.89f;
    private static float AIR_INERTIA = 0.89f;

    private boolean onGround = false;
    
    private static final int width = 4;
    private static final int height = 12;

    private int facing;

    private boolean avoidCliffs = false;
    private int life;

    public Mushroom(LevelScene world, int x, int y)
    {
        kind = KIND_MUSHROOM;
        sheet = Art.items;

        this.x = x;
        this.y = y;
        this.spriteContext = world;
        xPicO = 8;
        yPicO = 15;

        yPic = 0;
        facing = 1;
        wPic  = hPic = 16;
        life = 0;
    }
    
    public Mushroom(LevelScene alreadyCopied,Mushroom toCopy) {
    	super(alreadyCopied,toCopy);

    	onGround=toCopy.onGround;
    	facing=toCopy.facing;
    	avoidCliffs=toCopy.avoidCliffs;
    	life=toCopy.life;
    	
    }
    
    public void collideCheck()
    {
        float xMarioD = spriteContext.getMarioX() - x;
        float yMarioD = spriteContext.getMarioY() - y;
        if (xMarioD > -16 && xMarioD < 16)
        {
            if (yMarioD > -height && yMarioD < spriteContext.getMarioHeight())
            {
                spriteContext.getMarioMushroom();;
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
        float sideWaysSpeed = 1.75f;
        layer = 1;

        if (xa > 2)
        {
            facing = 1;
        }
        if (xa < -2)
        {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        xFlipPic = facing == -1;

        if (!move(xa, 0)) facing = -facing;
        onGround = false;
        move(0, ya);

        ya *= 0.85f;
        if (onGround)
        {
            xa *= GROUND_INERTIA;
        }
        else
        {
            xa *= AIR_INERTIA;
        }

        if (!onGround)
        {
            ya += 2;
        }
    }

    private boolean move(float xa, float ya)
    {
        while (xa > 8)
        {
            if (!move(8, 0)) return false;
            xa -= 8;
        }
        while (xa < -8)
        {
            if (!move(-8, 0)) return false;
            xa += 8;
        }
        while (ya > 8)
        {
            if (!move(0, 8)) return false;
            ya -= 8;
        }
        while (ya < -8)
        {
            if (!move(0, -8)) return false;
            ya += 8;
        }

        boolean collide = false;
        if (ya > 0)
        {
            if (isBlocking(x + xa - width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, xa, ya)) collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, xa, ya)) collide = true;
        }
        if (ya < 0)
        {
            if (isBlocking(x + xa, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
        }
        if (xa > 0)
        {
            if (isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
            if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya)) collide = true;
            if (isBlocking(x + xa + width, y + ya, xa, ya)) collide = true;

            if (avoidCliffs && onGround && !spriteContext.levelIsBlocking((int) ((x + xa + width) / 16), (int) ((y) / 16 + 1), xa, 1)) collide = true;
        }
        if (xa < 0)
        {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya, xa, ya)) collide = true;

            if (avoidCliffs && onGround && !spriteContext.levelIsBlocking((int) ((x + xa - width) / 16), (int) ((y) / 16 + 1), xa, 1)) collide = true;
        }

        if (collide)
        {
            if (xa < 0)
            {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            if (xa > 0)
            {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            if (ya < 0)
            {
                y = (int) ((y - height) / 16) * 16 + height;
                this.ya = 0;
            }
            if (ya > 0)
            {
                y = (int) (y / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        }
        else
        {
            x += xa;
            y += ya;
            return true; 
        }
    }

    private boolean isBlocking(float _x, float _y, float xa, float ya)
    {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) return false;

        boolean blocking = spriteContext.levelIsBlocking(x, y, xa, ya);

        spriteContext.levelGetBlock(x, y);

        return blocking;
    }

    public void bumpCheck(int xTile, int yTile)
    {
        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile==(int)((y-1)/16))
        {
            facing = -spriteContext.getMarioFacing();
            ya = -10;
        }
    }

}