package de.novatec.mario.engine.generalization;

import de.novatec.mario.engine.generalization.Tiles.TileType;

public class Tile {
	
	Coordinates coords;
	TileType type;

	public Tile(Coordinates coords, TileType type) {
		super();
		this.coords = coords;
		this.type = type;
	}

	public Coordinates getCoords() {
		return coords;
	}

	public TileType getType() {
		return type;
	}
	
	public float getX() {
		return this.coords.getX();
	}
	
	public float getY() {
		return this.coords.getY();
	}
	
	public String toString() {
		return "Tile --- Type: "+type+" Coordinates: "+coords;
	}


}
