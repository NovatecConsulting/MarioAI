package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;

import java.awt.*;


public abstract class Enemy extends Sprite {//cloneable

    protected static final float GROUND_INERTIA = 0.89f;
    protected static final float AIR_INERTIA = 0.89f;

    protected float runTime;
    protected boolean onGround = false;
    protected int width = 4;
    protected int height;

    protected int facing;
    protected int deadTime = 0;
    protected boolean flyDeath = false;

    protected boolean winged;
    protected int wingTime = 0;
	
    public Enemy(LevelScene world, int x, int y, int dir, boolean winged, int mapX, int mapY) {

        sheet = Art.enemies;
        this.winged = winged;

        this.x = x;
        this.y = y;
        
        this.spriteContext = world;
        xPicO = 8;
        yPicO = 31;

        if (yPic > 1) height = 12;
        facing = dir;
        if (facing == 0) facing = 1;
        this.wPic = 16;
    }
    
    public Enemy(LevelScene alreadyCopied,Enemy toCopy) {
    	super(alreadyCopied,toCopy);
    	
    	this.runTime = toCopy.runTime;
		this.onGround = toCopy.onGround;
		this.width = toCopy.width;
		this.height = toCopy.height;
		this.facing = toCopy.facing;
		this.deadTime = toCopy.deadTime;
		this.flyDeath = toCopy.flyDeath;
		this.winged = toCopy.winged;
		this.wingTime = toCopy.wingTime;
		this.yPic = toCopy.yPic;
    }

	public void collideCheck() {
    }
	
    public void move() {
        wingTime++;
        if (deadTime > 0) {
            deadTime--;

            if (deadTime == 0) {
                deadTime = 1;
                for (int i = 0; i < 8; i++) {
                    spriteContext.addSprite(new Sparkle(spriteContext,(int) (x + Math.random() * 16 - 8) + 4, (int) (y - Math.random() * 8) + 4, (float) (Math.random() * 2 - 1), (float) Math.random() * -1, 0, 5));
                }
                spriteContext.removeSprite(this);
            }

            if (flyDeath)
            {
                x += xa;
                y += ya;
                ya *= 0.95;
                ya += 1;
            }
            return;
        }


        float sideWaysSpeed = 1.75f;
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

        xFlipPic = facing == -1;

        runTime += (Math.abs(xa)) + 5;

        int runFrame = ((int) (runTime / 20)) % 2;

        if (!onGround)
        {
            runFrame = 1;
        }


        if (!move(xa, 0)) facing = -facing;
        onGround = false;
        move(0, ya);

        ya *= winged ? 0.95f : 0.85f;
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
            if (winged)
            {
                ya += 0.6f;
            }
            else
            {
                ya += 2;
            }
        }
        else if (winged)
        {
            ya = -10;
        }

        if (winged) runFrame = wingTime / 4 % 2;

        xPic = runFrame;
    }

    private boolean move(float xa, float ya)
    {
        return false;
    }

    protected boolean isBlocking(float _x, float _y, float xa, float ya)
    {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) return false;

        boolean blocking = spriteContext.levelIsBlocking(x, y, xa, ya);

        spriteContext.levelGetBlock(x, y);

        return blocking;
    }

    public boolean shellCollideCheck(Shell shell)
    {
        if (deadTime != 0) return false;

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16)
        {
            if (yD > -height && yD < Shell.getHeight())
            {
                xa = shell.getFacing() * 2;
                ya = -5;
                flyDeath = true;
                this.dead=true;
                deadTime = 100;
                winged = false;
                hPic = -hPic;
                yPicO = -yPicO + 16;
                
                spriteContext.incrementKilledCreaturesTotal();
                spriteContext.killedCreaturesByShell();
                return true;
            }
        }
        return false;
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        return false;
    }

    public void bumpCheck(int xTile, int yTile)
    {
        if (deadTime != 0) return;

        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16))
        {
            xa = -spriteContext.getMarioFacing() * 2;
            ya = -5;
            flyDeath = true;
            
            this.dead=true;
            
            deadTime = 100;
            winged = false;
            hPic = -hPic;
            yPicO = -yPicO + 16;
        }
    }

    public void render(Graphics og, SpriteKind kind)
    {
        if (winged)
        {
            int xPixel = (int) xOld - xPicO;
            int yPixel = (int) yOld - yPicO;

            xFlipPic = !xFlipPic;
            og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 8, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
            xFlipPic = !xFlipPic;
        }

        super.render(og);

        if (winged)
        {
            int xPixel = (int) xOld - xPicO;
            int yPixel = (int) yOld - yPicO;

            if (kind == SpriteKind.KIND_GREEN_KOOPA || kind == SpriteKind.KIND_RED_KOOPA)
            {
                og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 10, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
            }
            else
            {
                og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 8, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
            }
        }
    }

}