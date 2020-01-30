package de.novatec.mario.engine.generalization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.SpriteKind;

public class Entities {

	LevelScene scene;
	
	public Entities(LevelScene scene) {
		this.scene=scene;
	}
	
	public void setLevelScene(LevelScene scene) {
		this.scene=scene;
	}
	
	public List<Entity> getEntitiesAt(int x,int y){		
		List<Entity> tmp=scene.getEntities().get(new Coordinates(x,y));
		if(tmp==null) return new ArrayList<>();
		return tmp;
	}
	
	public List<Entity> getEntitiesOnScreen(){
		List<Entity> res=new ArrayList<>();
		
		for(Map.Entry<Coordinates, List<Entity>> entry:scene.getEntities().entrySet()) {
			for(Entity e:entry.getValue()) {
				res.add(e);
			} 
		}
		
		return res;
	}
	
	public List<Entity> getEnemiesAt(int x,int y){
		List<Entity> tmp=getEntitiesOnScreen(),res=new ArrayList<>();
		
		for(Entity e:tmp) {
			if(e.isDangerous()) res.add(e); 
		}
		return res;
	}
	
	public List<Entity> getEnemiesOnScreen(){
		List<Entity> res=new ArrayList<>();
		
		for(Entity next: getEntitiesOnScreen()) {
			if(next.isDangerous()) res.add(next);
		}
		
		return res;
	}
	
	public List<Entity> getCollectiblesAt(int x, int y){
		List<Entity> res=new ArrayList<>();
		for(Entity e:getEntitiesAt(x, y)) if(e.isCollectable()) res.add(e); 
		
		return res;
	}
	
	public List<Entity> getCollectiblesOnScreen(){
		List<Entity> tmp=getEntitiesOnScreen(),res=new ArrayList<>();
		
		for(Entity e:tmp) {
			if(e.isCollectable()) res.add(e); 
		}
		return res;
	}
	
	public EntityType getMostDangrousEntityTypeAt(int x,int y) {
		List<Entity> tmp=getEntitiesAt(x, y);

		if(tmp.isEmpty()) return EntityType.NONE;
		if(tmp.size()==1) return tmp.get(0).getType();
		Entity mostDangerous=null;
		for(Entity e:tmp) {
			if(mostDangerous==null) mostDangerous=e;
			else if(mostDangerous.getDangerLevel()<e.getDangerLevel()) mostDangerous=e;
		}
		return mostDangerous.getType();
	}
	
	public double getDistance(Entity entity) {
		return Math.sqrt(Math.pow(entity.getRelX(), 2)+Math.pow(entity.getRelY(), 2));
	}

	public boolean isNothingAt (int x,int y) {
		return getEntitiesAt(x, y).isEmpty();
	}
	
	public boolean isSomethingAt (int x, int y) {
		return !isNothingAt(x, y);
	}
	
	public boolean isDangerousAt(int x, int y) {
		return getMostDangrousEntityTypeAt(x, y).isDangerous();
	}
	
	public boolean isSquishableAt(int x, int y) {
		return getMostDangrousEntityTypeAt(x, y).isSquishable();
	}
	
	public boolean isShootableAt(int x, int y) {
		return getMostDangrousEntityTypeAt(x, y).isShootable();
	}
	
	public boolean isCollectableAt(int x,int y) {
		List<Entity> tmp=getEntitiesAt(x, y);

		if(tmp.isEmpty()) return false;
		boolean collectible=false;
		for(Entity e:tmp) {
			if(e.isDangerous()) return false;
			if(e.isCollectable()) collectible=true;
		}
		return collectible;
	}
	
	public String toString() {
		return scene.getEntities().toString();
	}
	
