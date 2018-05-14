package ch.idsia.mario.engine.level;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Enemy;
import ch.idsia.mario.engine.sprites.FlowerEnemy;
import ch.idsia.mario.engine.sprites.Sprite;

public class SpriteTemplate {
    private int lastVisibleTick = -1;
	private Sprite sprite;
    private boolean isDead = false;
    private boolean winged;
   
    private int type;
    
    public SpriteTemplate(int type, boolean winged) {
        this.type = type;
        this.winged = winged;
    }
    
    public SpriteTemplate(Sprite alreadyCopied,SpriteTemplate toCopy) {
    	this.type=toCopy.type;
    	this.sprite=alreadyCopied; //TODO LOOP,could (definitly will) cause problems! //fixed?
    	this.lastVisibleTick=toCopy.lastVisibleTick;
    	this.isDead=toCopy.isDead;
    	this.winged=toCopy.winged;
    }
    
    public void spawn(LevelScene world, int x, int y, int dir) {
        if (isDead) return;

        if (type==Enemy.ENEMY_FLOWER) {
            sprite = new FlowerEnemy(world, x*16+15, y*16+24, x, y);
        }
        else {
            sprite = new Enemy(world, x*16+8, y*16+15, dir, type, winged, x, y);
        }
        sprite.setSpriteTemplate(this);
        world.addSprite(sprite);
    }
    
    public int getLastVisibleTick() {
		return lastVisibleTick;
	}
    
    public void setLastVisinleTick(int lastVisbileTick) {
    	this.lastVisibleTick=lastVisbileTick;
    }

	public Sprite getSprite() {
		return sprite;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public boolean isWinged() {
		return winged;
	}

    public int getType() {
        return type;
    }

}