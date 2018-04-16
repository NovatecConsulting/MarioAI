package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;


public class Shell extends Sprite //cloneable
{
    private static final float GROUND_INERTIA = 0.89f;
    private static final float AIR_INERTIA = 0.89f;

    private boolean onGround = false;

    private static final int width = 4;
    private static final int height = 12;

    private int facing;

    private static final boolean avoidCliffs = false; //don't even ask why they put this in... (does work though)
    private int anim;

    private boolean dead = false;
    private int deadTime = 0;
    private boolean carried; //useless?


	public Shell(LevelScene world, float x, float y, int type)
    {
        kind = KIND_SHELL;
        sheet = Art.enemies;

        this.x = x;
        this.y = y;
        this.spriteContext = world;
        xPicO = 8;
        yPicO = 31;

        yPic = type;
        facing = 0;
        wPic = 16;

        xPic = 4;
        ya = -5;
    }
    
    public Shell(LevelScene alreadyCopied,Shell toCopy) {
    	super(alreadyCopied,toCopy);

    	onGround=toCopy.onGround;
    	facing=toCopy.facing;
    	anim=toCopy.anim;
    	dead=toCopy.dead;
    	deadTime=toCopy.deadTime;
    	carried=toCopy.carried;
    }
    
    public boolean fireballCollideCheck(Fireball fireball)
    {
        if (deadTime != 0) return false;

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16)
        {
            if (yD > -height && yD < fireball.getHeight())
            {
                if (facing!=0) return true;
                
                xa = fireball.getFacing() * 2;
                ya = -5;
                if (spriteTemplate != null) spriteTemplate.setDead(true); 
                deadTime = 100;
                hPic = -hPic;
                yPicO = -yPicO + 16;
                return true;
            }
        }
        return false;
    }    

    public void collideCheck()
    {
        if (isCarried() || dead || deadTime>0) return;

        float xMarioD = spriteContext.getMarioX() - x;
        float yMarioD = spriteContext.getMarioY() - y;
        if (xMarioD > -16 && xMarioD < 16)
        {
            if (yMarioD > -height && yMarioD < spriteContext.getMarioHeight())
            {
                if (spriteContext.getMarioYA()> 0 && yMarioD <= 0 && (!spriteContext.isMarioOnGround() || !spriteContext.wasMarioOnGround()))
                {
                    spriteContext.marioStomp(this);
                    if (facing != 0)
                    {
                        xa = 0;
                        facing = 0;
                    }
                    else
                    {
                        facing = spriteContext.getMarioFacing();
                    }
                }
                else
                {
                    if (facing != 0)
                    {
                        spriteContext.getMarioHurt();
                    }
                    else
                    {
                        spriteContext.marioKick(this);
                        facing = spriteContext.getMarioFacing();
                    }
                }
            }
        }
    }

    public void move()
    {
        if (isCarried())
        {
            spriteContext.checkShellCollide(this);
            return;
        }

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

        if (facing != 0) anim++;

        float sideWaysSpeed = 11f;
        //        float sideWaysSpeed = onGround ? 2.5f : 1.2f;

        if (xa > 2)
        {
            facing = 1;
        }
        if (xa < -2)
        {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        if (facing != 0)
        {
            spriteContext.checkShellCollide(this);
        }

        xFlipPic = facing == -1;

        xPic = (anim / 2) % 4 + 3;



        if (!move(xa, 0))
        {
            facing = -facing;
        }
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

    @SuppressWarnings("unused")
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

        if (blocking && ya == 0 && xa!=0)
        {
            spriteContext.bump(x, y, true);
        }
 
        return blocking;
    }

    public void bumpCheck(int xTile, int yTile)
    {
        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16))
        {
            facing = -spriteContext.getMarioFacing();
            ya = -10;
        }
    }

    public void die()
    {
        dead = true;

        setCarried(false);

        xa = -facing * 2;
        ya = -5;
        deadTime = 100;
    }

    public boolean shellCollideCheck(Shell shell)
    {
        if (deadTime != 0) return false;

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16)
        {
            if (yD > -height && yD < Shell.height)
            {
                if (spriteContext.getMarioCarried() == shell || spriteContext.getMarioCarried() == this)
                {
                    spriteContext.setMarioCarried(null);
                    }

                die();
                shell.die();
                return true;
            }
        }
        return false;
    }


    public void release(Mario mario)
    {
        setCarried(false);
        facing = mario.getFacing();
        x += facing * 8;
    }

	public static int getHeight() {
		return height;
	}

	public int getFacing() {
		return facing;
	}
	
    public boolean isDead() {
		return dead;
	}

	public boolean isCarried() { 
		return carried;
	}

	public void setCarried(boolean carried) {
		this.carried = carried;
	}
}