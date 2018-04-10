package de.novatec.mario.engine.generalization;

import de.novatec.mario.engine.generalization.Entities.EntityType;

public class Entity{
	private EntityType type;
	
	private Coordinates coords;
	
	public Entity(EntityType type,Coordinates coords) {
		this.type=type;
		this.coords=coords;
	}
	
	public EntityType getType() {
		return type;
	}
	
	public Coordinates getCoordinates() {
		return coords;
	}
	
	public float getRelX() {
		return coords.getX();
	}
	
	public float getRelY() {
		return coords.getY();
	}

	public boolean isDangerous() {
		return type.isDangerous();
	}
	
	public boolean isCollectable() {
		return type.isCollectable();
	}
	
	public boolean isSquishable() {
		return type.isSquishable();
	}
	
	public boolean isShootable() {
		return type.isShootable();
	}
	
	public boolean isWinged() {
		return type.isWinged();
	}
	
	public int getDangerLevel() {
		return type.getDangerLevel();
	}
	
	public String toString() {
		return "Entity -- Type: "+type+" Coordinates: "+coords;
	}

	
}
