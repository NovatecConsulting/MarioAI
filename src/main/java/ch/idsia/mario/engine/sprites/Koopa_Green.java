package ch.idsia.mario.engine.sprites;

import java.awt.Graphics;

import ch.idsia.mario.engine.LevelScene;

public class Koopa_Green extends Koopa {
	// can die by fireball and be stomped --> drops a green shell
	
	private final static int yPicPreset = 1;
	private static final SpriteKind kind = SpriteKind.KIND_GREEN_KOOPA;
	
	public Koopa_Green(LevelScene alreadyCopied, Koopa_Green toCopy) {
		super(alreadyCopied, toCopy);
		height = 24;
		yPic = yPicPreset;
	}

	public Koopa_Green(LevelScene world, int x, int y, int dir, boolean winged, int mapX, int mapY) {
		super(world, x, y, dir, winged, mapX, mapY);
		height = 24;
		yPic = yPicPreset;
	}
	
	public void collideCheck() {
        if (deadTime != 0) {
            return;
        }

        float xMarioD = spriteContext.getMarioX() - x;
        float yMarioD = spriteContext.getMarioY() - y;
        if (xMarioD > -width*2-4 && xMarioD < width*2+4) {
            if (yMarioD > -height && yMarioD < spriteContext.getMarioHeight()) {
                if (spriteContext.getMarioYA() > 0 && yMarioD <= 0 && (!spriteContext.isMarioOnGround() || !spriteContext.wasMarioOnGround())) {
                    spriteContext.marioStomp(this);
                    if (winged) {
                        winged = false;
                        ya = 0;
                    }
                    else {
                        this.yPicO = 31 - (32 - 8);
                        hPic = 8;
                        this.dead=true;
                        deadTime = 10;
                        winged = false;

                        spriteContext.addSprite(new Shell_Green(spriteContext, x, y));
                        
//                      System.out.println("collideCheck and stomp");
                        spriteContext.incrementKilledCreaturesTotal();
                        spriteContext.killedCreatureByStomp();;
                    }
                }
                else {
                    spriteContext.hurtMario();
                }
            }
        }
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

        }
        if (xa < 0)
        {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya, xa, ya)) collide = true;

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
	
	@Override
	public SpriteKind getKind() {
		return kind;
	}
    
    public void render(Graphics og) {
    	super.render(og, kind);
    }
	
}
