package ch.idsia.mario.engine.level;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.BulletBill;
import ch.idsia.mario.engine.sprites.CoinAnim;
import ch.idsia.mario.engine.sprites.FlowerEnemy;
import ch.idsia.mario.engine.sprites.Goomba;
import ch.idsia.mario.engine.sprites.Koopa_Green;
import ch.idsia.mario.engine.sprites.Koopa_Red;
import ch.idsia.mario.engine.sprites.Spiky;
import ch.idsia.mario.engine.sprites.Sprite;
import ch.idsia.mario.engine.sprites.SpriteKind;

public class SpriteTemplate {
    private int lastVisibleTick = -1;
	private Sprite sprite;
    private boolean winged;
   
    private SpriteKind type;
    
    public SpriteTemplate(SpriteKind type, boolean winged) {
        this.type = type;
        this.winged = winged;
    }
    
    public SpriteTemplate(Sprite alreadyCopied,SpriteTemplate toCopy) {
    	this.type=toCopy.type;
    	this.sprite=alreadyCopied;
    	this.lastVisibleTick=toCopy.lastVisibleTick;
    	this.winged=toCopy.winged;
    }
    
    public void spawn(LevelScene world, int x, int y, int dir) {
        if (sprite!=null && isDead()) return; // sprite won't be respawned after death
        
		    switch(type) {
			    case KIND_ENEMY_FLOWER:
			    	sprite = new FlowerEnemy(world, x*16+15, y*16+24, x, y);
			    	break;
			    case KIND_RED_KOOPA:
			    	sprite = new Koopa_Red(world, x*16+8, y*16+15, dir, winged, x, y);
			    	break;
			    case KIND_GREEN_KOOPA:
			    	sprite = new Koopa_Green(world, x*16+8, y*16+15, dir, winged, x, y);
			    	break;
			    case KIND_GOOMBA:
			    	sprite = new Goomba(world, x*16+8, y*16+15, dir, winged, x, y);
			    	break;
			    case KIND_SPIKY:
			    	sprite = new Spiky(world, x*16+8, y*16+15, dir, winged, x, y);
			    	break;
				case KIND_BULLET_BILL:
					sprite = new BulletBill(world, x*16+8, y*16+15, dir);
					break;
				default: 
					break;
		    }
        world.addSprite(sprite);
    }
    
    public int getLastVisibleTick() {
		return lastVisibleTick;
	}
    
    public void setLastVisibleTick(int lastVisibleTick) {
    	this.lastVisibleTick=lastVisibleTick;
    }

	public Sprite getSprite() {
		return sprite;
	}

	public boolean isDead() {
		return sprite.isDead();
	}

	public boolean isWinged() {
		return winged;
	}

    public SpriteKind getType() {
        return type;
    }

}