	public enum EntityType{ 
		NONE(EntityKind.NONE,false), 
		UNKNOWN(EntityKind.UNKNOWN,false),
		GOOMBA(EntityKind.SHOOTABLE_SQUISHABLE,false),
		GOOMBA_WINGED(EntityKind.SHOOTABLE_SQUISHABLE,true),
		RED_KOOPA(EntityKind.SHOOTABLE_SQUISHABLE,false),
		RED_KOOPA_WINGED(EntityKind.SHOOTABLE_SQUISHABLE,true),
		GREEN_KOOPA(EntityKind.SHOOTABLE_SQUISHABLE,false),
		GREEN_KOOPA_WINGED(EntityKind.SHOOTABLE_SQUISHABLE,true),
		BULLET_BILL(EntityKind.SQUISHABLE,false),
		SPIKY(EntityKind.INVINCIBLE,false),
		SPIKY_WINGED(EntityKind.INVINCIBLE,true),
		ENEMY_FLOWER(EntityKind.SHOOTABLE,false),
		SHELL(EntityKind.SHOOTABLE_SQUISHABLE,false),
		MUSHROOM(EntityKind.COLLECTIBLE,false),
		FIRE_FLOWER(EntityKind.COLLECTIBLE,false),
		FIREBALL(EntityKind.SAFE,false);
		
		private EntityKind kind;
		
		private boolean winged;
		
		private EntityType (EntityKind kind, boolean winged) {
			this.kind=kind;
			this.winged=winged;
		}
		
		public boolean isWinged() {
			return this.winged;
		}
		
		public boolean isDangerous() {
			return this.kind.dangerous;
		}
		
		public boolean isSquishable() {
			return this.kind.squishy;
		}
		
		public boolean isShootable() {
			return this.kind.shootable;
		}
		
		public boolean isCollectable() {
			return this.kind.collectible;
		}
		
		public int getDangerLevel() {
			return this.kind.dangerLevel;
		}

		public static EntityType getKindByType(SpriteKind type) {
		switch(type) {
  		    case KIND_COIN_ANIM: 
            case KIND_SPARCLE:
            case KIND_MARIO:
          	  return EntityType.NONE;

            case KIND_GOOMBA: 
            	return EntityType.GOOMBA; 
            case KIND_RED_KOOPA:
            	return EntityType.RED_KOOPA;
            case KIND_GREEN_KOOPA:
            	return EntityType.GREEN_KOOPA;
            case KIND_BULLET_BILL:
            	return EntityType.BULLET_BILL;
            case KIND_SPIKY:
            	return EntityType.SPIKY;
            case KIND_ENEMY_FLOWER:
            	return EntityType.ENEMY_FLOWER;
            case KIND_SHELL:
            	return EntityType.SHELL;
            case KIND_MUSHROOM:
            	return EntityType.MUSHROOM;
            case KIND_FIRE_FLOWER:
            	return EntityType.FIRE_FLOWER;
            case KIND_FIREBALL:
            	return EntityType.FIREBALL;
            default: return EntityType.UNKNOWN;
            
  			}
		}
	}
	
	public enum EntityKind{
		NONE(0,false,false,false,false),
		UNKNOWN(2,true,true,true,false),
		INVINCIBLE(6,true,false,false,false),
		SHOOTABLE(5,true,true,false,false),
		SQUISHABLE(4,true,false,true,false),
		SHOOTABLE_SQUISHABLE(3,true,true,true,false),
		COLLECTIBLE(1,false,false,false,true),
		SAFE(0,false,false,false,false);
		
		private EntityKind(int dangerLevel, boolean dangerous, boolean shootable, boolean squishy,boolean collectible) {
			this.dangerLevel = dangerLevel;
			this.dangerous = dangerous;
			this.shootable = shootable;
			this.squishy = squishy;
			this.collectible = collectible;
		}
		
		public static final EntityKind MOST_DANGEROUS=EntityKind.INVINCIBLE, LEAST_DANGEROUS=EntityKind.SAFE; //pure information, no use, could be deleted
		
		private int dangerLevel;
		
		private boolean dangerous,shootable,squishy,collectible;
		
		public int getDangerLevel() {
			return dangerLevel;
		}

		public boolean isDangerous() {
			return dangerous;
		}

		public boolean isShootable() {
			return shootable;
		}

		public boolean isSquishy() {
			return squishy;
		}

		public boolean isCollectible() {
			return collectible;
		} 
	}
	}
